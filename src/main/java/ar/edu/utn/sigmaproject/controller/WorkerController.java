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

import java.util.Date;
import java.util.HashMap;
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
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Messagebox;

import ar.edu.utn.sigmaproject.domain.Worker;
import ar.edu.utn.sigmaproject.service.WorkerRepository;
import ar.edu.utn.sigmaproject.util.SortingPagingHelper;

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
	public void doAfterCompose(Component comp) throws Exception {
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
		String name = nameTextbox.getText().toUpperCase();
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
                Messagebox.show("Esta seguro que quiere eliminar el empleado?", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
				public void onEvent(Event evt) throws InterruptedException {
					if (evt.getName().equals("onOK")) {
						workerRepository.delete(currentWorker);
                                                sortingPagingHelper.reset();
                                                currentWorker = null;
                                                refreshView();
                                                
						alert("Empleado eliminado.");
					}
				}
			});
		
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
