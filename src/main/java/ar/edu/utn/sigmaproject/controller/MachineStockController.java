package ar.edu.utn.sigmaproject.controller;

import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Selectbox;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Machine;
import ar.edu.utn.sigmaproject.domain.MachineType;
import ar.edu.utn.sigmaproject.service.MachineService;
import ar.edu.utn.sigmaproject.service.MachineTypeService;
import ar.edu.utn.sigmaproject.service.impl.MachineServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.MachineTypeServiceImpl;

public class MachineStockController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Listbox machineListbox;
	@Wire
	Grid machineGrid;
	@Wire
	Selectbox machineTypeSelectbox;
	@Wire
	Textbox nameTextbox;
	@Wire
	Textbox codeTextbox;
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
	private MachineService machineService = new MachineServiceImpl();
	private MachineTypeService machineTypeService = new MachineTypeServiceImpl();

	// attributes
	private Machine currentMachine;

	// list
	private List<Machine> machineList;
	private List<MachineType> machineTypeList;

	// list models
	private ListModelList<Machine> machineListModel;
	private ListModelList<MachineType> machineTypeListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		machineTypeList = machineTypeService.getMachineTypeList();
		machineTypeListModel = new ListModelList<MachineType>(machineTypeList);
		machineTypeSelectbox.setModel(machineTypeListModel);
		machineList = machineService.getMachineList();
		machineListModel = new ListModelList<Machine>(machineList);
		machineListbox.setModel(machineListModel);
		currentMachine = null;
		refreshView();
	}

	@Listen("onClick = #searchButton")
	public void search() {
	}

	@Listen("onSelect = #machineListbox")
	public void doWoodListBoxSelect() {
		if(machineListModel.isSelectionEmpty()) {
			//just in case for the no selection
			currentMachine = null;
		} else {
			if(currentMachine == null) {// si no hay nada editandose
				currentMachine = machineListbox.getSelectedItem().getValue();
				refreshView();
			}
		}
		machineListModel.clearSelection();
	}

	private void refreshView() {
		machineListModel.clearSelection();
		machineListbox.setModel(machineListModel);// se actualiza la lista limpiar la seleccion
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		newButton.setDisabled(false);
		if(currentMachine == null) {// nuevo
			machineGrid.setVisible(false);
			machineTypeSelectbox.setSelectedIndex(-1);
			codeTextbox.setValue(null);
			nameTextbox.setValue(null);
			yearIntbox.setValue(null);
			usedTimeIntboxHours.setValue(null);
			usedTimeIntboxMinutes.setValue(null);
			deleteButton.setDisabled(true);
			resetButton.setDisabled(true);
		}else {// editar
			machineGrid.setVisible(true);
			machineTypeSelectbox.setSelectedIndex(machineTypeListModel.indexOf(machineTypeService.getMachineType(currentMachine.getIdMachineType())));
			codeTextbox.setValue(currentMachine.getCode());
			nameTextbox.setValue(currentMachine.getName());
			yearIntbox.setValue(currentMachine.getYear());
			usedTimeIntboxHours.setValue(currentMachine.getUsedTime().getHours());
			usedTimeIntboxMinutes.setValue(currentMachine.getUsedTime().getMinutes());
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
		if(machineTypeSelectbox.getSelectedIndex() == -1) {
			Clients.showNotification("Debe seleccionar una Maquina", machineTypeSelectbox);
			return;
		}
		if(Strings.isBlank(nameTextbox.getValue())){
			Clients.showNotification("Ingrese el Nombre de la Maquina", nameTextbox);
			return;
		}
		if(Strings.isBlank(codeTextbox.getValue())){
			Clients.showNotification("Ingrese el Codigo de la Maquina", codeTextbox);
			return;
		}
		Integer idMachineType = machineTypeListModel.getElementAt(machineTypeSelectbox.getSelectedIndex()).getId();
		String code = codeTextbox.getText();
		String name = nameTextbox.getText();
		Integer year = yearIntbox.getValue();
		Integer usedTimeHours = usedTimeIntboxHours.getValue();
		Integer usedTimeMinutes = usedTimeIntboxMinutes.getValue();
		Duration usedTime = null;
		try {
			usedTime = DatatypeFactory.newInstance().newDuration(true, 0, 0, 0, usedTimeHours, usedTimeMinutes, 0);
		} catch (DatatypeConfigurationException e) {
			System.out.println("Error en convertir a duracion: " + e.toString());
		}
		if(currentMachine == null) {// nuevo
			currentMachine = new Machine(null, idMachineType, code, name, year, usedTime);
			currentMachine = machineService.saveMachine(currentMachine);
		} else {// edicion
			currentMachine.setIdMachineType(idMachineType);
			currentMachine.setCode(code);
			currentMachine.setName(name);
			currentMachine.setYear(year);
			currentMachine.setUsedTime(usedTime);
			currentMachine = machineService.updateMachine(currentMachine);
		}
		machineList = machineService.getMachineList();
		machineListModel = new ListModelList<Machine>(machineList);
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

	public String getDurationFormated(Machine machine) {
		Duration duration = machine.getUsedTime();
		String hours = duration.getHours() + " (Horas)";
		String Minutes = duration.getMinutes() + " (Minutos)";
		return hours + ":" + Minutes;
	}
	
	public MachineType getMachineType(Machine machine) {
		return machineTypeService.getMachineType(machine.getIdMachineType());
	}
}
