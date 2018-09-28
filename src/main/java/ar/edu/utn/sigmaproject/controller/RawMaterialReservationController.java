/*
 * The MIT License
 *
 * Copyright (C) 2017 SigmaProject.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.MaterialRequirement;
import ar.edu.utn.sigmaproject.domain.MaterialReserved;
import ar.edu.utn.sigmaproject.domain.Wood;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;
import ar.edu.utn.sigmaproject.service.WoodRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class RawMaterialReservationController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Window rawMaterialReservationWindow;
	@Wire
	Grid rawMaterialReservationGrid;
	@Wire
	Textbox woodTypeTextbox;
	@Wire
	Textbox descriptionTextbox;
	@Wire
	Doublebox stockDoublebox;
	@Wire
	Doublebox stockAvailableDoublebox;
	@Wire
	Doublebox stockReservedDoublebox;
	@Wire
	Doublebox quantityDoublebox;
	@Wire
	Doublebox stockMissingDoublebox;
	@Wire
	Button saveButton;
	@Wire
	Button cancelButton;
	@Wire
	Button resetButton;
	@Wire
	Button completeButton;

	// services
	@WireVariable
	private WoodRepository woodRepository;
	@WireVariable
	private ProductionPlanRepository productionPlanRepository;

	// attributes
	private MaterialRequirement currentRawMaterialRequirement;
	private Wood currentWood;
	private MaterialReserved currentWoodReserved;

	// list

	// list models

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentRawMaterialRequirement = (MaterialRequirement) Executions.getCurrent().getArg().get("selected_raw_material_requirement");
		if(currentRawMaterialRequirement == null) {throw new RuntimeException("RawMaterialRequirement null");}
		currentWood = (Wood) currentRawMaterialRequirement.getItem();
		currentWoodReserved = getMaterialReserved(currentRawMaterialRequirement);
		refreshView();
	}

	private void refreshView() {
		woodTypeTextbox.setDisabled(true);
		descriptionTextbox.setDisabled(true);
		stockDoublebox.setDisabled(true);
		stockReservedDoublebox.setDisabled(false);
		stockMissingDoublebox.setDisabled(true);
		quantityDoublebox.setDisabled(true);
		stockAvailableDoublebox.setDisabled(true);
		descriptionTextbox.setText(((Wood) currentRawMaterialRequirement.getItem()).getName());
		woodTypeTextbox.setText(currentWood.getWoodType().getName());
		stockDoublebox.setValue(currentWood.getStock().doubleValue());
		stockAvailableDoublebox.setValue(getStockAvailable(currentWood).doubleValue());
		quantityDoublebox.setValue(currentRawMaterialRequirement.getQuantity().doubleValue());
		if(currentWoodReserved == null) {
			stockReservedDoublebox.setValue(0.0);
			stockMissingDoublebox.setValue(currentRawMaterialRequirement.getQuantity().doubleValue());
		} else {
			stockReservedDoublebox.setValue(currentWoodReserved.getStockReserved().doubleValue());
			stockMissingDoublebox.setValue(currentRawMaterialRequirement.getQuantity().doubleValue() - currentWoodReserved.getStockReserved().doubleValue());
		}
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		rawMaterialReservationWindow.detach();
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
	}

	@Listen("onClick = #completeButton")
	public void completeButtonClick() {
		stockReservedDoublebox.setValue(quantityDoublebox.getValue());
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(stockReservedDoublebox.getValue() == 0.0) {
			Clients.showNotification("Debe ingresar una cantidad mayor a cero", stockReservedDoublebox);
			return;
		}
		if(stockReservedDoublebox.getValue() > currentRawMaterialRequirement.getQuantity().doubleValue()) {
			Clients.showNotification("Debe ingresar una cantidad menor o igual a la cantidad necesaria", stockReservedDoublebox);
			return;
		}
		if(stockReservedDoublebox.getValue() > getStockAvailable(currentWood).doubleValue()) {
			Clients.showNotification("No existe stock disponible suficiente para realizar la reserva", stockReservedDoublebox);
			return;
		}
		BigDecimal stockReserved = BigDecimal.valueOf(stockReservedDoublebox.getValue());
		if(currentWoodReserved == null) {
			currentWoodReserved = new MaterialReserved(currentWood, currentRawMaterialRequirement, stockReserved);
			currentWood.getWoodsReserved().add(currentWoodReserved);
		} else {
			currentWoodReserved.setStockReserved(stockReserved);
		}
		woodRepository.save(currentWood);
		EventQueue<Event> eq = EventQueues.lookup("Requirement Reservation Queue", EventQueues.DESKTOP, true);
		eq.publish(new Event("onRawMaterialReservation", null, null));
		alert("Reserva guardada.");
		rawMaterialReservationWindow.detach();
	}

	private BigDecimal getStockAvailable(Wood material) {
		// devuelve la diferencia entre el stock total y el total de los reservados, sumando a esa diferencia lo que ya se reservo del actual
		BigDecimal stockAvailable = material.getStockAvailable();
		if(currentWoodReserved != null) {
			// se suma porque lo reservado del actual es parte de lo que se puede reservar
			stockAvailable = stockAvailable.add(currentWoodReserved.getStockReserved());
		}
		return stockAvailable;
	}

	@Listen("onOK = #stockReservedDoublebox")
	public void stockReservedDoubleboxOnOK() {
		saveButtonClick();
	}

	private MaterialReserved getMaterialReserved(MaterialRequirement materialRequirement) {
		for(MaterialReserved each: materialRequirement.getItem().getMaterialReservedList()) {
			if(productionPlanRepository.findOne(each.getMaterialRequirement().getProductionPlan().getId()).equals(productionPlanRepository.findOne(materialRequirement.getProductionPlan().getId()))) {
				return each;
			}
		}
		return null;
	}
}