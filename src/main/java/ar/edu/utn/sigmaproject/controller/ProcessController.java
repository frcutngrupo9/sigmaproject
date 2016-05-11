package ar.edu.utn.sigmaproject.controller;

import ar.edu.utn.sigmaproject.domain.MachineType;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.service.MachineTypeRepository;
import ar.edu.utn.sigmaproject.service.ProcessTypeRepository;
import ar.edu.utn.sigmaproject.util.SortingPagingHelper;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;
import org.zkoss.zul.ext.Selectable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProcessController extends SelectorComposer<Component>{
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
	SortingPagingHelper<ProcessType> sortingPagingHelper;

	// list
	private List<MachineType> machineTypeList;

	// list models
	private ListModelList<MachineType> machineTypeListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		machineTypeList = machineTypeRepository.findAll();
		machineTypeListModel = new ListModelList<>(machineTypeList);
		machineTypeCombobox.setModel(machineTypeListModel);
		Map<String, Boolean> sortProperties = new LinkedHashMap<String, Boolean>();
		sortProperties.put("id", Boolean.TRUE);
		sortProperties.put("name", Boolean.TRUE);
		sortProperties.put("machineType", Boolean.TRUE);
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
		if (machineTypeCombobox.getSelectedItem() != null) {
			machineType = machineTypeCombobox.getSelectedItem().getValue();
		}
		if (currentProcessType == null) {
			// es un nuevo insumo
			currentProcessType = new ProcessType(name, machineType);
		} else {
			// es una edicion
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
		Selectable model = (Selectable) processTypeListbox.getModel();
		if (model.isSelectionEmpty()) {
			//just in case for the no selection
			currentProcessType = null;
		} else {
			if(currentProcessType == null) {// si no hay nada editandose
				currentProcessType = processTypeListbox.getSelectedItem().getValue();
				refreshView();
			}
		}
		model.clearSelection();
	}

	private void refreshView() {
		Selectable model = (Selectable) processTypeListbox.getModel();
		if (model != null) {
			model.clearSelection();
		}
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
		}else {// editando
			processTypeGrid.setVisible(true);
			nameTextbox.setValue(currentProcessType.getName());
			machineTypeCombobox.setSelectedIndex(machineTypeListModel.indexOf(currentProcessType.getMachineType()));
			deleteButton.setDisabled(false);
			resetButton.setDisabled(false);
		}
	}
}
