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

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.zkoss.lang.Strings;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.MachineType;
import ar.edu.utn.sigmaproject.service.MachineTypeRepository;
import ar.edu.utn.sigmaproject.util.SortingPagingHelper;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class MachineController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Button searchButton;
	@Wire
	Listbox machineTypeListbox;
	@Wire
	Paging pager;
	@Wire
	Button newButton;
	@Wire
	Grid machineTypeGrid;
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
	Textbox detailsTextbox;
	@Wire
	Intbox deteriorationTimeIntboxHours;

	// services
	@WireVariable
	private MachineTypeRepository machineTypeRepository;

	// atributes
	private MachineType currentMachineType;
	SortingPagingHelper<MachineType> sortingPagingHelper;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		Map<Integer, String> sortProperties = new HashMap<>();
		sortProperties.put(0, "name");
		sortingPagingHelper = new SortingPagingHelper<>(machineTypeRepository, machineTypeListbox, searchButton, searchTextbox, pager, sortProperties);
		currentMachineType = null;
		refreshView();
	}

	@Listen("onClick = #searchButton")
	public void search() {
	}

	@Listen("onClick = #newButton")
	public void newButtonClick() {
		currentMachineType = null;
		refreshView();
		machineTypeGrid.setVisible(true);
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(Strings.isBlank(nameTextbox.getText())) {
			Clients.showNotification("Debe ingresar un nombre", nameTextbox);
			return;
		}
		String name = nameTextbox.getText();
		String details = detailsTextbox.getText();
		Integer hours = deteriorationTimeIntboxHours.intValue();
		Duration duration = null;
		try {
			duration = DatatypeFactory.newInstance().newDuration(true, 0, 0, 0, hours, 0, 0);
		} catch (DatatypeConfigurationException e) {
			System.out.println("Error en finalizar maquina, en convertir a duracion: " + e.toString());
		}
		if(currentMachineType == null) {// nuevo
			currentMachineType = new MachineType(name, details, duration);
		} else {// edicion
			currentMachineType.setName(name);
			currentMachineType.setDetails(details);
			currentMachineType.setDeteriorationTime(duration);
		}
		machineTypeRepository.save(currentMachineType);
		sortingPagingHelper.reset();
		currentMachineType = null;
		refreshView();
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		currentMachineType = null;
		refreshView();
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
	}

	@Listen("onClick = #deleteButton")
	public void deleteButtonClick() {
		machineTypeRepository.delete(currentMachineType);
		sortingPagingHelper.reset();
		currentMachineType = null;
		refreshView();
	}

	@Listen("onSelect = #machineTypeListbox")
	public void doListBoxSelect() {
		if(machineTypeListbox.getSelectedItem() == null) {
			//just in case for the no selection
			currentMachineType = null;
		} else {
			if(currentMachineType == null) {// si no hay nada editandose
				currentMachineType = machineTypeListbox.getSelectedItem().getValue();
				refreshView();
			}
		}
		machineTypeListbox.clearSelection();
	}

	private void refreshView() {
		machineTypeListbox.clearSelection();
		sortingPagingHelper.reset();// se actualiza la lista
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		newButton.setDisabled(false);
		if(currentMachineType == null) {// creando
			machineTypeGrid.setVisible(false);
			nameTextbox.setValue(null);
			detailsTextbox.setValue(null);
			deleteButton.setDisabled(true);
			resetButton.setDisabled(true);// al crear, el boton new cumple la misma funcion q el reset
		} else {// editando
			machineTypeGrid.setVisible(true);
			nameTextbox.setValue(currentMachineType.getName());
			if(currentMachineType.getDeteriorationTime() == null) {
				deteriorationTimeIntboxHours.setValue(null);
			} else {
				deteriorationTimeIntboxHours.setValue(currentMachineType.getDeteriorationTime().getHours());
			}
			detailsTextbox.setValue(currentMachineType.getDetails());
			deleteButton.setDisabled(false);
			resetButton.setDisabled(false);
		}
	}

	public String getFormattedTime(Duration time) {
		if(time != null) {
			return String.format(time.getHours() + " " + Labels.getLabel("hours"));
		} else {
			return "";
		}
	}
}