package ar.edu.utn.sigmaproject.controller;

import java.util.List;

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

import ar.edu.utn.sigmaproject.domain.MachineType;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.service.MachineTypeService;
import ar.edu.utn.sigmaproject.service.ProcessTypeService;
import ar.edu.utn.sigmaproject.service.impl.MachineTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProcessTypeServiceImpl;

public class ProcessController extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Listbox processTypeListbox;
	@Wire
	Button newButton;
	@Wire
	Grid processTypeGrid;
	@Wire
	Textbox nameTextbox;
	@Wire
	Intbox stepNumberIntbox;
	@Wire
	Selectbox machineTypeSelectbox;
	@Wire
	Button saveButton;
	@Wire
	Button cancelButton;
	@Wire
	Button resetButton;
	@Wire
	Button deleteButton;

	// services
	private ProcessTypeService processTypeService = new ProcessTypeServiceImpl();
	private MachineTypeService machineTypeService = new MachineTypeServiceImpl();

	// attributes
	private ProcessType currentProcessType;

	// list
	private List<ProcessType> processTypeList;
	private List<MachineType> machineTypeList;

	// list models
	private ListModelList<ProcessType> processTypeListModel;
	private ListModelList<MachineType> machineTypeListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		machineTypeList = machineTypeService.getMachineTypeList();
		machineTypeListModel = new ListModelList<MachineType>(machineTypeList);
		machineTypeSelectbox.setModel(machineTypeListModel);
		processTypeList = processTypeService.getProcessTypeList();
		processTypeListModel = new ListModelList<ProcessType>(processTypeList);
		processTypeListbox.setModel(processTypeListModel);
		currentProcessType = null;
		refreshView();
	}

	@Listen("onClick = #searchButton")
	public void search() {
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
		String name = nameTextbox.getText();
		Integer stepNumber = stepNumberIntbox.getValue();
		MachineType machineType = machineTypeListModel.getElementAt(machineTypeSelectbox.getSelectedIndex());
		Integer idMachineType = null;
		if(machineType != null) {
			idMachineType = machineType.getId();
		}
		if(currentProcessType == null) {
			// es un nuevo insumo
			currentProcessType = new ProcessType(null, idMachineType, name, stepNumber);
//			currentProcessType = processTypeService.saveProcessType(currentProcessType);
		} else {
			// es una edicion
			currentProcessType.setName(name);
			currentProcessType.setStepNumber(stepNumber);
			currentProcessType.setIdMachineType(idMachineType);
//			currentProcessType = processTypeService.updateProcessType(currentProcessType);
		}
		processTypeList = processTypeService.getProcessTypeList();
		processTypeListModel = new ListModelList<ProcessType>(processTypeList);
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
//		processTypeService.deleteProcessType(currentProcessType);
		processTypeListModel.remove(currentProcessType);
		currentProcessType = null;
		refreshView();
	}

	@Listen("onSelect = #processTypeListbox")
	public void doListBoxSelect() {
		if(processTypeListModel.isSelectionEmpty()) {
			//just in case for the no selection
			currentProcessType = null;
		} else {
			if(currentProcessType == null) {// si no hay nada editandose
				currentProcessType = processTypeListModel.getSelection().iterator().next();
				refreshView();
			}
		}
		processTypeListModel.clearSelection();
	}

	private void refreshView() {
		processTypeListModel.clearSelection();
		processTypeListbox.setModel(processTypeListModel);// se actualiza la lista
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		newButton.setDisabled(false);
		if(currentProcessType == null) {// creando
			processTypeGrid.setVisible(false);
			nameTextbox.setValue(null);
			stepNumberIntbox.setValue(null);
			machineTypeSelectbox.setSelectedIndex(-1);
			deleteButton.setDisabled(true);
			resetButton.setDisabled(true);// al crear, el boton new cumple la misma funcion q el reset
		}else {// editando
			processTypeGrid.setVisible(true);
			nameTextbox.setValue(currentProcessType.getName());
			stepNumberIntbox.setValue(currentProcessType.getStepNumber());
			machineTypeSelectbox.setSelectedIndex(machineTypeListModel.indexOf(machineTypeService.getMachineType(currentProcessType.getIdMachineType())));
			deleteButton.setDisabled(false);
			resetButton.setDisabled(false);
		}
	}
	
	public String getMachineTypeName(ProcessType processType) {
		String name = "ninguna";
		MachineType machineType = machineTypeService.getMachineType(processType.getIdMachineType());
		if(machineType != null) {
			name = machineType.getName();
		}
		return name;
	}
}
