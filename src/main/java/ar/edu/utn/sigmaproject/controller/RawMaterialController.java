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
import ar.edu.utn.sigmaproject.domain.RawMaterialType;
import ar.edu.utn.sigmaproject.service.ClientService;
import ar.edu.utn.sigmaproject.service.MeasureUnitService;
import ar.edu.utn.sigmaproject.service.MeasureUnitTypeService;
import ar.edu.utn.sigmaproject.service.OrderDetailService;
import ar.edu.utn.sigmaproject.service.OrderService;
import ar.edu.utn.sigmaproject.service.OrderStateService;
import ar.edu.utn.sigmaproject.service.OrderStateTypeService;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.RawMaterialTypeService;
import ar.edu.utn.sigmaproject.service.impl.ClientServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.MeasureUnitServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.MeasureUnitTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderDetailServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderStateServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderStateTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.RawMaterialTypeServiceImpl;

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
    private RawMaterialTypeService rawMaterialTypeService = new RawMaterialTypeServiceImpl();
    private MeasureUnitService measureUnitService = new MeasureUnitServiceImpl();
    private MeasureUnitTypeService measureUnitTypeService = new MeasureUnitTypeServiceImpl();
    
    // atributes
    private RawMaterialType currentRawMaterialType;
    
    // list
    private List<RawMaterialType> rawMaterialTypeList;
    private List<MeasureUnit> measureUnitlList;
    
    // list models
    private ListModelList<RawMaterialType> rawMaterialTypeListModel;
    private ListModelList<MeasureUnit> measureUnitListModel;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception{
        super.doAfterCompose(comp);
        rawMaterialTypeList = rawMaterialTypeService.getRawMaterialTypeList();
        rawMaterialTypeListModel = new ListModelList<RawMaterialType>(rawMaterialTypeList);
        rawMaterialListbox.setModel(rawMaterialTypeListModel);
        currentRawMaterialType = null;
        
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
        currentRawMaterialType = new RawMaterialType(null, null, "", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
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
    	currentRawMaterialType.setName(nameTextbox.getText());
        currentRawMaterialType.setLength(new BigDecimal(lengthDoublebox.doubleValue()));
        currentRawMaterialType.setDepth(new BigDecimal(depthDoublebox.doubleValue()));
        currentRawMaterialType.setHeight(new BigDecimal(heightDoublebox.doubleValue()));
        currentRawMaterialType.setIdMeasureUnit(measureUnitListModel.getElementAt(selected_index).getId());
    	if(currentRawMaterialType.getId() == null)	{// si es nuevo
            currentRawMaterialType = rawMaterialTypeService.saveRawMaterialType(currentRawMaterialType);
    	} else {
    		// si es una edicion
    		currentRawMaterialType = rawMaterialTypeService.updateRawMaterialType(currentRawMaterialType);
    	}
    	rawMaterialTypeList = rawMaterialTypeService.getRawMaterialTypeList();
        rawMaterialTypeListModel = new ListModelList<RawMaterialType>(rawMaterialTypeList);
		currentRawMaterialType = null;
        refreshView();
    }
    
    @Listen("onClick = #cancelButton")
    public void cancelButtonClick() {
    	currentRawMaterialType = null;
        refreshView();
    }
    
    @Listen("onClick = #resetButton")
    public void resetButtonClick() {
        refreshView();
    }
    
    @Listen("onClick = #deleteButton")
    public void deleteButtonClick() {
    	rawMaterialTypeService.deleteRawMaterialType(currentRawMaterialType);
        rawMaterialTypeListModel.remove(currentRawMaterialType);
        currentRawMaterialType = null;
        refreshView();
    }
    
    @Listen("onSelect = #rawMaterialListbox")
	public void doListBoxSelect() {
		if(rawMaterialTypeListModel.isSelectionEmpty()) {
			//just in case for the no selection
			currentRawMaterialType = null;
		}else {
			if(currentRawMaterialType == null) {
				currentRawMaterialType = rawMaterialTypeListModel.getSelection().iterator().next();
			}
		}
		refreshView();
	}
    
    public String getMeasureUnitName(int idMeasureUnit) {
    	return measureUnitService.getMeasureUnit(idMeasureUnit).getName();
    }
    
    private void refreshView() {
    	rawMaterialTypeListModel.clearSelection();
    	rawMaterialListbox.setModel(rawMaterialTypeListModel);
        if(currentRawMaterialType == null) {
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
			nameTextbox.setValue(currentRawMaterialType.getName());
        	lengthDoublebox.setValue(currentRawMaterialType.getLength().doubleValue());
        	depthDoublebox.setValue(currentRawMaterialType.getDepth().doubleValue());
        	heightDoublebox.setValue(currentRawMaterialType.getHeight().doubleValue());
        	if(currentRawMaterialType.getIdMeasureUnit() != null) {
        		MeasureUnitType aux = measureUnitTypeService.getMeasureUnitType(currentRawMaterialType.getIdMeasureUnit());
        		measureUnitSelectbox.setSelectedIndex(aux.getId()-1);
        	}else {
        		measureUnitSelectbox.setSelectedIndex(-1);
        	}
			saveButton.setDisabled(false);
			cancelButton.setDisabled(false);
			resetButton.setDisabled(false);
			if(currentRawMaterialType.getId() == null) {
                deleteButton.setDisabled(true);
            } else {
                deleteButton.setDisabled(false);
            }
			newButton.setDisabled(true);
		}
    }
}
