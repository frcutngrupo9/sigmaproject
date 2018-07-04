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
import java.util.HashMap;
import java.util.Map;

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.service.SupplyTypeRepository;
import ar.edu.utn.sigmaproject.util.SortingPagingHelper;

public class SupplyController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Button searchButton;
	@Wire
	Listbox supplyTypeListbox;
	@Wire
	Paging pager;
	@Wire
	Button newButton;
	@Wire
	Grid supplyTypeGrid;
	@Wire
	Textbox codeTextbox;
	@Wire
	Textbox descriptionTextbox;
	@Wire
	Textbox detailsTextbox;
	@Wire
	Textbox brandTextbox;
	@Wire
	Textbox presentationTextbox;
	@Wire
	Textbox measureTextbox;
	@Wire
	Doublebox priceDoublebox;
	@Wire
	Button saveButton;
	@Wire
	Button cancelButton;
	@Wire
	Button resetButton;
	@Wire
	Button deleteButton;

	// services
	@WireVariable
	private SupplyTypeRepository supplyTypeRepository;

	// attributes
	private SupplyType currentSupplyType;
	private SortingPagingHelper<SupplyType> sortingPagingHelper;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		Map<Integer, String> sortProperties = new HashMap<>();
		sortProperties.put(0, "code");
		sortProperties.put(1, "description");
		sortProperties.put(3, "brand");
		sortProperties.put(4, "presentation");
		sortingPagingHelper = new SortingPagingHelper<>(supplyTypeRepository, supplyTypeListbox, searchButton, searchTextbox, pager, sortProperties);
		currentSupplyType = null;

		refreshView();
	}

	@Listen("onClick = #searchButton")
	public void search() {
	}

	@Listen("onClick = #newButton")
	public void newButtonClick() {
		currentSupplyType = null;
		refreshView();
		supplyTypeGrid.setVisible(true);
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(Strings.isBlank(descriptionTextbox.getText())){
			Clients.showNotification("Debe ingresar una descripcion", descriptionTextbox);
			return;
		}
		String code = codeTextbox.getText();
		String description = descriptionTextbox.getText().toUpperCase();
		String details = detailsTextbox.getText();
		String brand = brandTextbox.getText();
		String presentation = presentationTextbox.getText();
		String measure = measureTextbox.getText();
		BigDecimal price = BigDecimal.valueOf(priceDoublebox.doubleValue());
		if(currentSupplyType == null) {
			// es un nuevo insumo
			currentSupplyType = new SupplyType(code, description, details, brand, presentation, measure, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, price);
		} else {
			// es una edicion
			currentSupplyType.setCode(code);
			currentSupplyType.setDescription(description);
			currentSupplyType.setDetails(details);
			currentSupplyType.setBrand(brand);
			currentSupplyType.setPresentation(presentation);
			currentSupplyType.setMeasure(measure);
			currentSupplyType.setPrice(price);
		}
		currentSupplyType = supplyTypeRepository.save(currentSupplyType);
		sortingPagingHelper.reset();
		currentSupplyType = null;
		refreshView();
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		currentSupplyType = null;
		refreshView();
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
	}

	@Listen("onClick = #deleteButton")
	public void deleteButtonClick() {
		supplyTypeRepository.delete(currentSupplyType);
		sortingPagingHelper.reset();
		currentSupplyType = null;
		refreshView();
	}

	@Listen("onSelect = #supplyTypeListbox")
	public void doListBoxSelect() {
		if(supplyTypeListbox.getSelectedItem() == null) {
			//just in case for the no selection
			currentSupplyType = null;
		} else {
			if(currentSupplyType == null) {// si no hay nada editandose
				currentSupplyType = supplyTypeListbox.getSelectedItem().getValue();
				refreshView();
			}
		}
		supplyTypeListbox.clearSelection();
	}

	private void refreshView() {
		supplyTypeListbox.clearSelection();
		sortingPagingHelper.reset();// se actualiza la lista
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		newButton.setDisabled(false);
		if(currentSupplyType == null) {// creando
			supplyTypeGrid.setVisible(false);
			codeTextbox.setValue(null);
			descriptionTextbox.setValue(null);
			detailsTextbox.setValue(null);
			brandTextbox.setValue(null);
			presentationTextbox.setValue(null);
			measureTextbox.setValue(null);
			deleteButton.setDisabled(true);
			resetButton.setDisabled(true);// al crear, el boton new cumple la misma funcion q el reset
			priceDoublebox.setValue(null);
		} else {// editando
			supplyTypeGrid.setVisible(true);
			codeTextbox.setValue(currentSupplyType.getCode());
			descriptionTextbox.setValue(currentSupplyType.getDescription());
			detailsTextbox.setValue(currentSupplyType.getDetails());
			brandTextbox.setValue(currentSupplyType.getBrand());
			presentationTextbox.setValue(currentSupplyType.getPresentation());
			measureTextbox.setValue(currentSupplyType.getMeasure());
			deleteButton.setDisabled(false);
			resetButton.setDisabled(false);
			if(currentSupplyType.getPrice() != null) {
				priceDoublebox.setValue(currentSupplyType.getPrice().doubleValue());
			} else {
				priceDoublebox.setValue(null);
			}
		}
	}
}
