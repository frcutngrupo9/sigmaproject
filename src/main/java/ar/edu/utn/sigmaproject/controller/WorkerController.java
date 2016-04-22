package ar.edu.utn.sigmaproject.controller;

import java.util.Date;
import java.util.List;

import ar.edu.utn.sigmaproject.service.MachineRepository;
import ar.edu.utn.sigmaproject.service.WorkerRepository;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Worker;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class WorkerController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Listbox workerListbox;
	@Wire
	Button newButton;
	@Wire
	Grid workerGrid;
	@Wire
	Textbox nameTextbox;
	@Wire
	Datebox dateEmployedDatebox;
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
	WorkerRepository workerRepository;

	// attributes
	private Worker currentWorker;

	// list
	private List<Worker> workerList;

	// list models
	private ListModelList<Worker> workerListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		workerList = workerRepository.findAll();
		workerListModel = new ListModelList<>(workerList);
		workerListbox.setModel(workerListModel);
		currentWorker = null;

		refreshView();
	}

	@Listen("onClick = #searchButton")
	public void search() {
	}

	@Listen("onClick = #newButton")
	public void newButtonClick() {
		currentWorker = null;
		refreshView();
		workerGrid.setVisible(true);
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(Strings.isBlank(nameTextbox.getText())){
			Clients.showNotification("Debe ingresar un nombre", nameTextbox);
			return;
		}
		String name = nameTextbox.getText();
		Date dateEmployed = dateEmployedDatebox.getValue();
		if(currentWorker == null) {
			// es un nuevo insumo
			currentWorker = new Worker(name, dateEmployed);
		} else {
			// es una edicion
			currentWorker.setName(name);
			currentWorker.setDateEmployed(dateEmployed);
		}
		currentWorker = workerRepository.save(currentWorker);
		workerList = workerRepository.findAll();
		workerListModel = new ListModelList<Worker>(workerList);
		currentWorker = null;
		refreshView();
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		currentWorker = null;
		refreshView();
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
	}

	@Listen("onClick = #deleteButton")
	public void deleteButtonClick() {
		workerRepository.delete(currentWorker);
		workerListModel.remove(currentWorker);
		currentWorker = null;
		refreshView();
	}

	@Listen("onSelect = #workerListbox")
	public void doListBoxSelect() {
		if(workerListModel.isSelectionEmpty()) {
			//just in case for the no selection
			currentWorker = null;
		} else {
			if(currentWorker == null) {// si no hay nada editandose
				currentWorker = workerListbox.getSelectedItem().getValue();
				refreshView();
			}
		}
		workerListModel.clearSelection();
	}

	private void refreshView() {
		workerListModel.clearSelection();
		workerListbox.setModel(workerListModel);// se actualiza la lista
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		newButton.setDisabled(false);
		if(currentWorker == null) {// creando
			workerGrid.setVisible(false);
			nameTextbox.setValue(null);
			dateEmployedDatebox.setValue(null);
			deleteButton.setDisabled(true);
			resetButton.setDisabled(true);// al crear, el boton new cumple la misma funcion q el reset
		}else {// editando
			workerGrid.setVisible(true);
			nameTextbox.setValue(currentWorker.getName());
			dateEmployedDatebox.setValue(currentWorker.getDateEmployed());
			deleteButton.setDisabled(false);
			resetButton.setDisabled(false);
		}
	}
}
