/*
 * The MIT License
 *
 * Copyright (C) 2017 SigmaProject.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Selectbox;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.MeasureUnit;
import ar.edu.utn.sigmaproject.domain.MeasureUnitType;
import ar.edu.utn.sigmaproject.domain.Wood;
import ar.edu.utn.sigmaproject.domain.WoodType;
import ar.edu.utn.sigmaproject.service.MeasureUnitRepository;
import ar.edu.utn.sigmaproject.service.MeasureUnitTypeRepository;
import ar.edu.utn.sigmaproject.service.WoodRepository;
import ar.edu.utn.sigmaproject.service.WoodTypeRepository;

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
	@Wire
	Combobox woodTypeCombobox;

	// services
	@WireVariable
	private WoodRepository woodRepository;
	@WireVariable
	private MeasureUnitRepository measureUnitRepository;
	@WireVariable
	private MeasureUnitTypeRepository measureUnitTypeRepository;
	@WireVariable
	private WoodTypeRepository woodTypeRepository;

	// atributes
	private Wood currentWood;

	// list
	private List<Wood> rawMaterialTypeList;
	private List<MeasureUnit> measureUnitList;
	private List<WoodType> woodTypeList;

	// list models
	private ListModelList<Wood> rawMaterialTypeListModel;
	private ListModelList<MeasureUnit> lengthMeasureUnitListModel;
	private ListModelList<MeasureUnit> depthMeasureUnitListModel;
	private ListModelList<MeasureUnit> widthMeasureUnitListModel;
	private ListModelList<WoodType> woodTypeListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		woodTypeList = woodTypeRepository.findAll();
		woodTypeListModel = new ListModelList<>(woodTypeList);
		woodTypeCombobox.setModel(woodTypeListModel);
		rawMaterialTypeList = woodRepository.findAll();
		rawMaterialTypeListModel = new ListModelList<>(rawMaterialTypeList);
		rawMaterialTypeListbox.setModel(rawMaterialTypeListModel);
		currentWood = null;
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
	public void newButtonClick() {
		currentWood = null;
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
			Clients.showNotification("Debe seleccionar una unidad de medida.", lengthMeasureUnitSelectbox);
			return;
		}
		int depthSelectedIndex = depthMeasureUnitSelectbox.getSelectedIndex();
		if(depthSelectedIndex == -1) {// no hay una unidad de medida seleccionada
			Clients.showNotification("Debe seleccionar una unidad de medida.", depthMeasureUnitSelectbox);
			return;
		}
		int widthSelectedIndex = widthMeasureUnitSelectbox.getSelectedIndex();
		if(widthSelectedIndex == -1) {// no hay una unidad de medida seleccionada
			Clients.showNotification("Debe seleccionar una unidad de medida.", widthMeasureUnitSelectbox);
			return;
		}
		if(woodTypeCombobox.getSelectedIndex() == -1) {
			Clients.showNotification("Debe seleccionar un tipo de madera.", woodTypeCombobox);
			return;
		}
		String name = nameTextbox.getText();
		BigDecimal length = BigDecimal.valueOf(lengthDoublebox.doubleValue());
		MeasureUnit lengthMeasureUnit = lengthMeasureUnitListModel.getElementAt(lengthSelectedIndex);
		BigDecimal depth = BigDecimal.valueOf(depthDoublebox.doubleValue());
		MeasureUnit depthMeasureUnit = depthMeasureUnitListModel.getElementAt(depthSelectedIndex);
		BigDecimal width = BigDecimal.valueOf(widthDoublebox.doubleValue());
		MeasureUnit widthMeasureUnit = widthMeasureUnitListModel.getElementAt(widthSelectedIndex);
		WoodType woodType = woodTypeCombobox.getSelectedItem().getValue();
		if(currentWood == null)	{// si es nuevo
			currentWood = new Wood(name, length, lengthMeasureUnit, depth, depthMeasureUnit, width, widthMeasureUnit, woodType, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
		} else {
			// si es una edicion
			currentWood.setWoodType(woodType);
			currentWood.setName(name);
			currentWood.setLength(length);
			currentWood.setDepth(depth);
			currentWood.setWidth(width);
			currentWood.setLengthMeasureUnit(lengthMeasureUnit);
			currentWood.setDepthMeasureUnit(depthMeasureUnit);
			currentWood.setWidthMeasureUnit(widthMeasureUnit);
		}
		woodRepository.save(currentWood);
		rawMaterialTypeList = woodRepository.findAll();
		rawMaterialTypeListModel = new ListModelList<>(rawMaterialTypeList);
		currentWood = null;
		refreshView();
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		currentWood = null;
		refreshView();
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
		rawMaterialTypeGrid.setVisible(true);
	}

	@Listen("onClick = #deleteButton")
	public void deleteButtonClick() {
		woodRepository.delete(currentWood);
		rawMaterialTypeListModel.remove(currentWood);
		currentWood = null;
		refreshView();
	}

	@Listen("onSelect = #rawMaterialTypeListbox")
	public void doListBoxSelect() {
		if(rawMaterialTypeListModel.isSelectionEmpty()) {
			//just in case for the no selection
			currentWood = null;
		}else {
			if(currentWood == null) {
				currentWood = rawMaterialTypeListbox.getSelectedItem().getValue();
				refreshView();
			}
		}
		rawMaterialTypeListModel.clearSelection();
	}

	private void refreshView() {
		rawMaterialTypeListModel.clearSelection();
		rawMaterialTypeListbox.setModel(rawMaterialTypeListModel);
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		newButton.setDisabled(false);
		if(currentWood == null) {// creando
			rawMaterialTypeGrid.setVisible(false);
			woodTypeListModel.addToSelection(woodTypeRepository.findFirstByName("Pino"));
			woodTypeCombobox.setModel(woodTypeListModel);
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
			woodTypeCombobox.setSelectedIndex(woodTypeListModel.indexOf(currentWood.getWoodType()));
			nameTextbox.setValue(currentWood.getName());
			lengthDoublebox.setValue(currentWood.getLength().doubleValue());
			depthDoublebox.setValue(currentWood.getDepth().doubleValue());
			widthDoublebox.setValue(currentWood.getWidth().doubleValue());
			MeasureUnit lengthMeasureUnit = currentWood.getLengthMeasureUnit();
			if(lengthMeasureUnit != null) {
				lengthMeasureUnitSelectbox.setSelectedIndex(lengthMeasureUnitListModel.indexOf(lengthMeasureUnit));
			} else {
				lengthMeasureUnitSelectbox.setSelectedIndex(-1);
			}
			MeasureUnit depthMeasureUnit = currentWood.getDepthMeasureUnit();
			if(depthMeasureUnit != null) {
				depthMeasureUnitSelectbox.setSelectedIndex(depthMeasureUnitListModel.indexOf(depthMeasureUnit));
			} else {
				depthMeasureUnitSelectbox.setSelectedIndex(-1);
			}
			MeasureUnit widthMeasureUnit = currentWood.getWidthMeasureUnit();
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
