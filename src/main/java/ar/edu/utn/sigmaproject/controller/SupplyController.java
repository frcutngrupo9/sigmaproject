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
import ar.edu.utn.sigmaproject.domain.MeasureUnit;
import ar.edu.utn.sigmaproject.domain.RawMaterialType;
import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.service.MeasureUnitService;
import ar.edu.utn.sigmaproject.service.MeasureUnitTypeService;
import ar.edu.utn.sigmaproject.service.RawMaterialTypeService;
import ar.edu.utn.sigmaproject.service.SupplyTypeService;
import ar.edu.utn.sigmaproject.service.impl.MeasureUnitServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.MeasureUnitTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.RawMaterialTypeServiceImpl;
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
    
    // atributes
    private SupplyType currentSupplyType;
    
    // list
    private List<SupplyType> supplyTypeList;
    
    // list models
    private ListModelList<SupplyType> supplyTypeListModel;
	
	@Override
    public void doAfterCompose(Component comp) throws Exception{
        super.doAfterCompose(comp);
        //SupplyCreationController supplyCreationController = (SupplyCreationController) supplyCreationWindow.getAttribute("supplyCreationWindow$composer"); (codigo que quedo cuando se manejaba una ventana para crear el insumo)
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
        currentSupplyType = new SupplyType(null, "", "", "", "", "", "");
        refreshView();
    }
    
    @Listen("onClick = #saveButton")
    public void saveButtonClick() {
        if(Strings.isBlank(descriptionTextBox.getText())){
            Clients.showNotification("Debe ingresar una descripcion", descriptionTextBox);
            return;
        }
        currentSupplyType.setCode(codeTextBox.getText());
        currentSupplyType.setDescription(descriptionTextBox.getText());
        currentSupplyType.setDetails(detailsTextBox.getText());
        currentSupplyType.setBrand(brandTextBox.getText());
        currentSupplyType.setPresentation(presentationTextBox.getText());
        currentSupplyType.setMeasure(measureTextBox.getText());
        if(currentSupplyType.getId() == null) {
        	// es un nuevo insumo
        	currentSupplyType = supplyTypeService.saveSupplyType(currentSupplyType);
        } else {
            // es una edicion
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
        	}
        }
        refreshView();
    }
	
	private void refreshView() {
		supplyTypeListModel.clearSelection();
		supplyListbox.setModel(supplyTypeListModel);// se actualiza la lista
        if(currentSupplyType == null) {// no editando ni creando
        	supplyGrid.setVisible(false);
        	codeTextBox.setValue(null);
        	descriptionTextBox.setValue(null);
        	detailsTextBox.setValue(null);
        	brandTextBox.setValue(null);
        	presentationTextBox.setValue(null);
        	measureTextBox.setValue(null);
			saveButton.setDisabled(true);
			cancelButton.setDisabled(true);
			resetButton.setDisabled(true);
			deleteButton.setDisabled(true);
			newButton.setDisabled(false);
		}else {// editando o creando
			supplyGrid.setVisible(true);
			codeTextBox.setValue(currentSupplyType.getCode());
        	descriptionTextBox.setValue(currentSupplyType.getDescription());
        	detailsTextBox.setValue(currentSupplyType.getDetails());
        	brandTextBox.setValue(currentSupplyType.getBrand());
        	presentationTextBox.setValue(currentSupplyType.getPresentation());
        	measureTextBox.setValue(currentSupplyType.getMeasure());
			saveButton.setDisabled(false);
			cancelButton.setDisabled(false);
			resetButton.setDisabled(false);
			if(currentSupplyType.getId() == null) {
                deleteButton.setDisabled(true);
            } else {
                deleteButton.setDisabled(false);
            }
			newButton.setDisabled(true);
		}
    }
}
