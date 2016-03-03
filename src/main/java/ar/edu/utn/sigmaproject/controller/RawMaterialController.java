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

import ar.edu.utn.sigmaproject.domain.MeasureUnit;
import ar.edu.utn.sigmaproject.domain.RawMaterialType;
import ar.edu.utn.sigmaproject.service.MeasureUnitService;
import ar.edu.utn.sigmaproject.service.MeasureUnitTypeService;
import ar.edu.utn.sigmaproject.service.RawMaterialTypeService;
import ar.edu.utn.sigmaproject.service.impl.MeasureUnitServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.MeasureUnitTypeServiceImpl;
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
	Doublebox widthDoublebox;
	@Wire
	Selectbox lengthMeasureUnitSelectbox;
	@Wire
	Selectbox depthMeasureUnitSelectbox;
	@Wire
	Selectbox widthMeasureUnitSelectbox;
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
	private ListModelList<MeasureUnit> widthMeasureUnitListModel;

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
		widthMeasureUnitListModel = new ListModelList<MeasureUnit>(measureUnitList);
		lengthMeasureUnitSelectbox.setModel(lengthMeasureUnitListModel);
		depthMeasureUnitSelectbox.setModel(depthMeasureUnitListModel);
		widthMeasureUnitSelectbox.setModel(widthMeasureUnitListModel);
		refreshView();
	}

	@Listen("onClick = #searchButton")
	public void search() {
		//System.out.println("indice del measure elegido" + measureUnitSelectBox.getSelectedIndex());
	}

	@Listen("onClick = #newButton")
	public void newRawMaterial() {
		currentRawMaterialType = null;
		refreshView();
		rawMaterialGrid.setVisible(true);
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(Strings.isBlank(nameTextbox.getText())){
			Clients.showNotification("Debe ingresar un nombre", nameTextbox);
			return;
		}
		int lengthSelectedIndex = lengthMeasureUnitSelectbox.getSelectedIndex();
		if(lengthSelectedIndex == -1) {// no hay una unidad de medida seleccionada
			Clients.showNotification("Debe seleccionar una unidad de medida", lengthMeasureUnitSelectbox);
			return;
		}
		int depthSelectedIndex = depthMeasureUnitSelectbox.getSelectedIndex();
		if(depthSelectedIndex == -1) {// no hay una unidad de medida seleccionada
			Clients.showNotification("Debe seleccionar una unidad de medida", depthMeasureUnitSelectbox);
			return;
		}
		int widthSelectedIndex = widthMeasureUnitSelectbox.getSelectedIndex();
		if(widthSelectedIndex == -1) {// no hay una unidad de medida seleccionada
			Clients.showNotification("Debe seleccionar una unidad de medida", widthMeasureUnitSelectbox);
			return;
		}
		String name = nameTextbox.getText();
		Double length = lengthDoublebox.doubleValue();
		Integer lengthIdMeasureUnit = lengthMeasureUnitListModel.getElementAt(lengthSelectedIndex).getId();
		Double depth = depthDoublebox.doubleValue();
		Integer depthIdMeasureUnit = depthMeasureUnitListModel.getElementAt(depthSelectedIndex).getId();
		Double width = widthDoublebox.doubleValue();
		Integer widthIdMeasureUnit = widthMeasureUnitListModel.getElementAt(widthSelectedIndex).getId();
		if(currentRawMaterialType == null)	{// si es nuevo
			currentRawMaterialType = new RawMaterialType(null, name, length, lengthIdMeasureUnit, depth, depthIdMeasureUnit, width, widthIdMeasureUnit);
			currentRawMaterialType = rawMaterialTypeService.saveRawMaterialType(currentRawMaterialType);
		} else {
			// si es una edicion
			currentRawMaterialType.setName(name);
			currentRawMaterialType.setLength(length);
			currentRawMaterialType.setDepth(depth);
			currentRawMaterialType.setWidth(width);
			currentRawMaterialType.setLengthIdMeasureUnit(lengthIdMeasureUnit);
			currentRawMaterialType.setDepthIdMeasureUnit(depthIdMeasureUnit);
			currentRawMaterialType.setWidthIdMeasureUnit(widthIdMeasureUnit);
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
		rawMaterialGrid.setVisible(true);
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
				refreshView();
			}
		}
		rawMaterialTypeListModel.clearSelection();
	}

	public String getMeasureUnitName(int idMeasureUnit) {
		if(measureUnitService.getMeasureUnit(idMeasureUnit) != null) {
			return measureUnitService.getMeasureUnit(idMeasureUnit).getName();
		} else {
			return "[Sin Unidad de Medida]";
		}
	}

	private void refreshView() {
		rawMaterialTypeListModel.clearSelection();
		rawMaterialListbox.setModel(rawMaterialTypeListModel);
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		if(currentRawMaterialType == null) {// creando
			rawMaterialGrid.setVisible(false);
			nameTextbox.setValue( null);
			lengthDoublebox.setValue(null);
			depthDoublebox.setValue(null);
			widthDoublebox.setValue(null);
			// arrancamos con seleccion de metros x pulgada x pulgada
			Integer lengthIdMeasureUnit = measureUnitService.getMeasureUnit("Metros").getId();
			Integer depthIdMeasureUnit = measureUnitService.getMeasureUnit("Pulgadas").getId();
			Integer widthIdMeasureUnit = measureUnitService.getMeasureUnit("Pulgadas").getId();
			lengthMeasureUnitSelectbox.setSelectedIndex(lengthMeasureUnitListModel.indexOf(measureUnitService.getMeasureUnit(lengthIdMeasureUnit)));
			depthMeasureUnitSelectbox.setSelectedIndex(depthMeasureUnitListModel.indexOf(measureUnitService.getMeasureUnit(depthIdMeasureUnit)));
			widthMeasureUnitSelectbox.setSelectedIndex(widthMeasureUnitListModel.indexOf(measureUnitService.getMeasureUnit(widthIdMeasureUnit)));
			deleteButton.setDisabled(true);
			resetButton.setDisabled(true);
			newButton.setDisabled(false);
		} else {// editando
			rawMaterialGrid.setVisible(true);
			nameTextbox.setValue(currentRawMaterialType.getName());
			lengthDoublebox.setValue(currentRawMaterialType.getLength());
			depthDoublebox.setValue(currentRawMaterialType.getDepth());
			widthDoublebox.setValue(currentRawMaterialType.getWidth());
			Integer lengthIdMeasureUnit = currentRawMaterialType.getLengthIdMeasureUnit();
			if(lengthIdMeasureUnit != null) {
				MeasureUnit aux = measureUnitService.getMeasureUnit(lengthIdMeasureUnit);
				lengthMeasureUnitSelectbox.setSelectedIndex(lengthMeasureUnitListModel.indexOf(aux));
			} else {
				lengthMeasureUnitSelectbox.setSelectedIndex(-1);
			}
			Integer depthIdMeasureUnit = currentRawMaterialType.getDepthIdMeasureUnit();
			if(depthIdMeasureUnit != null) {
				MeasureUnit aux = measureUnitService.getMeasureUnit(depthIdMeasureUnit);
				depthMeasureUnitSelectbox.setSelectedIndex(depthMeasureUnitListModel.indexOf(aux));
			} else {
				depthMeasureUnitSelectbox.setSelectedIndex(-1);
			}
			Integer widthIdMeasureUnit = currentRawMaterialType.getWidthIdMeasureUnit();
			if(widthIdMeasureUnit != null) {
				MeasureUnit aux = measureUnitService.getMeasureUnit(widthIdMeasureUnit);
				widthMeasureUnitSelectbox.setSelectedIndex(widthMeasureUnitListModel.indexOf(aux));
			} else {
				widthMeasureUnitSelectbox.setSelectedIndex(-1);
			}
			deleteButton.setDisabled(false);
			resetButton.setDisabled(false);
			newButton.setDisabled(true);
		}
	}
}
