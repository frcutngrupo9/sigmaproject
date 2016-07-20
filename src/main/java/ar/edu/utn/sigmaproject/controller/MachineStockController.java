package ar.edu.utn.sigmaproject.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

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

import ar.edu.utn.sigmaproject.domain.Machine;
import ar.edu.utn.sigmaproject.domain.MachineType;
import ar.edu.utn.sigmaproject.service.MachineRepository;
import ar.edu.utn.sigmaproject.service.MachineTypeRepository;
import ar.edu.utn.sigmaproject.util.SortingPagingHelper;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class MachineStockController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Button searchButton;
	@Wire
	Listbox machineListbox;
	@Wire
	Paging pager;
	@Wire
	Grid machineGrid;
	@Wire
	Combobox machineTypeCombobox;
	@Wire
	Textbox nameTextbox;
	@Wire
	Intbox yearIntbox;
	@Wire
	Intbox usedTimeIntboxHours;
	@Wire
	Intbox usedTimeIntboxMinutes;
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

	// services
	@WireVariable
	private MachineTypeRepository machineTypeRepository;
	@WireVariable
	MachineRepository machineRepository;

	// attributes
	private Machine currentMachine;
	SortingPagingHelper<Machine> sortingPagingHelper;

	// list
	private List<Machine> machineList;
	private List<MachineType> machineTypeList;

	// list models
	private ListModelList<Machine> machineListModel;
	private ListModelList<MachineType> machineTypeListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		Map<Integer, String> sortProperties = new HashMap<>();
		sortProperties.put(0, "id");
		sortProperties.put(1, "name");
		sortProperties.put(2, "code");
		sortProperties.put(3, "machineType");
		sortProperties.put(4, "year");
		sortingPagingHelper = new SortingPagingHelper<>(machineRepository, machineListbox, searchButton, searchTextbox, pager, sortProperties);
		machineTypeList = machineTypeRepository.findAll();
		machineTypeListModel = new ListModelList<>(machineTypeList);
		machineTypeCombobox.setModel(machineTypeListModel);
		machineList = machineRepository.findAll();
		machineListModel = new ListModelList<>(machineList);
		machineListbox.setModel(machineListModel);
		currentMachine = null;
		refreshView();
	}

	@Listen("onClick = #searchButton")
	public void search() {
	}

	@Listen("onSelect = #machineListbox")
	public void doWoodListBoxSelect() {
		if(machineListbox.getSelectedItem() == null) {
			//just in case for the no selection
			currentMachine = null;
		} else {
			if(currentMachine == null) {// si no hay nada editandose
				currentMachine = machineListbox.getSelectedItem().getValue();
				refreshView();
			}
		}
		machineListbox.clearSelection();
	}

	private void refreshView() {
		machineListbox.clearSelection();
		sortingPagingHelper.reset();// se actualiza la lista limpiar la seleccion
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		newButton.setDisabled(false);
		if(currentMachine == null) {// nuevo
			machineGrid.setVisible(false);
			machineTypeCombobox.setSelectedIndex(-1);
			nameTextbox.setValue(null);
			yearIntbox.setValue(null);
			usedTimeIntboxHours.setValue(null);
			usedTimeIntboxMinutes.setValue(null);
			deleteButton.setDisabled(true);
			resetButton.setDisabled(true);
		}else {// editar
			machineGrid.setVisible(true);
			machineTypeListModel.addToSelection(currentMachine.getMachineType());
			machineTypeCombobox.setModel(machineTypeListModel);
			nameTextbox.setValue(currentMachine.getName());
			yearIntbox.setValue(currentMachine.getYear());
			if(currentMachine.getUsedTime() != null) {
				usedTimeIntboxHours.setValue(currentMachine.getUsedTime().getHours());
				usedTimeIntboxMinutes.setValue(currentMachine.getUsedTime().getMinutes());
			} else {
				usedTimeIntboxHours.setValue(null);
				usedTimeIntboxMinutes.setValue(null);
			}
			deleteButton.setDisabled(false);
			resetButton.setDisabled(false);
		}
	}

	@Listen("onClick = #newButton")
	public void newButtonClick() {
		currentMachine = null;
		refreshView();
		machineGrid.setVisible(true);
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(machineTypeCombobox.getSelectedIndex() == -1) {
			Clients.showNotification("Debe seleccionar una Maquina", machineTypeCombobox);
			return;
		}
		if(Strings.isBlank(nameTextbox.getValue())){
			Clients.showNotification("Ingrese el Nombre de la Maquina", nameTextbox);
			return;
		}
		MachineType machineType = machineTypeListModel.getElementAt(machineTypeCombobox.getSelectedIndex());
		String name = nameTextbox.getText();
		Integer year = yearIntbox.getValue();
		Integer usedTimeHours = usedTimeIntboxHours.getValue();
		if(usedTimeHours == null) { usedTimeHours = 0; }
		Integer usedTimeMinutes = usedTimeIntboxMinutes.getValue();
		if(usedTimeMinutes == null) { usedTimeMinutes = 0; }
		Duration usedTime = null;
		try {
			usedTime = DatatypeFactory.newInstance().newDuration(true, 0, 0, 0, usedTimeHours, usedTimeMinutes, 0);
		} catch (DatatypeConfigurationException e) {
			System.out.println("Error en convertir a duracion: " + e.toString());
		}
		if(currentMachine == null) {// nuevo
			currentMachine = new Machine(machineType, name, year, usedTime);
		} else {// edicion
			currentMachine.setMachineType(machineType);
			currentMachine.setName(name);
			currentMachine.setYear(year);
			currentMachine.setUsedTime(usedTime);
		}
		currentMachine = machineRepository.save(currentMachine);
		sortingPagingHelper.reset();
		currentMachine = null;
		refreshView();
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		currentMachine = null;
		refreshView();
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
	}

	public String getDurationFormatted(Machine machine) {
		Duration duration = machine.getUsedTime();
		String hours = "0 (Horas)";
		String Minutes = "0 (Minutos)";
		if(duration != null) {
			hours = duration.getHours() + " (Horas)";
			Minutes = duration.getMinutes() + " (Minutos)";
		}
		return hours + ":" + Minutes;
	}
}
