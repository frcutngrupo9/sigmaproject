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

import ar.edu.utn.sigmaproject.domain.Client;
import ar.edu.utn.sigmaproject.domain.MeasureUnit;
import ar.edu.utn.sigmaproject.domain.MeasureUnitType;
import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.domain.OrderState;
import ar.edu.utn.sigmaproject.domain.OrderStateType;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.RawMaterial;
import ar.edu.utn.sigmaproject.service.ClientService;
import ar.edu.utn.sigmaproject.service.MeasureUnitService;
import ar.edu.utn.sigmaproject.service.MeasureUnitTypeService;
import ar.edu.utn.sigmaproject.service.OrderDetailService;
import ar.edu.utn.sigmaproject.service.OrderService;
import ar.edu.utn.sigmaproject.service.OrderStateService;
import ar.edu.utn.sigmaproject.service.OrderStateTypeService;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.RawMaterialService;
import ar.edu.utn.sigmaproject.service.impl.ClientServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.MeasureUnitServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.MeasureUnitTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderDetailServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderStateServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderStateTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.RawMaterialServiceImpl;

public class RawMaterialController extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;
    
    @Wire
    Textbox searchTextbox;
    @Wire
    Listbox rawMaterialListbox;
    @Wire
    Button saveButton;
    @Wire
    Button cancelButton;
    @Wire
    Button resetButton;
    @Wire
    Button deleteButton;
    @Wire
    Button newButton;
    @Wire
    Textbox nameTextbox;
    @Wire
    Doublebox lengthDoublebox;
    @Wire
    Doublebox depthDoublebox;
    @Wire
    Doublebox heightDoublebox;
    @Wire
    Selectbox measureUnitSelectbox;
    @Wire
    Grid rawMaterialGrid;
    
    // services
    private RawMaterialService rawMaterialService = new RawMaterialServiceImpl();
    private MeasureUnitService measureUnitService = new MeasureUnitServiceImpl();
    private MeasureUnitTypeService measureUnitTypeService = new MeasureUnitTypeServiceImpl();
    
    // atributes
    private RawMaterial currentRawMaterial;
    
    // list
    private List<RawMaterial> rawMaterialList;
    private List<MeasureUnit> measureUnitlList;
    
    // list models
    private ListModelList<RawMaterial> rawMaterialListModel;
    private ListModelList<MeasureUnit> measureUnitListModel;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception{
        super.doAfterCompose(comp);
        rawMaterialList = rawMaterialService.getRawMaterialList();
        rawMaterialListModel = new ListModelList<RawMaterial>(rawMaterialList);
        rawMaterialListbox.setModel(rawMaterialListModel);
        currentRawMaterial = null;
        
        Integer idMeasureUnitType = measureUnitTypeService.getMeasureUnitType("Longitud").getId();
        measureUnitlList = measureUnitService.getMeasureUnitList(idMeasureUnitType);
        measureUnitListModel = new ListModelList<MeasureUnit>(measureUnitlList);
        measureUnitSelectbox.setModel(measureUnitListModel);
        refreshView();
    }
    //prueba de commit
    @Listen("onClick = #searchButton")
    public void search() {
    	//System.out.println("indice del measure elegido" + measureUnitSelectBox.getSelectedIndex());
    }
    
    @Listen("onClick = #newButton")
    public void newRawMaterial() {
        currentRawMaterial = new RawMaterial(null, null, "", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        refreshView();
    }
    
    @Listen("onClick = #saveButton")
    public void saveButtonClick() {
    	if(Strings.isBlank(nameTextbox.getText())){
			Clients.showNotification("Debe ingresar un nombre", nameTextbox);
			return;
		}
    	int selected_index = measureUnitSelectbox.getSelectedIndex();
        if(selected_index == -1) {// no hay una unidad de medida seleccionada
        	Clients.showNotification("Debe seleccionar una unidad de medida", measureUnitSelectbox);
			return;
        }
    	currentRawMaterial.setName(nameTextbox.getText());
        currentRawMaterial.setLength(new BigDecimal(lengthDoublebox.doubleValue()));
        currentRawMaterial.setDepth(new BigDecimal(depthDoublebox.doubleValue()));
        currentRawMaterial.setHeight(new BigDecimal(heightDoublebox.doubleValue()));
        currentRawMaterial.setIdMeasureUnit(measureUnitListModel.getElementAt(selected_index).getId());
    	if(currentRawMaterial.getId() == null)	{// si es nuevo
            currentRawMaterial = rawMaterialService.saveRawMaterial(currentRawMaterial);
    	} else {
    		// si es una edicion
    		currentRawMaterial = rawMaterialService.updateRawMaterial(currentRawMaterial);
    	}
    	rawMaterialList = rawMaterialService.getRawMaterialList();
        rawMaterialListModel = new ListModelList<RawMaterial>(rawMaterialList);
		currentRawMaterial = null;
        refreshView();
    }
    
    @Listen("onClick = #cancelButton")
    public void cancelButtonClick() {
    	currentRawMaterial = null;
        refreshView();
    }
    
    @Listen("onClick = #resetButton")
    public void resetButtonClick() {
        refreshView();
    }
    
    @Listen("onClick = #deleteButton")
    public void deleteButtonClick() {
    	rawMaterialService.deleteRawMaterial(currentRawMaterial);
        rawMaterialListModel.remove(currentRawMaterial);
        currentRawMaterial = null;
        refreshView();
    }
    
    @Listen("onSelect = #rawMaterialListbox")
	public void doListBoxSelect() {
		if(rawMaterialListModel.isSelectionEmpty()) {
			//just in case for the no selection
			currentRawMaterial = null;
		}else {
			if(currentRawMaterial == null) {
				currentRawMaterial = rawMaterialListModel.getSelection().iterator().next();
			}
		}
		refreshView();
	}
    
    public String getMeasureUnitName(int idMeasureUnit) {
    	return measureUnitService.getMeasureUnit(idMeasureUnit).getName();
    }
    
    private void refreshView() {
    	rawMaterialListModel.clearSelection();
    	rawMaterialListbox.setModel(rawMaterialListModel);
        if(currentRawMaterial == null) {
			//limpiar
        	rawMaterialGrid.setVisible(false);
        	nameTextbox.setValue(null);
        	lengthDoublebox.setValue(null);
        	depthDoublebox.setValue(null);
        	heightDoublebox.setValue(null);
        	measureUnitSelectbox.setSelectedIndex(-1);
        	
			saveButton.setDisabled(true);
			cancelButton.setDisabled(true);
			resetButton.setDisabled(true);
			deleteButton.setDisabled(true);
			newButton.setDisabled(false);
			rawMaterialListbox.clearSelection();
		}else {
			rawMaterialGrid.setVisible(true);
			nameTextbox.setValue(currentRawMaterial.getName());
        	lengthDoublebox.setValue(currentRawMaterial.getLength().doubleValue());
        	depthDoublebox.setValue(currentRawMaterial.getDepth().doubleValue());
        	heightDoublebox.setValue(currentRawMaterial.getHeight().doubleValue());
        	if(currentRawMaterial.getIdMeasureUnit() != null) {
        		MeasureUnitType aux = measureUnitTypeService.getMeasureUnitType(currentRawMaterial.getIdMeasureUnit());
        		measureUnitSelectbox.setSelectedIndex(aux.getId()-1);
        	}else {
        		measureUnitSelectbox.setSelectedIndex(-1);
        	}
			saveButton.setDisabled(false);
			cancelButton.setDisabled(false);
			resetButton.setDisabled(false);
			if(currentRawMaterial.getId() == null) {
                deleteButton.setDisabled(true);
            } else {
                deleteButton.setDisabled(false);
            }
			newButton.setDisabled(true);
		}
    }
}
