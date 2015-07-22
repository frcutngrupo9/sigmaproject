package ar.edu.utn.sigmaproject.controller;

import java.util.List;

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Selectbox;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.RawMaterial;
import ar.edu.utn.sigmaproject.service.RawMaterialService;
import ar.edu.utn.sigmaproject.service.impl.RawMaterialServiceImpl;

public class RawMaterialController extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;
    
    @Wire
    private Textbox searchTextbox;
    
    @Wire
    private Listbox rawMaterialListbox;
    
    @Wire
    private Button saveButton;
    
    @Wire
    private Button cancelButton;
    
    @Wire
    private Button resetButton;
    
    @Wire
    private Button newButton;
    
    @Wire
    private Textbox nameTextBox;

    @Wire
    private Doublebox lengthTextBox;

    @Wire
    private Doublebox depthTextBox;

    @Wire
    private Doublebox heightTextBox;
    
    @Wire
    private Selectbox measureUnitSelectBox;
    
    @Wire
    private Grid rawMaterialGrid;
    
    private RawMaterial selectedRawMaterial;
    private RawMaterialService rawMaterialService = new RawMaterialServiceImpl();
    private ListModelList<RawMaterial> rawMaterialListModel;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception{
        super.doAfterCompose(comp);
        List<RawMaterial> rawMaterialList = rawMaterialService.getRawMaterialList();
        rawMaterialListModel = new ListModelList<RawMaterial>(rawMaterialList);
        rawMaterialListbox.setModel(rawMaterialListModel);
        selectedRawMaterial = null;
        updateUI();
    }
    //prueba de commit
    @Listen("onClick = #searchButton")
    public void search() {
    }
    
    @Listen("onClick = #newButton")
    public void newRawMaterial() {
        selectedRawMaterial = new RawMaterial(null, null, "", 0L, 0L, 0L);
        updateUI();
    }
    
    @Listen("onClick = #saveButton")
    public void saveRawMaterial() {
    	if(Strings.isBlank(nameTextBox.getText())){
			Clients.showNotification("Debe ingresar un nombre", nameTextBox);
			return;
		}
    	selectedRawMaterial.setName(nameTextBox.getText());
        selectedRawMaterial.setLength(Long.parseLong(lengthTextBox.getText()));
        selectedRawMaterial.setDepth(Long.parseLong(depthTextBox.getText()));
        selectedRawMaterial.setHeight(Long.parseLong(heightTextBox.getText()));
        selectedRawMaterial.setIdMeasureUnit(measureUnitSelectBox.getSelectedIndex());
        //selectedRawMaterial.setIdMeasureUnit(null);
    	if(selectedRawMaterial.getId() == null)	{
    		selectedRawMaterial.setId(rawMaterialService.getNewId());
            selectedRawMaterial = rawMaterialService.saveRawMaterial(selectedRawMaterial);
    	} else {
    		//si es una actualizacion
    		selectedRawMaterial = rawMaterialService.updateRawMaterial(selectedRawMaterial);
    	}
    	List<RawMaterial> rawMaterialList = rawMaterialService.getRawMaterialList();
        rawMaterialListModel = new ListModelList<RawMaterial>(rawMaterialList);
        rawMaterialListbox.setModel(rawMaterialListModel);
		selectedRawMaterial = null;
        updateUI();
    }
    
    @Listen("onClick = #cancelButton")
    public void cancelRawMaterial() {
    	selectedRawMaterial = null;
        updateUI();
    }
    
    @Listen("onClick = #resetButton")
    public void resetRawMaterial() {
        updateUI();
    }
    
    @Listen("onSelect = #rawMaterialListbox")
	public void doTodoSelect() {
		if(rawMaterialListModel.isSelectionEmpty()){
			//just in case for the no selection
			selectedRawMaterial = null;
		}else{
			selectedRawMaterial = rawMaterialListModel.getSelection().iterator().next();
		}
		updateUI();
	}
    
    public String getMeasureUnitName(int idMeasureUnit) {
    	//return measureUnitService.getMeasureUnit(idMeasureUnit).getName();
    	return "name_measure_unit_" + idMeasureUnit;
    }
    
    private void updateUI() {  
        if(selectedRawMaterial == null) {
			//limpiar
        	rawMaterialGrid.setVisible(false);
        	nameTextBox.setValue(null);
        	lengthTextBox.setValue(null);
        	depthTextBox.setValue(null);
        	heightTextBox.setValue(null);
        	//measureUnitSelectBox.setSelectedIndex(0);
        	
			saveButton.setDisabled(true);
			cancelButton.setDisabled(true);
			resetButton.setDisabled(true);
			newButton.setDisabled(false);
			rawMaterialListbox.clearSelection();
		}else{
			rawMaterialGrid.setVisible(true);
			nameTextBox.setValue(selectedRawMaterial.getName());
        	lengthTextBox.setValue(selectedRawMaterial.getLength());
        	depthTextBox.setValue(selectedRawMaterial.getDepth());
        	heightTextBox.setValue(selectedRawMaterial.getHeight());
        	//measureUnitSelectBox.setSelectedIndex(measureUnit.get(selectedRawMaterial.getIdMeasureUnit()).getName();
        	
			saveButton.setDisabled(false);
			cancelButton.setDisabled(false);
			resetButton.setDisabled(false);
			newButton.setDisabled(true);
		}
    }
}
