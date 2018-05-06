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
import java.util.List;
import java.util.Map;

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
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.MachineType;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.service.MachineTypeRepository;
import ar.edu.utn.sigmaproject.service.ProcessTypeRepository;
import ar.edu.utn.sigmaproject.util.SortingPagingHelper;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProcessController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Button searchButton;
	@Wire
	Listbox processTypeListbox;
	@Wire
	Paging pager;
	@Wire
	Button newButton;
	@Wire
	Grid processTypeGrid;
	@Wire
	Textbox nameTextbox;
	@Wire
	Textbox detailsTextbox;
	@Wire
	Intbox sequenceIntbox;
	@Wire
	Combobox machineTypeCombobox;
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
	private ProcessTypeRepository processTypeRepository;
	@WireVariable
	private MachineTypeRepository machineTypeRepository;

	// attributes
	private ProcessType currentProcessType;
	private SortingPagingHelper<ProcessType> sortingPagingHelper;

	// list
	private List<MachineType> machineTypeList;

	// list models
	private ListModelList<MachineType> machineTypeListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		machineTypeList = machineTypeRepository.findAll();
		machineTypeListModel = new ListModelList<>(machineTypeList);
		machineTypeCombobox.setModel(machineTypeListModel);
		Map<Integer, String> sortProperties = new HashMap<>();
		sortProperties.put(0, "id");
		sortProperties.put(1, "name");
		sortProperties.put(2, "machineType");
		sortingPagingHelper = new SortingPagingHelper<>(processTypeRepository, processTypeListbox, searchButton, searchTextbox, pager, sortProperties);
		currentProcessType = null;
		refreshView();
	}

	@Listen("onClick = #newButton")
	public void newButtonClick() {
		currentProcessType = null;
		refreshView();
		processTypeGrid.setVisible(true);
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(Strings.isBlank(nameTextbox.getText())){
			Clients.showNotification("Debe ingresar un nombre", nameTextbox);
			return;
		}
		if(sequenceIntbox.getValue() <= 0){
			Clients.showNotification("Debe ingresar un nro de secuencia mayor a cero", sequenceIntbox);
			return;
		}
		String name = nameTextbox.getText();
		String details = detailsTextbox.getText();
		int sequence = sequenceIntbox.getValue();
		MachineType machineType = null;
		if(machineTypeCombobox.getSelectedItem() != null) {
			machineType = machineTypeCombobox.getSelectedItem().getValue();
		}
		int prevSeq = -1;
		if(currentProcessType == null) {
			// nuevo
			prevSeq = sequence;
			currentProcessType = new ProcessType(sequence, name, details, machineType);
		} else {
			// edicion
			prevSeq = currentProcessType.getSequence();
			currentProcessType.setName(name);
			currentProcessType.setSequence(sequence);
			currentProcessType.setMachineType(machineType);
		}
		// si la secuencia se superpone a otro proceso, se mueven las secuencias
//		List<ProcessType> processTypelist = processTypeRepository.findAll();
//		if(sequence <= getLastSequence() && prevSeq != sequence) {
//			boolean movedForward = prevSeq < sequence;
//			for(ProcessType each : processTypelist) {
//				if(movedForward) {// se movio hacia adelante
//					// a todos los procesos entre las 2 secuencias se les resta 1
//					if(each.getSequence() > prevSeq && each.getSequence() <= sequence) {
//						each.setSequence(each.getSequence() - 1);
//					}
//				} else {
//					if(each.getSequence() >= sequence && each.getSequence() < prevSeq) {
//						each.setSequence(each.getSequence() + 1);
//					}
//				}
//			}
//		} else {
//			
//		}

		currentProcessType = processTypeRepository.save(currentProcessType);
		sortingPagingHelper.reset();
		currentProcessType = null;
		refreshView();
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		currentProcessType = null;
		refreshView();
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
	}

	@Listen("onClick = #deleteButton")
	public void deleteButtonClick() {
		processTypeRepository.delete(currentProcessType);
		sortingPagingHelper.reset();
		currentProcessType = null;
		refreshView();
	}

	@Listen("onSelect = #processTypeListbox")
	public void doListBoxSelect() {
		if(processTypeListbox.getSelectedItem() == null) {
			//just in case for the no selection
			currentProcessType = null;
		} else {
			if(currentProcessType == null) {// si no hay nada editandose
				currentProcessType = processTypeListbox.getSelectedItem().getValue();
				refreshView();
			}
		}
		processTypeListbox.clearSelection();
	}

	private void refreshView() {
		processTypeListbox.clearSelection();
		sortingPagingHelper.reset();;// se actualiza la lista
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		newButton.setDisabled(false);
		if(currentProcessType == null) {// creando
			processTypeGrid.setVisible(false);
			nameTextbox.setValue(null);
			detailsTextbox.setValue(null);
			// se selecciona el ultimo nro de secuencia mas 1
			sequenceIntbox.setValue(getLastSequence() + 1);
			machineTypeCombobox.setSelectedIndex(-1);
			deleteButton.setDisabled(true);
			resetButton.setDisabled(true);// al crear, el boton new cumple la misma funcion q el reset
		} else {// editando
			processTypeGrid.setVisible(true);
			nameTextbox.setValue(currentProcessType.getName());
			sequenceIntbox.setValue(currentProcessType.getSequence());
			machineTypeCombobox.setSelectedIndex(machineTypeListModel.indexOf(currentProcessType.getMachineType()));
			deleteButton.setDisabled(false);
			resetButton.setDisabled(false);
		}
	}

	private int getLastSequence() {
		int lastSequence = 0;
		for(ProcessType each : processTypeRepository.findAll()) {
			if(each.getSequence() >= lastSequence) {
				lastSequence = each.getSequence();
			}
		}
		return lastSequence;
	}
}
