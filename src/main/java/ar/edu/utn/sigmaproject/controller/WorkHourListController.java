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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.WorkHour;
import ar.edu.utn.sigmaproject.domain.Worker;
import ar.edu.utn.sigmaproject.domain.WorkerRole;
import ar.edu.utn.sigmaproject.service.ProductRepository;
import ar.edu.utn.sigmaproject.service.WorkHourRepository;
import ar.edu.utn.sigmaproject.service.WorkerRepository;

public class WorkHourListController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Listbox workHourListbox;

	@WireVariable
	private WorkHourRepository workHourRepository;
	@WireVariable
	private ProductRepository productRepository;
	@WireVariable
	private WorkerRepository workerRepository;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		createListener();// listener para cuando se modifique la lista
		refreshView();
	}

	private void refreshView() {
		workHourListbox.setModel(new ListModelList<WorkHour>(workHourRepository.findAll()));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createListener() {
		EventQueue<Event> eq = EventQueues.lookup("WorkHour Update Queue", EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener() {
			public void onEvent(Event event) throws Exception {
				if(event.getName().equals("onWorkHourUpdate")) {
					refreshView();
				}
			}
		});
	}

	@Listen("onSelect = #workHourListbox")
	public void doListBoxSelect() {
		if(workHourListbox.getSelectedItem() == null) {
			//just in case for the no selection
		} else {
			doModalWindow(workHourListbox.getSelectedItem().getValue());
		}
		workHourListbox.clearSelection();
	}

	@Listen("onClick = #newButton")
	public void newButtonClick() {
		doModalWindow(null);
	}

	private void doModalWindow(Object object) {
		Executions.getCurrent().setAttribute("selected_work_hour", object);
		final Window win = (Window) Executions.createComponents("/work_hour_creation.zul", null, null);
		win.setSizable(false);
		win.setPosition("center");
		win.doModal();
	}

	private boolean isWorkHourAssignedToProcess(WorkHour workHour) {
		for(Product eachProduct : productRepository.findAll()) {
			for(Piece eachPiece : eachProduct.getPieces()) {
				for(Process eachProcess : eachPiece.getProcesses()) {
					if(eachProcess.getWorkHour().getId() == workHour.getId()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean isWorkHourAssignedToWorker(WorkHour workHour) {
		for(Worker eachWorker : workerRepository.findAll()) {
			for(WorkerRole eachWorkerRole : eachWorker.getWorkerRoleList()) {
				if(eachWorkerRole.getWorkHour().getId() == workHour.getId()) {
					return true;
				}
			}
		}
		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onDeleteWorkHour = #workHourListbox")
	public void deleteButtonClick(final ForwardEvent ForwEvt) {
		final WorkHour workHour = (WorkHour) ForwEvt.getData();
		if(isWorkHourAssignedToProcess(workHour)) {
			Messagebox.show("No se puede eliminar, el rol se encuentra asignado a uno o mas procesos.", "Informacion", Messagebox.OK, Messagebox.ERROR);
			return;
		}
		if(isWorkHourAssignedToWorker(workHour)) {
			Messagebox.show("No se puede eliminar, el rol se encuentra asignado a uno o mas empleados.", "Informacion", Messagebox.OK, Messagebox.ERROR);
			return;
		}
		Messagebox.show("Desea eliminar?", "Confirmar", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
			public void onEvent(Event evt) throws InterruptedException {
				if (evt.getName().equals("onOK")) {
					workHourRepository.delete(workHour);
					refreshView();
				}
			}
		});
	}
}
