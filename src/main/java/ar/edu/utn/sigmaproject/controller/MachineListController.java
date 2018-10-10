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

import ar.edu.utn.sigmaproject.domain.Machine;
import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.service.MachineRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderRepository;

public class MachineListController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Listbox machineListbox;

	@WireVariable
	private MachineRepository machineRepository;
	@WireVariable
	private ProductionOrderRepository productionOrderRepository;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		createListener();// listener para cuando se modifique la lista
		refreshView();
	}

	private void refreshView() {
		machineListbox.setModel(new ListModelList<Machine>(machineRepository.findAll()));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createListener() {
		EventQueue<Event> eq = EventQueues.lookup("Machine Update Queue", EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener() {
			public void onEvent(Event event) throws Exception {
				if(event.getName().equals("onMachineUpdate")) {
					refreshView();
				}
			}
		});
	}

	@Listen("onSelect = #machineListbox")
	public void doListBoxSelect() {
		if(machineListbox.getSelectedItem() == null) {
			//just in case for the no selection
		} else {
			doModalWindow(machineListbox.getSelectedItem().getValue());
		}
		machineListbox.clearSelection();
	}

	@Listen("onClick = #newButton")
	public void newButtonClick() {
		doModalWindow(null);
	}

	private void doModalWindow(Object object) {
		Executions.getCurrent().setAttribute("selected_machine", object);
		final Window win = (Window) Executions.createComponents("/machine_creation.zul", null, null);
		win.setSizable(false);
		win.setPosition("center");
		win.doModal();
	}

	private boolean isMachineAssigned(Machine machine) {
		for(ProductionOrder eachProductionOrder : productionOrderRepository.findAll()) {
			for(ProductionOrderDetail eachProductionOrderDetail : eachProductionOrder.getDetails()) {
				if(eachProductionOrderDetail.getMachine().getId() == machine.getId()) {
					return true;
				}
			}
		}
		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onDeleteMachine = #machineListbox")
	public void deleteButtonClick(final ForwardEvent ForwEvt) {
		final Machine machine = (Machine) ForwEvt.getData();
		if(isMachineAssigned(machine)) {
			Messagebox.show("No se puede eliminar, la maquina se encuentra asignada a uno o mas ordenes de produccion.", "Informacion", Messagebox.OK, Messagebox.ERROR);
			return;
		}
		Messagebox.show("Desea eliminar?", "Confirmar", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
			public void onEvent(Event evt) throws InterruptedException {
				if (evt.getName().equals("onOK")) {
					machineRepository.delete(machine);
					refreshView();
				}
			}
		});
	}
}