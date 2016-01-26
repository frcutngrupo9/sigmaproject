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
	private ListModelList<MeasureUnit> heightMeasureUnitListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		rawMaterialTypeList = rawMaterialTypeRepository.findAll();
		rawMaterialTypeListModel = new ListModelList<RawMaterialType>(rawMaterialTypeList);
		rawMaterialListbox.setModel(rawMaterialTypeListModel);
		currentRawMaterialType = null;

		MeasureUnitType measureUnitType = measureUnitTypeRepository.findByName("Longitud");
		measureUnitList = measureUnitType.getMeasureUnits();
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
		// System.out.println("indice del measure elegido" +
		// measureUnitSelectBox.getSelectedIndex());
	}

	@Listen("onClick = #newButton")
	public void newRawMaterial() {
		// arrancamos con seleccion de metros x pulgada x pulgada
		MeasureUnit lengthMeasureUnit = measureUnitRepository.findByName("Metros");
		MeasureUnit depthMeasureUnit = measureUnitRepository.findByName("Pulgadas");
		MeasureUnit heightMeasureUnit = measureUnitRepository.findByName("Pulgadas");
		currentRawMaterialType = new RawMaterialType("", BigDecimal.ZERO, lengthMeasureUnit, BigDecimal.ZERO,
				depthMeasureUnit, BigDecimal.ZERO, heightMeasureUnit);
		refreshView();
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if (Strings.isBlank(nameTextbox.getText())) {
			Clients.showNotification("Debe ingresar un nombre", nameTextbox);
			return;
		}
		int selected_index_length = lengthMeasureUnitSelectbox.getSelectedIndex();
		if (selected_index_length == -1) {// no hay una unidad de medida
											// seleccionada
			Clients.showNotification("Debe seleccionar una unidad de medida", lengthMeasureUnitSelectbox);
			return;
		}
		int selected_index_depth = depthMeasureUnitSelectbox.getSelectedIndex();
		if (selected_index_depth == -1) {// no hay una unidad de medida
											// seleccionada
			Clients.showNotification("Debe seleccionar una unidad de medida", depthMeasureUnitSelectbox);
			return;
		}
		int selected_index_height = heightMeasureUnitSelectbox.getSelectedIndex();
		if (selected_index_height == -1) {// no hay una unidad de medida
											// seleccionada
			Clients.showNotification("Debe seleccionar una unidad de medida", heightMeasureUnitSelectbox);
			return;
		}
		currentRawMaterialType.setName(nameTextbox.getText());
		currentRawMaterialType.setLength(new BigDecimal(lengthDoublebox.doubleValue()));
		currentRawMaterialType.setDepth(new BigDecimal(depthDoublebox.doubleValue()));
		currentRawMaterialType.setHeight(new BigDecimal(heightDoublebox.doubleValue()));
		currentRawMaterialType.setLengthMeasureUnit(lengthMeasureUnitListModel.getElementAt(selected_index_length));
		currentRawMaterialType.setDepthMeasureUnit(depthMeasureUnitListModel.getElementAt(selected_index_depth));
		currentRawMaterialType.setHeightMeasureUnit(heightMeasureUnitListModel.getElementAt(selected_index_height));
		currentRawMaterialType = rawMaterialTypeRepository.save(currentRawMaterialType);
		rawMaterialTypeList = rawMaterialTypeRepository.findAll();
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
		rawMaterialTypeRepository.delete(currentRawMaterialType);
		rawMaterialTypeListModel.remove(currentRawMaterialType);
		currentRawMaterialType = null;
		refreshView();
	}

	@Listen("onSelect = #rawMaterialListbox")
	public void doListBoxSelect() {
		if (rawMaterialTypeListModel.isSelectionEmpty()) {
			// just in case for the no selection
			currentRawMaterialType = null;
		} else {
			if (currentRawMaterialType == null) {
				currentRawMaterialType = rawMaterialTypeListModel.getSelection().iterator().next();
			}
		}
		refreshView();
	}

	private void refreshView() {
		rawMaterialTypeListModel.clearSelection();
		rawMaterialListbox.setModel(rawMaterialTypeListModel);
		if (currentRawMaterialType == null) {
			// limpiar
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
		} else {
			rawMaterialGrid.setVisible(true);
			nameTextbox.setValue(currentRawMaterialType.getName());
			lengthDoublebox.setValue(currentRawMaterialType.getLength().doubleValue());
			depthDoublebox.setValue(currentRawMaterialType.getDepth().doubleValue());
			heightDoublebox.setValue(currentRawMaterialType.getHeight().doubleValue());

			if (currentRawMaterialType.getLengthMeasureUnit() != null) {
				lengthMeasureUnitSelectbox.setSelectedIndex(
						lengthMeasureUnitListModel.indexOf(currentRawMaterialType.getLengthMeasureUnit()));
			} else {
				lengthMeasureUnitSelectbox.setSelectedIndex(-1);
			}
			if (currentRawMaterialType.getDepthMeasureUnit() != null) {
				depthMeasureUnitSelectbox.setSelectedIndex(
						depthMeasureUnitListModel.indexOf(currentRawMaterialType.getDepthMeasureUnit()));
			} else {
				depthMeasureUnitSelectbox.setSelectedIndex(-1);
			}
			if (currentRawMaterialType.getHeightMeasureUnit() != null) {
				heightMeasureUnitSelectbox.setSelectedIndex(
						heightMeasureUnitListModel.indexOf(currentRawMaterialType.getHeightMeasureUnit()));
			} else {
				heightMeasureUnitSelectbox.setSelectedIndex(-1);
			}
			saveButton.setDisabled(false);
			cancelButton.setDisabled(false);
			resetButton.setDisabled(false);
			if (currentRawMaterialType.getId() == null) {
				deleteButton.setDisabled(true);
			} else {
				deleteButton.setDisabled(false);
			}
			newButton.setDisabled(true);
		}
	}
}
