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
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.MaterialReserved;
import ar.edu.utn.sigmaproject.domain.MaterialType;
import ar.edu.utn.sigmaproject.domain.MeasureUnit;
import ar.edu.utn.sigmaproject.domain.Wood;
import ar.edu.utn.sigmaproject.domain.WoodType;
import ar.edu.utn.sigmaproject.service.MaterialReservedRepository;
import ar.edu.utn.sigmaproject.service.WoodRepository;
import ar.edu.utn.sigmaproject.service.WoodTypeRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class RawMaterialStockController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Listbox woodListbox;
	@Wire
	Grid woodCreationGrid;
	@Wire
	Textbox woodNameTextbox;
	@Wire
	Combobox woodTypeCombobox;
	@Wire
	Doublebox stockDoublebox;
	@Wire
	Doublebox stockMinDoublebox;
	@Wire
	Doublebox stockRepoDoublebox;
	@Wire
	Button saveButton;
	@Wire
	Button cancelButton;
	@Wire
	Button resetButton;
	@Wire
	Listbox woodReservedListbox;

	// services
	@WireVariable
	private WoodRepository woodRepository;
	@WireVariable
	private MaterialReservedRepository materialReservedRepository;
	@WireVariable
	private WoodTypeRepository woodTypeRepository;

	// attributes
	private Wood currentWood;

	// list
	private List<Wood> woodList;
	private List<WoodType> woodTypeList;
	private List<MaterialReserved> woodReservedList;

	// list models
	private ListModelList<Wood> woodListModel;
	private ListModelList<WoodType> woodTypeListModel;
	private ListModelList<MaterialReserved> woodReservedListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		woodTypeList = woodTypeRepository.findAll();
		woodTypeListModel = new ListModelList<>(woodTypeList);
		woodTypeCombobox.setModel(woodTypeListModel);
		woodList = woodRepository.findAll();
		woodListModel = new ListModelList<>(woodList);
		woodListbox.setModel(woodListModel);
		woodReservedList = materialReservedRepository.findAllByType(MaterialType.Wood);
		woodReservedListModel = new ListModelList<>(woodReservedList);
		woodReservedListbox.setModel(woodReservedListModel);
		currentWood = null;
		refreshView();
	}

	@Listen("onClick = #searchButton")
	public void search() {
	}

	@Listen("onSelect = #woodListbox")
	public void doWoodListBoxSelect() {
		if(woodListModel.isSelectionEmpty()) {
			//just in case for the no selection
			currentWood = null;
		} else {
			if(currentWood == null) {// si no hay nada editandose
				currentWood = woodListbox.getSelectedItem().getValue();
				refreshView();
			}
		}
		woodListModel.clearSelection();
	}

	private void refreshView() {
		woodListModel.clearSelection();
		woodListbox.setModel(woodListModel);// se actualiza la lista limpiar la seleccion
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		if(currentWood == null) {// nuevo
			woodCreationGrid.setVisible(false);
			woodTypeListModel.addToSelection(woodTypeRepository.findFirstByName("Pino"));
			woodTypeCombobox.setModel(woodTypeListModel);
			woodNameTextbox.setText("");
			stockDoublebox.setValue(0.0);
			stockMinDoublebox.setValue(0.0);
			stockRepoDoublebox.setValue(0.0);
			woodTypeCombobox.setDisabled(false);
			woodNameTextbox.setDisabled(false);
			stockDoublebox.setDisabled(false);
			stockMinDoublebox.setDisabled(false);
			stockRepoDoublebox.setDisabled(false);
			resetButton.setDisabled(true);
		} else {// editar
			woodCreationGrid.setVisible(true);
			woodTypeCombobox.setSelectedIndex(woodTypeListModel.indexOf(currentWood.getWoodType()));
			woodNameTextbox.setText(currentWood.getName());
			stockDoublebox.setValue(currentWood.getStock().doubleValue());
			stockMinDoublebox.setValue(currentWood.getStockMin().doubleValue());
			stockRepoDoublebox.setValue(currentWood.getStockRepo().doubleValue());
			woodTypeCombobox.setDisabled(true);
			woodNameTextbox.setDisabled(true);
			stockDoublebox.setDisabled(true);
			stockMinDoublebox.setDisabled(false);
			stockRepoDoublebox.setDisabled(false);
			resetButton.setDisabled(false);
		}
	}

	public String getMeasureUnitName(MeasureUnit measureUnit) {
		if (measureUnit != null) {
			return measureUnit.getName();
		} else {
			return "[Sin Unidad de Medida]";
		}
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(woodTypeCombobox.getSelectedIndex() == -1) {
			Clients.showNotification("Debe seleccionar una Madera", woodTypeCombobox);
			return;
		}
		WoodType woodType = woodTypeCombobox.getSelectedItem().getValue();
		BigDecimal stock = BigDecimal.valueOf(stockDoublebox.getValue());
		BigDecimal stockMin = BigDecimal.valueOf(stockMinDoublebox.getValue());
		BigDecimal stockRepo = BigDecimal.valueOf(stockRepoDoublebox.getValue());
		currentWood.setWoodType(woodType);
		currentWood.setStock(stock);
		currentWood.setStockRepo(stockRepo);
		currentWood.setStockMin(stockMin);
		currentWood = woodRepository.save(currentWood);
		woodList = woodRepository.findAll();
		woodListModel = new ListModelList<>(woodList);
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
	}
}
