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
	Listbox supplyListbox;
	@Wire
	Button newButton;
	@Wire
	Grid supplyGrid;
	@Wire
	Textbox codeTextBox;
	@Wire
	Textbox descriptionTextBox;
	@Wire
	Textbox detailsTextBox;
	@Wire
	Textbox brandTextBox;
	@Wire
	Textbox presentationTextBox;
	@Wire
	Textbox measureTextBox;
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
		supplyListbox.setModel(supplyTypeListModel);
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
		supplyGrid.setVisible(true);
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(Strings.isBlank(descriptionTextBox.getText())){
			Clients.showNotification("Debe ingresar una descripcion", descriptionTextBox);
			return;
		}
		String code = codeTextBox.getText();
		String description = descriptionTextBox.getText();
		String details = detailsTextBox.getText();
		String brand = brandTextBox.getText();
		String presentation = presentationTextBox.getText();
		String measure = measureTextBox.getText();
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

	@Listen("onSelect = #supplyListbox")
	public void doListBoxSelect() {
		if(supplyTypeListModel.isSelectionEmpty()) {
			//just in case for the no selection
			currentSupplyType = null;
		} else {
			if(currentSupplyType == null) {// si no hay nada editandose
				currentSupplyType = supplyTypeListModel.getSelection().iterator().next();
				refreshView();
			}
		}
		supplyTypeListModel.clearSelection();
	}

	private void refreshView() {
		supplyTypeListModel.clearSelection();
		supplyListbox.setModel(supplyTypeListModel);// se actualiza la lista
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		newButton.setDisabled(false);
		if(currentSupplyType == null) {// creando
			supplyGrid.setVisible(false);
			codeTextBox.setValue(null);
			descriptionTextBox.setValue(null);
			detailsTextBox.setValue(null);
			brandTextBox.setValue(null);
			presentationTextBox.setValue(null);
			measureTextBox.setValue(null);
			deleteButton.setDisabled(true);
			resetButton.setDisabled(true);// al crear, el boton new cumple la misma funcion q el reset
		}else {// editando
			supplyGrid.setVisible(true);
			codeTextBox.setValue(currentSupplyType.getCode());
			descriptionTextBox.setValue(currentSupplyType.getDescription());
			detailsTextBox.setValue(currentSupplyType.getDetails());
			brandTextBox.setValue(currentSupplyType.getBrand());
			presentationTextBox.setValue(currentSupplyType.getPresentation());
			measureTextBox.setValue(currentSupplyType.getMeasure());
			deleteButton.setDisabled(false);
			resetButton.setDisabled(false);
		}
	}
}
