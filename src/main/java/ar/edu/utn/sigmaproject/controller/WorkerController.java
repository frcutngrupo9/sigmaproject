package ar.edu.utn.sigmaproject.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.edu.utn.sigmaproject.service.MachineRepository;
import ar.edu.utn.sigmaproject.service.WorkerRepository;
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

import ar.edu.utn.sigmaproject.domain.Worker;
import ar.edu.utn.sigmaproject.service.WorkerRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class WorkerController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Button searchButton;
	@Wire
	Listbox workerListbox;
	@Wire
	Paging pager;
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
	private WorkerRepository workerRepository;

	// attributes
	private Worker currentWorker;
	SortingPagingHelper<Worker> sortingPagingHelper;

	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		Map<Integer, String> sortProperties = new HashMap<>();
		sortProperties.put(0, "name");
		sortProperties.put(1, "dateEmployed");
		sortingPagingHelper = new SortingPagingHelper<>(workerRepository, workerListbox, searchButton, searchTextbox, pager, sortProperties);
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
		sortingPagingHelper.reset();
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
		sortingPagingHelper.reset();
		currentWorker = null;
		refreshView();
	}

	@Listen("onSelect = #workerListbox")
	public void doListBoxSelect() {
		if(workerListbox.getSelectedItem() == null) {
			//just in case for the no selection
			currentWorker = null;
		} else {
			if(currentWorker == null) {// si no hay nada editandose
				currentWorker = workerListbox.getSelectedItem().getValue();
				refreshView();
			}
		}
		workerListbox.clearSelection();
	}

	private void refreshView() {
		workerListbox.clearSelection();
		sortingPagingHelper.reset();// se actualiza la lista
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
