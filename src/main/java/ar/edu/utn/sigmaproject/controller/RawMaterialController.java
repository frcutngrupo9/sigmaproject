package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
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

import ar.edu.utn.sigmaproject.domain.MeasureUnit;
import ar.edu.utn.sigmaproject.domain.MeasureUnitType;
import ar.edu.utn.sigmaproject.domain.RawMaterial;
import ar.edu.utn.sigmaproject.service.MeasureUnitService;
import ar.edu.utn.sigmaproject.service.MeasureUnitTypeService;
import ar.edu.utn.sigmaproject.service.RawMaterialService;
import ar.edu.utn.sigmaproject.service.impl.MeasureUnitServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.MeasureUnitTypeServiceImpl;
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
    private MeasureUnitService measureUnitService = new MeasureUnitServiceImpl();
    private MeasureUnitTypeService measureUnitTypeService = new MeasureUnitTypeServiceImpl();
    private ListModelList<RawMaterial> rawMaterialListModel;
    private ListModelList<MeasureUnit> measureUnitListModel;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception{
        super.doAfterCompose(comp);
        List<RawMaterial> rawMaterialList = rawMaterialService.getRawMaterialList();
        rawMaterialListModel = new ListModelList<RawMaterial>(rawMaterialList);
        rawMaterialListbox.setModel(rawMaterialListModel);
        selectedRawMaterial = null;
        
        Integer idMeasureUnitType = measureUnitTypeService.getMeasureUnitType("Longitud").getId();
        List<MeasureUnit> measureUnitlList = measureUnitService.getMeasureUnitList(idMeasureUnitType);
        
        measureUnitListModel = new ListModelList<MeasureUnit>(measureUnitlList);
        measureUnitSelectBox.setModel(measureUnitListModel);
        
        updateUI();
    }
    //prueba de commit
    @Listen("onClick = #searchButton")
    public void search() {
    	//System.out.println("indice del measure elegido" + measureUnitSelectBox.getSelectedIndex());
    }
    
    @Listen("onClick = #newButton")
    public void newRawMaterial() {
        selectedRawMaterial = new RawMaterial(null, null, "", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        updateUI();
    }
    
    @Listen("onClick = #saveButton")
    public void saveButtonClick() {
    	if(Strings.isBlank(nameTextBox.getText())){
			Clients.showNotification("Debe ingresar un nombre", nameTextBox);
			return;
		}
    	selectedRawMaterial.setName(nameTextBox.getText());
        selectedRawMaterial.setLength(new BigDecimal(Double.parseDouble(lengthTextBox.getText())));
        selectedRawMaterial.setDepth(new BigDecimal(Double.parseDouble(depthTextBox.getText())));
        selectedRawMaterial.setHeight(new BigDecimal(Double.parseDouble(heightTextBox.getText())));
        selectedRawMaterial.setIdMeasureUnit(measureUnitListModel.getElementAt(measureUnitSelectBox.getSelectedIndex()).getId());
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
    public void cancelButtonClick() {
    	selectedRawMaterial = null;
        updateUI();
    }
    
    @Listen("onClick = #resetButton")
    public void resetButtonClick() {
        updateUI();
    }
    
    @Listen("onSelect = #rawMaterialListbox")
	public void doListBoxSelect() {
		if(rawMaterialListModel.isSelectionEmpty()){
			//just in case for the no selection
			selectedRawMaterial = null;
		}else{
			selectedRawMaterial = rawMaterialListModel.getSelection().iterator().next();
		}
		updateUI();
	}
    
    public String getMeasureUnitName(int idMeasureUnit) {
    	return measureUnitService.getMeasureUnit(idMeasureUnit).getName();
    }
    
    private void updateUI() {  
        if(selectedRawMaterial == null) {
			//limpiar
        	rawMaterialGrid.setVisible(false);
        	nameTextBox.setValue(null);
        	lengthTextBox.setValue(null);
        	depthTextBox.setValue(null);
        	heightTextBox.setValue(null);
        	measureUnitSelectBox.setSelectedIndex(-1);
        	
			saveButton.setDisabled(true);
			cancelButton.setDisabled(true);
			resetButton.setDisabled(true);
			newButton.setDisabled(false);
			rawMaterialListbox.clearSelection();
		}else {
			rawMaterialGrid.setVisible(true);
			nameTextBox.setValue(selectedRawMaterial.getName());
        	lengthTextBox.setValue(selectedRawMaterial.getLength().doubleValue());
        	depthTextBox.setValue(selectedRawMaterial.getDepth().doubleValue());
        	heightTextBox.setValue(selectedRawMaterial.getHeight().doubleValue());
        	if(selectedRawMaterial.getIdMeasureUnit() != null) {
        		MeasureUnitType aux = measureUnitTypeService.getMeasureUnitType(selectedRawMaterial.getIdMeasureUnit());
        		measureUnitSelectBox.setSelectedIndex(aux.getId()-1);
        	} else {
        		measureUnitSelectBox.setSelectedIndex(-1);
        	}
			saveButton.setDisabled(false);
			cancelButton.setDisabled(false);
			resetButton.setDisabled(false);
			newButton.setDisabled(true);
		}
    }
}
