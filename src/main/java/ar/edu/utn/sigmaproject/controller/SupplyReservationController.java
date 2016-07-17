package ar.edu.utn.sigmaproject.controller;

import org.zkoss.lang.Strings;
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

	// attributes
	private SupplyRequirement currentSupplyRequirement;

	// list

	// list models

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
//		currentSupplyRequirement = (SupplyRequirement) Executions.getCurrent().getAttribute("selected_supply_requirement");
		currentSupplyRequirement = (SupplyRequirement) Executions.getCurrent().getArg().get("selected_supply_requirement");
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
		stockReservedDoublebox.setValue(0.0);
		stockMissingDoublebox.setValue(currentSupplyRequirement.getQuantity().doubleValue());
	}
	
	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		supplyReservationWindow.detach();
	}
	
	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(stockReservedDoublebox.getValue() == 0.0){
			Clients.showNotification("Debe ingresar una cantidad mayor a cero", stockReservedDoublebox);
			return;
		}
	}
}
