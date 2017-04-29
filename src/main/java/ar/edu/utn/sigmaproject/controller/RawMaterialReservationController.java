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

import ar.edu.utn.sigmaproject.domain.RawMaterialRequirement;
import ar.edu.utn.sigmaproject.domain.Wood;
import ar.edu.utn.sigmaproject.domain.WoodReserved;
import ar.edu.utn.sigmaproject.service.RawMaterialRequirementRepository;
import ar.edu.utn.sigmaproject.service.WoodRepository;
import ar.edu.utn.sigmaproject.service.WoodReservedRepository;

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
	private RawMaterialRequirementRepository rawMaterialRequirementRepository;
	@WireVariable
	private WoodRepository woodRepository;
	@WireVariable
	private WoodReservedRepository woodReservedRepository;

	// attributes
	private RawMaterialRequirement currentRawMaterialRequirement;
	private Wood currentWood;
	private WoodReserved currentWoodReserved;

	// list

	// list models

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		//		currentRawMaterialRequirement = (RawMaterialRequirement) Executions.getCurrent().getAttribute("selected_rawMaterial_requirement");
		currentRawMaterialRequirement = (RawMaterialRequirement) Executions.getCurrent().getArg().get("selected_raw_material_requirement");
		if(currentRawMaterialRequirement == null) {throw new RuntimeException("RawMaterialRequirement null");}
		currentWood = currentRawMaterialRequirement.getWood();
		currentWoodReserved = woodReservedRepository.findByRawMaterialRequirement(currentRawMaterialRequirement);
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
		descriptionTextbox.setText(currentRawMaterialRequirement.getWood().getName());
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
			currentWoodReserved = new WoodReserved(currentRawMaterialRequirement, stockReserved);
			currentWoodReserved = woodReservedRepository.save(currentWoodReserved);
			if(currentWood != null) {
				currentWood.getWoodsReserved().add(currentWoodReserved);
				woodRepository.save(currentWood);
			} else {
				throw new RuntimeException("currentWood null");
			}
		} else {
			currentWoodReserved.setStockReserved(stockReserved);
			woodReservedRepository.save(currentWoodReserved);
		}
		EventQueue<Event> eq = EventQueues.lookup("Requirement Reservation Queue", EventQueues.DESKTOP, true);
		eq.publish(new Event("onRawMaterialReservation", null, null));
		alert("Reserva guardada.");
		rawMaterialReservationWindow.detach();
	}

	@Listen("onOK = #stockReservedDoublebox")
	public void stockReservedDoubleboxOnOK() {
		saveButtonClick();
	}
}
