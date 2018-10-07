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
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Messagebox;

import ar.edu.utn.sigmaproject.domain.ToolType;
import ar.edu.utn.sigmaproject.service.ToolTypeRepository;
import ar.edu.utn.sigmaproject.util.SortingPagingHelper;

public class ToolController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Button searchButton;
	@Wire
	Listbox toolTypeListbox;
	@Wire
	Paging pager;
	@Wire
	Button newButton;
	@Wire
	Grid toolTypeGrid;
	@Wire
	Button saveButton;
	@Wire
	Button cancelButton;
	@Wire
	Button resetButton;
	@Wire
	Button deleteButton;
	@Wire
	Textbox nameTextbox;
	@Wire
	Textbox descriptionTextbox;
	@Wire
	Textbox detailsTextbox;
	@Wire
	Textbox brandTextbox;

	// services
	@WireVariable
	private ToolTypeRepository toolTypeRepository;

	// atributes
	private ToolType currentToolType;
	private SortingPagingHelper<ToolType> sortingPagingHelper;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		Map<Integer, String> sortProperties = new HashMap<>();
		sortProperties.put(0, "name");
		sortProperties.put(3, "brand");
		sortingPagingHelper = new SortingPagingHelper<>(toolTypeRepository, toolTypeListbox, searchButton, searchTextbox, pager, sortProperties);
		currentToolType = null;
		refreshView();
	}

	@Listen("onClick = #searchButton")
	public void search() {
	}

	@Listen("onClick = #newButton")
	public void newButtonClick() {
		currentToolType = null;
		refreshView();
		toolTypeGrid.setVisible(true);
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(Strings.isBlank(nameTextbox.getText())){
			Clients.showNotification("Debe ingresar un nombre", nameTextbox);
			return;
		}
		String name = nameTextbox.getText().toUpperCase();
		String description = descriptionTextbox.getText();
		String details = detailsTextbox.getText();
		String brand = brandTextbox.getText();
		if(currentToolType == null) {// nuevo
			currentToolType = new ToolType(name, description, details, brand);
		} else {// actualizacion
			currentToolType.setName(name);
			currentToolType.setDescription(description);
			currentToolType.setDetails(details);
			currentToolType.setBrand(brand);
		}
		currentToolType = toolTypeRepository.save(currentToolType);
		sortingPagingHelper.reset();
		currentToolType = null;
		refreshView();
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		currentToolType = null;
		refreshView();
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
	}

	@Listen("onClick = #deleteButton")
	public void deleteButtonClick() {
                Messagebox.show("Esta seguro que quiere eliminar este tipo?", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
				public void onEvent(Event evt) throws InterruptedException {
					if (evt.getName().equals("onOK")) {
						toolTypeRepository.delete(currentToolType);
                                                sortingPagingHelper.reset();
                                                currentToolType = null;
                                                refreshView();
                                                
						alert("Tipo eliminado.");
					}
				}
			});
		
	}

	@Listen("onSelect = #toolTypeListbox")
	public void doListBoxSelect() {
		if(toolTypeListbox.getSelectedItem() == null) {
			//just in case for the no selection
			currentToolType = null;
		} else {
			if(currentToolType == null) {// si no hay nada editandose
				currentToolType = toolTypeListbox.getSelectedItem().getValue();
				refreshView();
			}
		}
		toolTypeListbox.clearSelection();
	}

	private void refreshView() {
		toolTypeListbox.clearSelection();
		sortingPagingHelper.reset();;// se actualiza la lista
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		newButton.setDisabled(false);
		if(currentToolType == null) {// creando
			toolTypeGrid.setVisible(false);
			nameTextbox.setValue(null);
			descriptionTextbox.setValue(null);
			detailsTextbox.setValue(null);
			brandTextbox.setValue(null);
			deleteButton.setDisabled(true);
			resetButton.setDisabled(true);// al crear, el boton new cumple la misma funcion q el reset
		} else {// editando
			toolTypeGrid.setVisible(true);
			nameTextbox.setValue(currentToolType.getName());
			descriptionTextbox.setValue(currentToolType.getDescription());
			detailsTextbox.setValue(currentToolType.getDetails());
			brandTextbox.setValue(currentToolType.getBrand());
			deleteButton.setDisabled(false);
			resetButton.setDisabled(false);
		}
	}
}
