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
import ar.edu.utn.sigmaproject.domain.Wood;
import ar.edu.utn.sigmaproject.service.MaterialReservedRepository;
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
	@WireVariable
	private MaterialReservedRepository materialReservedRepository;

	// attributes
	private MaterialRequirement currentRawMaterialRequirement;
	private Wood currentWood;
	private MaterialReserved currentWoodReserved;

	// list

	// list models

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		//		currentRawMaterialRequirement = (RawMaterialRequirement) Executions.getCurrent().getAttribute("selected_rawMaterial_requirement");
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
		stockAvailableDoublebox.setValue(currentWood.getStock().doubleValue() - currentWood.getStockReserved().doubleValue());
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
		if(stockReservedDoublebox.getValue() > currentWood.getStockAvailable().doubleValue()) {
			Clients.showNotification("No existe stock disponible suficiente para realizar la reserva", stockReservedDoublebox);
			return;
		}
		BigDecimal stockReserved = BigDecimal.valueOf(stockReservedDoublebox.getValue());
		if(currentWoodReserved == null) {
			currentWoodReserved = new MaterialReserved(currentWood, MaterialType.Wood, currentRawMaterialRequirement, stockReserved);
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
