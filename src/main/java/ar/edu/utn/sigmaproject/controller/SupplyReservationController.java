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
import ar.edu.utn.sigmaproject.domain.MaterialType;
import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;
import ar.edu.utn.sigmaproject.service.SupplyTypeRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class SupplyReservationController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Window supplyReservationWindow;
	@Wire
	Grid supplyReservationGrid;
	@Wire
	Textbox codeTextbox;
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
	private SupplyTypeRepository supplyTypeRepository;
	@WireVariable
	private ProductionPlanRepository productionPlanRepository;

	// attributes
	private MaterialRequirement currentSupplyRequirement;
	private MaterialReserved currentSupplyReserved;

	// list

	// list models

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentSupplyRequirement = (MaterialRequirement) Executions.getCurrent().getArg().get("selected_supply_requirement");
		if(currentSupplyRequirement == null) {throw new RuntimeException("SupplyRequirement null");}
		currentSupplyReserved = getMaterialReserved(currentSupplyRequirement);
		refreshView();
	}

	private void refreshView() {
		codeTextbox.setDisabled(true);
		descriptionTextbox.setDisabled(true);
		stockDoublebox.setDisabled(true);
		stockReservedDoublebox.setDisabled(false);
		stockMissingDoublebox.setDisabled(true);
		quantityDoublebox.setDisabled(true);
		stockAvailableDoublebox.setDisabled(true);
		SupplyType supplyType = (SupplyType) currentSupplyRequirement.getItem();
		codeTextbox.setText(supplyType.getCode());
		descriptionTextbox.setText(supplyType.getDescription());
		stockDoublebox.setValue(supplyType.getStock().doubleValue());
		stockAvailableDoublebox.setValue(getStockAvailable(supplyType).doubleValue());
		quantityDoublebox.setValue(currentSupplyRequirement.getQuantity().doubleValue());
		if(currentSupplyReserved == null) {
			stockReservedDoublebox.setValue(0.0);
			stockMissingDoublebox.setValue(currentSupplyRequirement.getQuantity().doubleValue());
		} else {
			stockReservedDoublebox.setValue(currentSupplyReserved.getStockReserved().doubleValue());
			stockMissingDoublebox.setValue(currentSupplyRequirement.getQuantity().doubleValue() - currentSupplyReserved.getStockReserved().doubleValue());
		}

	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		supplyReservationWindow.detach();
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
		SupplyType supplyType = (SupplyType) currentSupplyRequirement.getItem();
		if(stockReservedDoublebox.getValue() == 0.0) {
			Clients.showNotification("Debe ingresar una cantidad mayor a cero", stockReservedDoublebox);
			return;
		}
		if(stockReservedDoublebox.getValue() > currentSupplyRequirement.getQuantity().doubleValue()) {
			Clients.showNotification("Debe ingresar una cantidad menor o igual a la cantidad necesaria", stockReservedDoublebox);
			return;
		}
		if(stockReservedDoublebox.getValue() > getStockAvailable(supplyType).doubleValue()) {
			Clients.showNotification("No existe stock disponible suficiente para realizar la reserva", stockReservedDoublebox);
			return;
		}
		BigDecimal stockReserved = BigDecimal.valueOf(stockReservedDoublebox.getValue());
		if(currentSupplyReserved == null) {
			currentSupplyReserved = new MaterialReserved(supplyType, MaterialType.Supply, currentSupplyRequirement, stockReserved);
			supplyType.getSuppliesReserved().add(currentSupplyReserved);
		} else {
			currentSupplyReserved.setStockReserved(stockReserved);
		}
		supplyTypeRepository.save(supplyType);
		EventQueue<Event> eq = EventQueues.lookup("Requirement Reservation Queue", EventQueues.DESKTOP, true);
		eq.publish(new Event("onSupplyReservation", null, null));
		alert("Reserva guardada.");
		supplyReservationWindow.detach();
	}

	private BigDecimal getStockAvailable(SupplyType material) {
		// devuelve la diferencia entre el stock total y el total de los reservados, sumando a esa diferencia lo que ya se reservo del actual
		BigDecimal stockAvailable = material.getStockAvailable();
		if(currentSupplyReserved != null) {
			// se suma porque lo reservado del actual es parte de lo que se puede reservar
			stockAvailable = stockAvailable.add(currentSupplyReserved.getStockReserved());
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
