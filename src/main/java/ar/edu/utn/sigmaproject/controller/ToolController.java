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

import ar.edu.utn.sigmaproject.domain.ToolType;
import ar.edu.utn.sigmaproject.service.ToolTypeService;
import ar.edu.utn.sigmaproject.service.impl.ToolTypeServiceImpl;

public class ToolController extends SelectorComposer<Component>{
    private static final long serialVersionUID = 1L;
    
    @Wire
    Textbox searchTextbox;
    @Wire
    Listbox toolListbox;
    @Wire
    Button newButton;
    @Wire
    Grid toolGrid;
    @Wire
    Button saveButton;
    @Wire
    Button cancelButton;
    @Wire
    Button resetButton;
    @Wire
    Button deleteButton;
    @Wire
    Textbox nameTextBox;
    @Wire
    Textbox descriptionTextBox;
    @Wire
    Textbox detailsTextBox;
    @Wire
    Textbox brandTextBox;
    
    // services
    private ToolTypeService toolTypeService = new ToolTypeServiceImpl();
    
    // atributes
    private ToolType currentToolType;
    
    // list
    private List<ToolType> toolTypeList;
    
    // list models
    private ListModelList<ToolType> toolTypeListModel;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        toolTypeList = toolTypeService.getToolTypeList();
        toolTypeListModel = new ListModelList<ToolType>(toolTypeList);
        toolListbox.setModel(toolTypeListModel);
        currentToolType = null;
        refreshView();
    }
    
    @Listen("onClick = #searchButton")
    public void search() {
    }
    
    @Listen("onClick = #newButton")
    public void newButtonClick() {
        currentToolType = new ToolType(null, "", "", "", "");
        refreshView();
    }
    
    @Listen("onClick = #saveButton")
    public void saveButtonClick() {
        if(Strings.isBlank(nameTextBox.getText())){
            Clients.showNotification("Debe ingresar un nombre", nameTextBox);
            return;
        }
        currentToolType.setName(nameTextBox.getText());
        currentToolType.setDescription(descriptionTextBox.getText());
        currentToolType.setDetails(detailsTextBox.getText());
        currentToolType.setBrand(brandTextBox.getText());
        if(currentToolType.getId() == null) {// nuevo
            currentToolType = toolTypeService.saveToolType(currentToolType);
        } else {
            // es una actualizacion
            currentToolType = toolTypeService.updateToolType(currentToolType);
        }
        toolTypeList = toolTypeService.getToolTypeList();
        toolTypeListModel = new ListModelList<ToolType>(toolTypeList);
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
        toolTypeService.deleteToolType(currentToolType);
        toolTypeListModel.remove(currentToolType);
        currentToolType = null;
        refreshView();
    }
    
    @Listen("onSelect = #toolListbox")
    public void doListBoxSelect() {
        if(toolTypeListModel.isSelectionEmpty()) {
            //just in case for the no selection
            currentToolType = null;
        } else {
        	if(currentToolType == null) {// si no hay nada editandose
        		currentToolType = toolTypeListModel.getSelection().iterator().next();
        		refreshView();
        	}
        }
        toolTypeListModel.clearSelection();
    }
    
    private void refreshView() {
    	toolTypeListModel.clearSelection();
    	toolListbox.setModel(toolTypeListModel);// se actualiza la lista
        if(currentToolType == null) {// no se esta editando ni creando
            toolGrid.setVisible(false);
            
            saveButton.setDisabled(true);
            cancelButton.setDisabled(true);
            resetButton.setDisabled(true);
            deleteButton.setDisabled(true);
            newButton.setDisabled(false);
        } else {// editando o creando
            toolGrid.setVisible(true);
            nameTextBox.setValue(currentToolType.getName());
            descriptionTextBox.setValue(currentToolType.getDescription());
            detailsTextBox.setValue(currentToolType.getDetails());
            brandTextBox.setValue(currentToolType.getBrand());
            
            saveButton.setDisabled(false);
            cancelButton.setDisabled(false);
            resetButton.setDisabled(false);
            if(currentToolType.getId() == null) {
                deleteButton.setDisabled(true);
            } else {
                deleteButton.setDisabled(false);
            }
            newButton.setDisabled(true);
        }
    }
}
