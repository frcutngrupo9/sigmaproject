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
		String name = nameTextbox.getText();
		MachineType machineType = null;
		if(machineTypeCombobox.getSelectedItem() != null) {
			machineType = machineTypeCombobox.getSelectedItem().getValue();
		}
		if(currentProcessType == null) {
			// nuevo
			currentProcessType = new ProcessType(name, machineType);
		} else {
			// edicion
			currentProcessType.setName(name);
			currentProcessType.setMachineType(machineType);
		}
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
			machineTypeCombobox.setSelectedIndex(-1);
			deleteButton.setDisabled(true);
			resetButton.setDisabled(true);// al crear, el boton new cumple la misma funcion q el reset
		} else {// editando
			processTypeGrid.setVisible(true);
			nameTextbox.setValue(currentProcessType.getName());
			machineTypeCombobox.setSelectedIndex(machineTypeListModel.indexOf(currentProcessType.getMachineType()));
			deleteButton.setDisabled(false);
			resetButton.setDisabled(false);
		}
	}
}
