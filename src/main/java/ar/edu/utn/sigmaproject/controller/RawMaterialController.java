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
    Selectbox lengthMeasureUnitSelectbox;
    @Wire
    Selectbox depthMeasureUnitSelectbox;
    @Wire
    Selectbox heightMeasureUnitSelectbox;
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
    private List<MeasureUnit> measureUnitList;
    
    // list models
    private ListModelList<RawMaterialType> rawMaterialTypeListModel;
    private ListModelList<MeasureUnit> lengthMeasureUnitListModel;
    private ListModelList<MeasureUnit> depthMeasureUnitListModel;
    private ListModelList<MeasureUnit> heightMeasureUnitListModel;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception{
        super.doAfterCompose(comp);
        rawMaterialTypeList = rawMaterialTypeService.getRawMaterialTypeList();
        rawMaterialTypeListModel = new ListModelList<RawMaterialType>(rawMaterialTypeList);
        rawMaterialListbox.setModel(rawMaterialTypeListModel);
        currentRawMaterialType = null;
        
        Integer idMeasureUnitType = measureUnitTypeService.getMeasureUnitType("Longitud").getId();
        measureUnitList = measureUnitService.getMeasureUnitList(idMeasureUnitType);
        lengthMeasureUnitListModel = new ListModelList<MeasureUnit>(measureUnitList);
        depthMeasureUnitListModel = new ListModelList<MeasureUnit>(measureUnitList);
        heightMeasureUnitListModel = new ListModelList<MeasureUnit>(measureUnitList);
        lengthMeasureUnitSelectbox.setModel(lengthMeasureUnitListModel);
        depthMeasureUnitSelectbox.setModel(depthMeasureUnitListModel);
        heightMeasureUnitSelectbox.setModel(heightMeasureUnitListModel);
        refreshView();
    }

    @Listen("onClick = #searchButton")
    public void search() {
    	//System.out.println("indice del measure elegido" + measureUnitSelectBox.getSelectedIndex());
    }
    
    @Listen("onClick = #newButton")
    public void newRawMaterial() {
    	// arrancamos con seleccion de metros x pulgada x pulgada
    	Integer length_id_measure_unit = measureUnitService.getMeasureUnit("Metros").getId();
    	Integer depth_id_measure_unit = measureUnitService.getMeasureUnit("Pulgadas").getId();
    	Integer height_id_measure_unit = measureUnitService.getMeasureUnit("Pulgadas").getId();
        currentRawMaterialType = new RawMaterialType(null, "", BigDecimal.ZERO, length_id_measure_unit, BigDecimal.ZERO, depth_id_measure_unit, BigDecimal.ZERO, height_id_measure_unit);
        refreshView();
    }
    
    @Listen("onClick = #saveButton")
    public void saveButtonClick() {
    	if(Strings.isBlank(nameTextbox.getText())){
			Clients.showNotification("Debe ingresar un nombre", nameTextbox);
			return;
		}
    	int selected_index_length = lengthMeasureUnitSelectbox.getSelectedIndex();
        if(selected_index_length == -1) {// no hay una unidad de medida seleccionada
        	Clients.showNotification("Debe seleccionar una unidad de medida", lengthMeasureUnitSelectbox);
			return;
        }
        int selected_index_depth = depthMeasureUnitSelectbox.getSelectedIndex();
        if(selected_index_depth == -1) {// no hay una unidad de medida seleccionada
        	Clients.showNotification("Debe seleccionar una unidad de medida", depthMeasureUnitSelectbox);
			return;
        }
        int selected_index_height = heightMeasureUnitSelectbox.getSelectedIndex();
        if(selected_index_height == -1) {// no hay una unidad de medida seleccionada
        	Clients.showNotification("Debe seleccionar una unidad de medida", heightMeasureUnitSelectbox);
			return;
        }
    	currentRawMaterialType.setName(nameTextbox.getText());
        currentRawMaterialType.setLength(new BigDecimal(lengthDoublebox.doubleValue()));
        currentRawMaterialType.setDepth(new BigDecimal(depthDoublebox.doubleValue()));
        currentRawMaterialType.setHeight(new BigDecimal(heightDoublebox.doubleValue()));
        currentRawMaterialType.setLengthIdMeasureUnit(lengthMeasureUnitListModel.getElementAt(selected_index_length).getId());
        currentRawMaterialType.setDepthIdMeasureUnit(depthMeasureUnitListModel.getElementAt(selected_index_depth).getId());
        currentRawMaterialType.setHeightIdMeasureUnit(heightMeasureUnitListModel.getElementAt(selected_index_height).getId());
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
            lengthMeasureUnitSelectbox.setSelectedIndex(-1);
            depthMeasureUnitSelectbox.setSelectedIndex(-1);
            heightMeasureUnitSelectbox.setSelectedIndex(-1);
        	
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
        	
        	Integer length_id_measure_unit = currentRawMaterialType.getLengthIdMeasureUnit();
        	if(length_id_measure_unit != null) {
        		MeasureUnit aux = measureUnitService.getMeasureUnit(length_id_measure_unit);
        		lengthMeasureUnitSelectbox.setSelectedIndex(lengthMeasureUnitListModel.indexOf(aux));
        	} else {
        		lengthMeasureUnitSelectbox.setSelectedIndex(-1);
        	}
        	Integer depth_id_measure_unit = currentRawMaterialType.getDepthIdMeasureUnit();
        	if(depth_id_measure_unit != null) {
        		MeasureUnit aux = measureUnitService.getMeasureUnit(depth_id_measure_unit);
        		depthMeasureUnitSelectbox.setSelectedIndex(depthMeasureUnitListModel.indexOf(aux));
        	} else {
        		depthMeasureUnitSelectbox.setSelectedIndex(-1);
        	}
        	Integer height_id_measure_unit = currentRawMaterialType.getHeightIdMeasureUnit();
        	if(height_id_measure_unit != null) {
        		MeasureUnit aux = measureUnitService.getMeasureUnit(height_id_measure_unit);
        		heightMeasureUnitSelectbox.setSelectedIndex(heightMeasureUnitListModel.indexOf(aux));
        	} else {
        		heightMeasureUnitSelectbox.setSelectedIndex(-1);
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
