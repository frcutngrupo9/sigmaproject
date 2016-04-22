package ar.edu.utn.sigmaproject.controller;

import java.util.List;

import ar.edu.utn.sigmaproject.service.ToolTypeRepository;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.ToolType;

public class ToolController extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Listbox toolTypeListbox;
	@Wire
	Button newButton;
	@Wire
	Grid toolTypeGrid;
	@Wire
	Button saveButton;
	@Wire
	Button cancelButton;
	@Wire
	Button resetButton;
	@Wire
	Button deleteButton;
	@Wire
	Textbox nameTextbox;
	@Wire
	Textbox descriptionTextbox;
	@Wire
	Textbox detailsTextbox;
	@Wire
	Textbox brandTextbox;

	// services
	@WireVariable
	ToolTypeRepository toolTypeRepository;

	// atributes
	private ToolType currentToolType;

	// list
	private List<ToolType> toolTypeList;

	// list models
	private ListModelList<ToolType> toolTypeListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		toolTypeList = toolTypeRepository.findAll();
		toolTypeListModel = new ListModelList<>(toolTypeList);
		toolTypeListbox.setModel(toolTypeListModel);
		currentToolType = null;
		refreshView();
	}

	@Listen("onClick = #searchButton")
	public void search() {
	}

	@Listen("onClick = #newButton")
	public void newButtonClick() {
		currentToolType = null;
		refreshView();
		toolTypeGrid.setVisible(true);
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(Strings.isBlank(nameTextbox.getText())){
			Clients.showNotification("Debe ingresar un nombre", nameTextbox);
			return;
		}
		String name = nameTextbox.getText();
		String description = descriptionTextbox.getText();
		String details = detailsTextbox.getText();
		String brand = brandTextbox.getText();
		if(currentToolType == null) {// nuevo
			currentToolType = new ToolType(name, description, details, brand);
		} else {// actualizacion
			currentToolType.setName(name);
			currentToolType.setDescription(description);
			currentToolType.setDetails(details);
			currentToolType.setBrand(brand);
		}
		currentToolType = toolTypeRepository.save(currentToolType);
		toolTypeList = toolTypeRepository.findAll();
		toolTypeListModel = new ListModelList<>(toolTypeList);
		currentToolType = null;
		refreshView();
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		currentToolType = null;
		refreshView();
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
	}

	@Listen("onClick = #deleteButton")
	public void deleteButtonClick() {
		toolTypeRepository.delete(currentToolType);
		toolTypeListModel.remove(currentToolType);
		currentToolType = null;
		refreshView();
	}

	@Listen("onSelect = #toolTypeListbox")
	public void doListBoxSelect() {
		if(toolTypeListModel.isSelectionEmpty()) {
			//just in case for the no selection
			currentToolType = null;
		} else {
			if(currentToolType == null) {// si no hay nada editandose
				currentToolType = toolTypeListbox.getSelectedItem().getValue();
				refreshView();
			}
		}
		toolTypeListModel.clearSelection();
	}

	private void refreshView() {
		toolTypeListModel.clearSelection();
		toolTypeListbox.setModel(toolTypeListModel);// se actualiza la lista
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		newButton.setDisabled(false);
		if(currentToolType == null) {// creando
			toolTypeGrid.setVisible(false);
			nameTextbox.setValue(null);
			descriptionTextbox.setValue(null);
			detailsTextbox.setValue(null);
			brandTextbox.setValue(null);
			deleteButton.setDisabled(true);
			resetButton.setDisabled(true);// al crear, el boton new cumple la misma funcion q el reset
		} else {// editando
			toolTypeGrid.setVisible(true);
			nameTextbox.setValue(currentToolType.getName());
			descriptionTextbox.setValue(currentToolType.getDescription());
			detailsTextbox.setValue(currentToolType.getDetails());
			brandTextbox.setValue(currentToolType.getBrand());
			deleteButton.setDisabled(false);
			resetButton.setDisabled(false);
		}
	}
}
