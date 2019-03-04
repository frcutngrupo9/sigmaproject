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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.zkoss.lang.Strings;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.InputEvent;
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
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.MachineType;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.service.MachineTypeRepository;
import ar.edu.utn.sigmaproject.service.ProcessRepository;
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
	@Wire
	Button saveSequencesButton;

	// services
	@WireVariable
	private ProcessTypeRepository processTypeRepository;
	@WireVariable
	private ProcessRepository processRepository;
	@WireVariable
	private MachineTypeRepository machineTypeRepository;

	// attributes
	private ProcessType currentProcessType;
	private MachineType selectedMachineType;

	// list
	private List<MachineType> machineTypeList;
	private List<ProcessType> processTypeList;

	// list models
	private ListModelList<MachineType> machineTypeListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		machineTypeList = machineTypeRepository.findAll();
		machineTypeListModel = new ListModelList<>(machineTypeList);
		machineTypeListbox.setModel(machineTypeListModel);
		processTypeList = processTypeRepository.findAll();
		processTypeListbox.setModel(new ListModelList<>(processTypeList));
		currentProcessType = null;
		selectedMachineType = null;
		saveSequencesButton.setDisabled(true);
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
		String name = nameTextbox.getText();
		String details = detailsTextbox.getText();
		MachineType machineType = selectedMachineType;
		if(currentProcessType == null) {
			currentProcessType = new ProcessType(sequence, name, details, machineType);
		} else {
			// en lugar de modificar el objeto de la tabla, se lo resetea, esto es pq quizas fue modificado su secuencia en la tabla
			currentProcessType = processTypeRepository.findOne(currentProcessType.getId());
			currentProcessType.setName(name);
			currentProcessType.setDetails(details);
			//currentProcessType.setSequence(sequence);
			currentProcessType.setMachineType(machineType);
		}
		processTypeRepository.save(currentProcessType);
		currentProcessType = null;
		processTypeList = processTypeRepository.findAll();
		refreshView();
	}

	private void resetSequences() {
		for(int i = 0; i < processTypeList.size(); i++) {
			ProcessType each = processTypeList.get(i);
			each.setSequence(i + 1);
		}
		processTypeList = processTypeRepository.save(processTypeList);
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
		// verificamos que el proceso no este utilizado
		if(processRepository.findByType(currentProcessType).isEmpty() == false) {
			Messagebox.show("No se puede eliminar, el proceso se encuentra asignado a 1 o mas piezas.", "Informacion", Messagebox.OK, Messagebox.ERROR);
			return;
		}
		processTypeRepository.delete(currentProcessType);
		processTypeList = processTypeRepository.findAll();
		resetSequences();
		currentProcessType = null;
		refreshView();
	}

	@Listen("onSelect = #processTypeListbox")
	public void doListBoxSelect() {
		if(processTypeListbox.getSelectedItem() == null) {
			currentProcessType = null;
		} else {
			if(currentProcessType == null) {// si no hay nada editandose
				// en caso de que se hayan editado las secuencias se resetea la lista
				if(saveSequencesButton.isDisabled() == false) {
					processTypeList = processTypeRepository.findAll();
					saveSequencesButton.setDisabled(true);
				}
				currentProcessType = processTypeListbox.getSelectedItem().getValue();
				refreshView();
			}
		}
		processTypeListbox.clearSelection();
	}

	private void refreshView() {
		processTypeListbox.clearSelection();
		processTypeList = sortProcessTypeListBySequence(processTypeList);
		processTypeListbox.setModel(new ListModelList<>(processTypeList));
		//sortingPagingHelper.reset();// se actualiza la lista
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
			sequenceIntbox.setValue(processTypeRepository.findOne(currentProcessType.getId()).getSequence());// se busca la secuencia de la bd
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
	
	@Listen("onProcessTypeSequenceChange = #processTypeListbox")
	public void doProcessTypeSequenceChange(ForwardEvent evt) {
		saveSequencesButton.setDisabled(false);
		ProcessType data = (ProcessType) evt.getData();// obtenemos el objeto pasado por parametro
		Spinner origin = (Spinner) evt.getOrigin().getTarget();
		InputEvent inputEvent = (InputEvent) evt.getOrigin();
		int oldValue = data.getSequence();
		if(inputEvent.getValue().compareTo("") != 0) {
			int spinnerValue = Integer.valueOf(inputEvent.getValue());
			if(oldValue == getLastSequence()) {
				if(spinnerValue > oldValue) {
					// si es el ultimo valor y se lo quiere aumentar, se lo impide
					origin.setValue(oldValue);
					processTypeListbox.setModel(new ListModelList<>(processTypeList));
					return;
				}
			}
			origin.setValue(spinnerValue);
			// cambiamos el proceso que tiene la secuencia seleccionada y le asignamos esa secuencia al actual
			ProcessType processTypeToChange = findProcessType(spinnerValue);
			if(spinnerValue > oldValue) {// si se aumeta el valor
				processTypeToChange.setSequence(processTypeToChange.getSequence() - 1);
			} else {// si disminuye
				processTypeToChange.setSequence(processTypeToChange.getSequence() + 1);
			}
			data.setSequence(spinnerValue);
			processTypeList = sortProcessTypeListBySequence(processTypeList);
			processTypeListbox.setModel(new ListModelList<>(processTypeList));
			// si se estaba editando algo se cancela
			if(processTypeGrid.isVisible() == true) {
				currentProcessType = null;
				processTypeGrid.setVisible(false);
			}
		}
	}
	
	private ProcessType findProcessType(int sequence) {
		// devuelve el ProcessType que contenga el nro de secuencia
		for(ProcessType each : processTypeList) {
			if(each.getSequence() == sequence) {
				return each;
			}
		}
		return null;
	}
	
	public List<ProcessType> sortProcessTypeListBySequence(List<ProcessType> list) {
		Comparator<ProcessType> comp = new Comparator<ProcessType>() {
			@Override
			public int compare(ProcessType a, ProcessType b) {
				return a.getSequence().compareTo(b.getSequence());
			}
		};
		Collections.sort(list, comp);
		return list;
	}
	
	@Listen("onClick = #saveSequencesButton")
	public void saveSequencesButtonClick() {
		// guarda los cambios a las secuencias
		processTypeRepository.save(processTypeList);
		saveSequencesButton.setDisabled(true);
	}
}
