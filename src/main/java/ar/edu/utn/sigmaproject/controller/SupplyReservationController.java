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

import ar.edu.utn.sigmaproject.domain.SupplyRequirement;
import ar.edu.utn.sigmaproject.domain.SupplyReserved;
import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.service.SupplyReservedRepository;
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
	private SupplyReservedRepository supplyReservedRepository;

	// attributes
	private SupplyRequirement currentSupplyRequirement;
	private SupplyReserved currentSupplyReserved;

	// list

	// list models

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		//		currentSupplyRequirement = (SupplyRequirement) Executions.getCurrent().getAttribute("selected_supply_requirement");
		currentSupplyRequirement = (SupplyRequirement) Executions.getCurrent().getArg().get("selected_supply_requirement");
		if(currentSupplyRequirement == null) {throw new RuntimeException("SupplyRequirement null");}
		currentSupplyReserved = supplyReservedRepository.findBySupplyRequirement(currentSupplyRequirement);

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
		codeTextbox.setText(currentSupplyRequirement.getSupplyType().getCode());
		descriptionTextbox.setText(currentSupplyRequirement.getSupplyType().getDescription());
		stockDoublebox.setValue(currentSupplyRequirement.getSupplyType().getStock().doubleValue());
		stockAvailableDoublebox.setValue(getSupplyStockAvailable(currentSupplyRequirement.getSupplyType()).doubleValue());
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
		if(stockReservedDoublebox.getValue() == 0.0) {
			Clients.showNotification("Debe ingresar una cantidad mayor a cero", stockReservedDoublebox);
			return;
		}
		if(stockReservedDoublebox.getValue() > currentSupplyRequirement.getQuantity().doubleValue()) {
			Clients.showNotification("Debe ingresar una cantidad menor o igual a la cantidad necesaria", stockReservedDoublebox);
			return;
		}
		if(stockReservedDoublebox.getValue() > getSupplyStockAvailable(currentSupplyRequirement.getSupplyType()).doubleValue()) {
			Clients.showNotification("No existe stock disponible suficiente para realizar la reserva", stockReservedDoublebox);
			return;
		}
		BigDecimal stockReserved = BigDecimal.valueOf(stockReservedDoublebox.getValue());
		if(currentSupplyReserved == null) {
			currentSupplyReserved = new SupplyReserved(currentSupplyRequirement, stockReserved);
			currentSupplyReserved = supplyReservedRepository.save(currentSupplyReserved);
			SupplyType supplyType = currentSupplyRequirement.getSupplyType();
			supplyType.getSuppliesReserved().add(currentSupplyReserved);
			supplyTypeRepository.save(supplyType);
		} else {
			currentSupplyReserved.setStockReserved(stockReserved);
			supplyReservedRepository.save(currentSupplyReserved);
		}
		alert("Reserva guardada.");
		EventQueue<Event> eq = EventQueues.lookup("Requirement Reservation Queue", EventQueues.DESKTOP, true);
		eq.publish(new Event("onSupplyReservation", null, null));
		supplyReservationWindow.detach();
	}

	public BigDecimal getSupplyStockAvailable(SupplyType supplyType) {
		// devuelve la diferencia entre el stock total y el total reservado
		BigDecimal stockTotal = supplyType.getStock();
		BigDecimal stockReservedTotal = supplyType.getStockReserved();
		// si existe una reserva hecha para este plan, entonces es parte del stock disponible
		if(currentSupplyReserved != null) {
			stockReservedTotal = stockReservedTotal.subtract(currentSupplyReserved.getStockReserved());
		}
		return stockTotal.subtract(stockReservedTotal);
	}
	
	@Listen("onOK = #stockReservedDoublebox")
	public void stockReservedDoubleboxOnOK() {
		saveButtonClick();
	}
}
