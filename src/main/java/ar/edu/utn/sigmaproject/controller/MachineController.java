package ar.edu.utn.sigmaproject.controller;

import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import ar.edu.utn.sigmaproject.service.MachineTypeRepository;
import org.zkoss.lang.Strings;
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
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.MachineType;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class MachineController extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Listbox machineTypeListbox;
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
	Intbox deteriorationTimeIntboxYears;
	@Wire
	Intbox deteriorationTimeIntboxDays;
	@Wire
	Intbox deteriorationTimeIntboxHours;

	// services
	@WireVariable
	private MachineTypeRepository machineTypeRepository;

	// atributes
	private MachineType currentMachineType;

	// list
	private List<MachineType> machineTypeList;

	// list models
	private ListModelList<MachineType> machineTypeListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		machineTypeList = machineTypeRepository.findAll();
		machineTypeListModel = new ListModelList<MachineType>(machineTypeList);
		machineTypeListbox.setModel(machineTypeListModel);
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
		if(Strings.isBlank(nameTextbox.getText())){
			Clients.showNotification("Debe ingresar un nombre", nameTextbox);
			return;
		}
		String name = nameTextbox.getText();
		String details = detailsTextbox.getText();
		Integer years = deteriorationTimeIntboxYears.intValue();
		Integer days = deteriorationTimeIntboxDays.intValue();
		Integer hours = deteriorationTimeIntboxHours.intValue();
		Duration duration = null;
		try {
			duration = DatatypeFactory.newInstance().newDuration(true, years, 0, days, hours, 0, 0);
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
		machineTypeList = machineTypeRepository.findAll();
		machineTypeListModel = new ListModelList<>(machineTypeList);
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
		machineTypeListModel.remove(currentMachineType);
		currentMachineType = null;
		refreshView();
	}

	@Listen("onSelect = #machineTypeListbox")
	public void doListBoxSelect() {
		if(machineTypeListModel.isSelectionEmpty()) {
			//just in case for the no selection
			currentMachineType = null;
		} else {
			if(currentMachineType == null) {// si no hay nada editandose
				currentMachineType = machineTypeListbox.getSelectedItem().getValue();
				refreshView();
			}
		}
		machineTypeListModel.clearSelection();
	}

	private void refreshView() {
		machineTypeListModel.clearSelection();
		machineTypeListbox.setModel(machineTypeListModel);// se actualiza la lista
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		newButton.setDisabled(false);
		if(currentMachineType == null) {// creando
			machineTypeGrid.setVisible(false);
			nameTextbox.setValue(null);
			deteriorationTimeIntboxYears.setValue(null);
			deteriorationTimeIntboxDays.setValue(null);
			deteriorationTimeIntboxHours.setValue(null);
			detailsTextbox.setValue(null);
			deleteButton.setDisabled(true);
			resetButton.setDisabled(true);// al crear, el boton new cumple la misma funcion q el reset
		} else {// editando
			machineTypeGrid.setVisible(true);
			nameTextbox.setValue(currentMachineType.getName());
			deteriorationTimeIntboxYears.setValue(currentMachineType.getDeteriorationTime().getYears());
			deteriorationTimeIntboxDays.setValue(currentMachineType.getDeteriorationTime().getDays());
			deteriorationTimeIntboxHours.setValue(currentMachineType.getDeteriorationTime().getHours());
			detailsTextbox.setValue(currentMachineType.getDetails());
			deleteButton.setDisabled(false);
			resetButton.setDisabled(false);
		}
	}

	public String getFormattedTime(Duration time) {
		return String.format("A�os: %d Horas: %d Minutos: %d", time.getYears(), time.getHours(), time.getMinutes());
	}
}
