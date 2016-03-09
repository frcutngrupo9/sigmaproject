package ar.edu.utn.sigmaproject.controller;

import java.util.List;

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.service.SupplyTypeService;
import ar.edu.utn.sigmaproject.service.impl.SupplyTypeServiceImpl;

public class SupplyController extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Listbox supplyTypeListbox;
	@Wire
	Button newButton;
	@Wire
	Grid supplyTypeGrid;
	@Wire
	Textbox codeTextbox;
	@Wire
	Textbox descriptionTextbox;
	@Wire
	Textbox detailsTextbox;
	@Wire
	Textbox brandTextbox;
	@Wire
	Textbox presentationTextbox;
	@Wire
	Textbox measureTextbox;
	@Wire
	Button saveButton;
	@Wire
	Button cancelButton;
	@Wire
	Button resetButton;
	@Wire
	Button deleteButton;

	// services
	private SupplyTypeService supplyTypeService = new SupplyTypeServiceImpl();

	// attributes
	private SupplyType currentSupplyType;

	// list
	private List<SupplyType> supplyTypeList;

	// list models
	private ListModelList<SupplyType> supplyTypeListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		supplyTypeList = supplyTypeService.getSupplyTypeList();
		supplyTypeListModel = new ListModelList<SupplyType>(supplyTypeList);
		supplyTypeListbox.setModel(supplyTypeListModel);
		currentSupplyType = null;

		refreshView();
	}

	@Listen("onClick = #searchButton")
	public void search() {
	}

	@Listen("onClick = #newButton")
	public void newButtonClick() {
		currentSupplyType = null;
		refreshView();
		supplyTypeGrid.setVisible(true);
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(Strings.isBlank(descriptionTextbox.getText())){
			Clients.showNotification("Debe ingresar una descripcion", descriptionTextbox);
			return;
		}
		String code = codeTextbox.getText();
		String description = descriptionTextbox.getText();
		String details = detailsTextbox.getText();
		String brand = brandTextbox.getText();
		String presentation = presentationTextbox.getText();
		String measure = measureTextbox.getText();
		if(currentSupplyType == null) {
			// es un nuevo insumo
			currentSupplyType = new SupplyType(null, code, description, details, brand, presentation, measure, 0.0, 0.0, 0.0);
			currentSupplyType = supplyTypeService.saveSupplyType(currentSupplyType);
		} else {
			// es una edicion
			currentSupplyType.setCode(code);
			currentSupplyType.setDescription(description);
			currentSupplyType.setDetails(details);
			currentSupplyType.setBrand(brand);
			currentSupplyType.setPresentation(presentation);
			currentSupplyType.setMeasure(measure);
			currentSupplyType = supplyTypeService.updateSupplyType(currentSupplyType);
		}
		supplyTypeList = supplyTypeService.getSupplyTypeList();
		supplyTypeListModel = new ListModelList<SupplyType>(supplyTypeList);
		currentSupplyType = null;
		refreshView();
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		currentSupplyType = null;
		refreshView();
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
	}

	@Listen("onClick = #deleteButton")
	public void deleteButtonClick() {
		supplyTypeService.deleteSupplyType(currentSupplyType);
		supplyTypeListModel.remove(currentSupplyType);
		currentSupplyType = null;
		refreshView();
	}

	@Listen("onSelect = #supplyTypeListbox")
	public void doListBoxSelect() {
		if(supplyTypeListModel.isSelectionEmpty()) {
			//just in case for the no selection
			currentSupplyType = null;
		} else {
			if(currentSupplyType == null) {// si no hay nada editandose
				currentSupplyType = supplyTypeListbox.getSelectedItem().getValue();
				refreshView();
			}
		}
		supplyTypeListModel.clearSelection();
	}

	private void refreshView() {
		supplyTypeListModel.clearSelection();
		supplyTypeListbox.setModel(supplyTypeListModel);// se actualiza la lista
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		newButton.setDisabled(false);
		if(currentSupplyType == null) {// creando
			supplyTypeGrid.setVisible(false);
			codeTextbox.setValue(null);
			descriptionTextbox.setValue(null);
			detailsTextbox.setValue(null);
			brandTextbox.setValue(null);
			presentationTextbox.setValue(null);
			measureTextbox.setValue(null);
			deleteButton.setDisabled(true);
			resetButton.setDisabled(true);// al crear, el boton new cumple la misma funcion q el reset
		}else {// editando
			supplyTypeGrid.setVisible(true);
			codeTextbox.setValue(currentSupplyType.getCode());
			descriptionTextbox.setValue(currentSupplyType.getDescription());
			detailsTextbox.setValue(currentSupplyType.getDetails());
			brandTextbox.setValue(currentSupplyType.getBrand());
			presentationTextbox.setValue(currentSupplyType.getPresentation());
			measureTextbox.setValue(currentSupplyType.getMeasure());
			deleteButton.setDisabled(false);
			resetButton.setDisabled(false);
		}
	}
}
