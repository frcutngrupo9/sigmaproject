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
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
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
	Bandbox machineTypeBandbox;
	@Wire
	Listbox machineTypeListbox;
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
	private MachineType selectedMachineType;
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
		machineTypeListbox.setModel(machineTypeListModel);
		Map<Integer, String> sortProperties = new HashMap<>();
		sortProperties.put(0, "sequence");
		sortProperties.put(1, "name");
		sortProperties.put(2, "machineType");
		sortingPagingHelper = new SortingPagingHelper<>(processTypeRepository, processTypeListbox, searchButton, searchTextbox, pager, sortProperties);
		currentProcessType = null;
		selectedMachineType = null;
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
		Integer sequence = sequenceIntbox.getValue();
		boolean isOverlapped = false;
		if(sequence == null || sequence <= 0) {
			Clients.showNotification("Debe ingresar un nro de secuencia mayor a cero", sequenceIntbox);
			return;
		}
		String name = nameTextbox.getText();
		String details = detailsTextbox.getText();
		MachineType machineType = selectedMachineType;
		List<ProcessType> processTypelist = processTypeRepository.findAll();
		if(currentProcessType == null) {
			// nuevo
			if(sequence > getLastSequence() + 1) {
				sequence = getLastSequence() + 1;// no deja que se la secuencia sea mayor q el ultimo mas 1
			}
			currentProcessType = new ProcessType(sequence, name, details, machineType);
			// si el nro de secuencia es igual a otro, se lo sustituye y se corren ese y las otras secuencias a continuacion
			if(sequence>0 && sequence<=getLastSequence()) {
				Clients.showNotification("El nro de secuencia es igual al de otro proceso, se sustituira el mismo y ordenaran las restantes");
				isOverlapped = true;
			}
		} else {
			// edicion
			if(sequence > getLastSequence()) {// como se esta modificando la ultima secuencia es el maximo
				sequence = getLastSequence();// no deja que se la secuencia sea mayor q el ultimo mas 1
			}
			if(sequence != currentProcessType.getSequence()) {// si el nro de secuencia se modifico
				Clients.showNotification("El nro de secuencia es igual al de otro proceso, se sustituira el mismo y ordenaran las restantes");
				isOverlapped = true;
			}
			currentProcessType.setName(name);
			currentProcessType.setSequence(sequence);
			currentProcessType.setMachineType(machineType);
		}
		if(isOverlapped) {
			refreshSequences(currentProcessType, processTypelist);
		} else {
			processTypeRepository.save(currentProcessType);
		}
		sortingPagingHelper.reset();
		currentProcessType = null;
		refreshView();
	}

	private void refreshSequences(ProcessType processToMove, List<ProcessType> processTypelist) {
		if(processToMove.getId() != null) {// si es una edicion
			processTypelist.remove(getProcessIndex(processToMove, processTypelist));
		}
		int newIndex = processToMove.getSequence() - 1;
		processTypelist.add(newIndex, processToMove);
		updateAllSequences(processTypelist);
	}

	private void updateAllSequences(List<ProcessType> processTypelist) {
		for(int i = 0; i < processTypelist.size(); i++) {
			ProcessType each = processTypelist.get(i);
			each.setSequence(i + 1);
			processTypeRepository.save(each);
		}
	}

	private int getProcessIndex(ProcessType processType, List<ProcessType> processTypelist) {
		for(int i = 0; i < processTypelist.size(); i++) {
			ProcessType each = processTypelist.get(i);
			if(each.getId() == processType.getId()) {
				return i;
			}
		}
		return -1;
	}

	private void arrangeSequence(int oldSequence, int newSequence, List<ProcessType> processTypelist) {
		if(oldSequence > newSequence) {
			for(int i = newSequence-1; i < oldSequence; i++) {
				ProcessType each = processTypelist.get(i);
				each.setSequence(each.getSequence() + 1);
				processTypeRepository.save(each);
			}
		} else if (oldSequence == newSequence) {
			return;
		} else if (oldSequence < newSequence) {
			for(int i = oldSequence; i < newSequence-1; i++) {
				ProcessType each = processTypelist.get(i);
				each.setSequence(each.getSequence() - 1);
				processTypeRepository.save(each);
			}
		}
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
		updateAllSequences(processTypeRepository.findAll());
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
		sortingPagingHelper.reset();// se actualiza la lista
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		newButton.setDisabled(false);
		if(currentProcessType == null) {// creando
			processTypeGrid.setVisible(false);
			nameTextbox.setValue(null);
			detailsTextbox.setValue(null);
			// se selecciona el ultimo nro de secuencia mas 1
			sequenceIntbox.setValue(getLastSequence() + 1);
			selectedMachineType = null;
			machineTypeBandbox.setValue(getMachineTypeName(selectedMachineType));
			deleteButton.setDisabled(true);
			resetButton.setDisabled(true);// al crear, el boton new cumple la misma funcion q el reset
		} else {// editando
			processTypeGrid.setVisible(true);
			nameTextbox.setValue(currentProcessType.getName());
			sequenceIntbox.setValue(currentProcessType.getSequence());
			selectedMachineType = currentProcessType.getMachineType();
			machineTypeBandbox.setValue(getMachineTypeName(selectedMachineType));
			deleteButton.setDisabled(false);
			resetButton.setDisabled(false);
		}
	}

	private String getMachineTypeName(MachineType machineType) {
		if(machineType != null) {
			return machineType.getName();
		}
		return Labels.getLabel("none");
	}

	private int getLastSequence() {
		return processTypeRepository.findAll().size();
	}

	@Listen("onClick = #noneButton")
	public void noneButtonClick() {
		selectedMachineType = null;
		machineTypeBandbox.close();
		machineTypeBandbox.setValue(getMachineTypeName(selectedMachineType));
	}

	@Listen("onSelect = #machineTypeListbox")
	public void machineTypeListboxSelect() {
		if(machineTypeListbox.getSelectedItem() == null) {
			//just in case for the no selection
			selectedMachineType = null;
		} else {
			selectedMachineType = machineTypeListbox.getSelectedItem().getValue();
			machineTypeBandbox.setValue(getMachineTypeName(selectedMachineType));
		}
		machineTypeListbox.clearSelection();
	}
}
