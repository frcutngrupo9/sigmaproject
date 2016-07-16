package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
import java.util.List;

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
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
import ar.edu.utn.sigmaproject.domain.RawMaterialType;
import ar.edu.utn.sigmaproject.service.MeasureUnitRepository;
import ar.edu.utn.sigmaproject.service.MeasureUnitTypeRepository;
import ar.edu.utn.sigmaproject.service.RawMaterialTypeRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class RawMaterialController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Listbox rawMaterialTypeListbox;
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
	Grid rawMaterialTypeGrid;

	// services
	@WireVariable
	private RawMaterialTypeRepository rawMaterialTypeRepository;
	@WireVariable
	private MeasureUnitRepository measureUnitRepository;
	@WireVariable
	private MeasureUnitTypeRepository measureUnitTypeRepository;

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
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		rawMaterialTypeList = rawMaterialTypeRepository.findAll();
		rawMaterialTypeListModel = new ListModelList<>(rawMaterialTypeList);
		rawMaterialTypeListbox.setModel(rawMaterialTypeListModel);
		currentRawMaterialType = null;
		MeasureUnitType measureUnitType = measureUnitTypeRepository.findFirstByName("Longitud");
		measureUnitList = measureUnitRepository.findByType(measureUnitType);
		lengthMeasureUnitListModel = new ListModelList<>(measureUnitList);
		depthMeasureUnitListModel = new ListModelList<>(measureUnitList);
		widthMeasureUnitListModel = new ListModelList<>(measureUnitList);
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
		rawMaterialTypeGrid.setVisible(true);
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
		BigDecimal length = BigDecimal.valueOf(lengthDoublebox.doubleValue());
		MeasureUnit lengthMeasureUnit = lengthMeasureUnitListModel.getElementAt(lengthSelectedIndex);
		BigDecimal depth = BigDecimal.valueOf(depthDoublebox.doubleValue());
		MeasureUnit depthMeasureUnit = depthMeasureUnitListModel.getElementAt(depthSelectedIndex);
		BigDecimal width = BigDecimal.valueOf(widthDoublebox.doubleValue());
		MeasureUnit widthMeasureUnit = widthMeasureUnitListModel.getElementAt(widthSelectedIndex);
		if(currentRawMaterialType == null)	{// si es nuevo
			currentRawMaterialType = new RawMaterialType(name, length, lengthMeasureUnit, depth, depthMeasureUnit, width, widthMeasureUnit);
		} else {
			// si es una edicion
			currentRawMaterialType.setName(name);
			currentRawMaterialType.setLength(length);
			currentRawMaterialType.setDepth(depth);
			currentRawMaterialType.setWidth(width);
			currentRawMaterialType.setLengthMeasureUnit(lengthMeasureUnit);
			currentRawMaterialType.setDepthMeasureUnit(depthMeasureUnit);
			currentRawMaterialType.setWidthMeasureUnit(widthMeasureUnit);
		}
		rawMaterialTypeRepository.save(currentRawMaterialType);
		rawMaterialTypeList = rawMaterialTypeRepository.findAll();
		rawMaterialTypeListModel = new ListModelList<>(rawMaterialTypeList);
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
		rawMaterialTypeGrid.setVisible(true);
	}

	@Listen("onClick = #deleteButton")
	public void deleteButtonClick() {
		rawMaterialTypeRepository.delete(currentRawMaterialType);
		rawMaterialTypeListModel.remove(currentRawMaterialType);
		currentRawMaterialType = null;
		refreshView();
	}

	@Listen("onSelect = #rawMaterialTypeListbox")
	public void doListBoxSelect() {
		if(rawMaterialTypeListModel.isSelectionEmpty()) {
			//just in case for the no selection
			currentRawMaterialType = null;
		}else {
			if(currentRawMaterialType == null) {
				currentRawMaterialType = rawMaterialTypeListbox.getSelectedItem().getValue();
				refreshView();
			}
		}
		rawMaterialTypeListModel.clearSelection();
	}

	public String getMeasureUnitName(MeasureUnit measureUnit) {
		if (measureUnit.getName() != null) {
			return measureUnit.getName();
		} else {
			return "[Sin Unidad de Medida]";
		}
	}

	private void refreshView() {
		rawMaterialTypeListModel.clearSelection();
		rawMaterialTypeListbox.setModel(rawMaterialTypeListModel);
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		newButton.setDisabled(false);
		if(currentRawMaterialType == null) {// creando
			rawMaterialTypeGrid.setVisible(false);
			nameTextbox.setValue( null);
			lengthDoublebox.setValue(null);
			depthDoublebox.setValue(null);
			widthDoublebox.setValue(null);
			// arrancamos con seleccion de metros x pulgada x pulgada
			MeasureUnit lengthMeasureUnit = measureUnitRepository.findFirstByName("Metros");
			MeasureUnit inchesMeasureUnit = measureUnitRepository.findFirstByName("Pulgadas");
			lengthMeasureUnitSelectbox.setSelectedIndex(lengthMeasureUnitListModel.indexOf(lengthMeasureUnit));
			depthMeasureUnitSelectbox.setSelectedIndex(depthMeasureUnitListModel.indexOf(inchesMeasureUnit));
			widthMeasureUnitSelectbox.setSelectedIndex(widthMeasureUnitListModel.indexOf(inchesMeasureUnit));
			deleteButton.setDisabled(true);
			resetButton.setDisabled(true);
		} else {// editando
			rawMaterialTypeGrid.setVisible(true);
			nameTextbox.setValue(currentRawMaterialType.getName());
			lengthDoublebox.setValue(currentRawMaterialType.getLength().doubleValue());
			depthDoublebox.setValue(currentRawMaterialType.getDepth().doubleValue());
			widthDoublebox.setValue(currentRawMaterialType.getWidth().doubleValue());
			MeasureUnit lengthMeasureUnit = currentRawMaterialType.getLengthMeasureUnit();
			if(lengthMeasureUnit != null) {
				lengthMeasureUnitSelectbox.setSelectedIndex(lengthMeasureUnitListModel.indexOf(lengthMeasureUnit));
			} else {
				lengthMeasureUnitSelectbox.setSelectedIndex(-1);
			}
			MeasureUnit depthMeasureUnit = currentRawMaterialType.getDepthMeasureUnit();
			if(depthMeasureUnit != null) {
				depthMeasureUnitSelectbox.setSelectedIndex(depthMeasureUnitListModel.indexOf(depthMeasureUnit));
			} else {
				depthMeasureUnitSelectbox.setSelectedIndex(-1);
			}
			MeasureUnit widthMeasureUnit = currentRawMaterialType.getWidthMeasureUnit();
			if(widthMeasureUnit != null) {
				widthMeasureUnitSelectbox.setSelectedIndex(widthMeasureUnitListModel.indexOf(widthMeasureUnit));
			} else {
				widthMeasureUnitSelectbox.setSelectedIndex(-1);
			}
			deleteButton.setDisabled(false);
			resetButton.setDisabled(false);
		}
	}
}
