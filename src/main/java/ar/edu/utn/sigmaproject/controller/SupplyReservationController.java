package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
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
	Doublebox stockReservedDoublebox;
	@Wire
	Doublebox stockMissingDoublebox;
	@Wire
	Button saveButton;
	@Wire
	Button cancelButton;
	@Wire
	Button resetButton;

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
		currentSupplyReserved = supplyReservedRepository.findBySupplyRequirement(currentSupplyRequirement);
		if(currentSupplyRequirement == null) {throw new RuntimeException("SupplyRequirement null");}

		refreshView();
	}

	private void refreshView() {
		codeTextbox.setDisabled(true);
		descriptionTextbox.setDisabled(true);
		stockDoublebox.setDisabled(true);
		stockReservedDoublebox.setDisabled(false);
		stockMissingDoublebox.setDisabled(true);
		codeTextbox.setText(currentSupplyRequirement.getSupplyType().getCode());
		descriptionTextbox.setText(currentSupplyRequirement.getSupplyType().getDescription());
		stockDoublebox.setValue(currentSupplyRequirement.getSupplyType().getStock().doubleValue());
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

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(stockReservedDoublebox.getValue() == 0.0) {
			Clients.showNotification("Debe ingresar una cantidad mayor a cero", stockReservedDoublebox);
			return;
		}
		BigDecimal stockReserved = BigDecimal.valueOf(stockReservedDoublebox.getValue());
		if(currentSupplyReserved == null) {
			currentSupplyReserved = new SupplyReserved(currentSupplyRequirement, stockReserved);
		} else {
			currentSupplyReserved.setStockReserved(stockReserved);
		}
		supplyReservedRepository.save(currentSupplyReserved);
		alert("Reserva guardada.");
		supplyReservationWindow.detach();
	}
}
