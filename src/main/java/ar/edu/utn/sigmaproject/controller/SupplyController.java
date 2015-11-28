package ar.edu.utn.sigmaproject.controller;

import java.util.List;

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Selectbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.Client;
import ar.edu.utn.sigmaproject.domain.RawMaterialType;
import ar.edu.utn.sigmaproject.domain.Supply;
import ar.edu.utn.sigmaproject.service.RawMaterialTypeService;
import ar.edu.utn.sigmaproject.service.SupplyService;
import ar.edu.utn.sigmaproject.service.impl.RawMaterialTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.SupplyServiceImpl;

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
	Textbox nameTextBox;
	@Wire
	Textbox detailsTextBox;
	@Wire
    Button saveButton;
	@Wire
    Button cancelButton;
    @Wire
    Button resetButton;
    @Wire
    Button deleteButton;
	
    // services
    private SupplyService supplyService = new SupplyServiceImpl();
    
    // atributes
    private Supply currentSupply;
    
    // list
    private List<Supply> supplyList;
    
    // list models
    private ListModelList<Supply> supplyListModel;
	
	@Override
    public void doAfterCompose(Component comp) throws Exception{
        super.doAfterCompose(comp);
        //SupplyCreationController supplyCreationController = (SupplyCreationController) supplyCreationWindow.getAttribute("supplyCreationWindow$composer"); (codigo que quedo cuando se manejaba una ventana para crear el insumo)
        supplyList = supplyService.getSupplyList();
        supplyListModel = new ListModelList<Supply>(supplyList);
        supplyListbox.setModel(supplyListModel);
        currentSupply = null;
        refreshView();
    }
	
	@Listen("onClick = #searchButton")
    public void search() {
    }
    
    @Listen("onClick = #newButton")
    public void newButtonClick() {
        currentSupply = new Supply(null, "", "");
        refreshView();
    }
    
    @Listen("onClick = #saveButton")
    public void saveButtonClick() {
        if(Strings.isBlank(nameTextBox.getText())){
            Clients.showNotification("Debe ingresar un nombre", nameTextBox);
            return;
        }
        currentSupply.setName(nameTextBox.getText());
        currentSupply.setDetails(detailsTextBox.getText());
        if(currentSupply.getId() == null) {
        	// es un nuevo insumo
        	currentSupply = supplyService.saveSupply(currentSupply);
        } else {
            // es una edicion
        	currentSupply = supplyService.updateSupply(currentSupply);
        }
        supplyList = supplyService.getSupplyList();
        supplyListModel = new ListModelList<Supply>(supplyList);
        currentSupply = null;
        refreshView();
    }
    
    @Listen("onClick = #cancelButton")
    public void cancelButtonClick() {
    	currentSupply = null;
        refreshView();
    }
    
    @Listen("onClick = #resetButton")
    public void resetButtonClick() {
        refreshView();
    }
    
    @Listen("onClick = #deleteButton")
    public void deleteButtonClick() {
    	supplyService.deleteSupply(currentSupply);
    	supplyListModel.remove(currentSupply);
    	currentSupply = null;
        refreshView();
    }
    
    @Listen("onSelect = #supplyListbox")
    public void doListBoxSelect() {
        if(supplyListModel.isSelectionEmpty()) {
            //just in case for the no selection
        	currentSupply = null;
        } else {
        	if(currentSupply == null) {// si no hay nada editandose
        		currentSupply = supplyListModel.getSelection().iterator().next();
        	}
        }
        refreshView();
    }
	
	private void refreshView() {
		supplyListModel.clearSelection();
		supplyListbox.setModel(supplyListModel);// se actualiza la lista
        if(currentSupply == null) {// no editando ni creando
        	supplyGrid.setVisible(false);
        	nameTextBox.setValue(null);
        	detailsTextBox.setValue(null);
			saveButton.setDisabled(true);
			cancelButton.setDisabled(true);
			resetButton.setDisabled(true);
			deleteButton.setDisabled(true);
			newButton.setDisabled(false);
		}else {// editando o creando
			supplyGrid.setVisible(true);
			nameTextBox.setValue(currentSupply.getName());
			detailsTextBox.setValue(currentSupply.getDetails());
			saveButton.setDisabled(false);
			cancelButton.setDisabled(false);
			resetButton.setDisabled(false);
			if(currentSupply.getId() == null) {
                deleteButton.setDisabled(true);
            } else {
                deleteButton.setDisabled(false);
            }
			newButton.setDisabled(true);
		}
    }
}
