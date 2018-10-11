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

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import javax.xml.datatype.Duration;

import ar.edu.utn.sigmaproject.domain.Machine;
import ar.edu.utn.sigmaproject.domain.MachineType;
import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.service.MachineRepository;
import ar.edu.utn.sigmaproject.service.MachineTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderRepository;

public class MachineCreationController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Window machineCreationWindow;
	@Wire
	Textbox nameTextbox;
	@Wire
	Intbox yearIntbox;
	@Wire
	Bandbox machineTypeBandbox;
	@Wire
	Listbox machineTypeListbox;

	@WireVariable
	private MachineRepository machineRepository;
	@WireVariable
	private MachineTypeRepository machineTypeRepository;
	@WireVariable
	private ProductionOrderRepository productionOrderRepository;

	private Machine currentMachine;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentMachine = (Machine) Executions.getCurrent().getAttribute("selected_machine");
		refreshView();
	}

	private void refreshView() {
		machineTypeListbox.setModel(new ListModelList<>(machineTypeRepository.findAll()));
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int currentYear = cal.get(Calendar.YEAR);
		if(currentMachine != null) {
			nameTextbox.setValue(currentMachine.getName());
			if(currentMachine.getYear() != null) {
				yearIntbox.setValue(currentMachine.getYear());
			} else {
				yearIntbox.setValue(currentYear);
			}
			machineTypeBandbox.setValue(currentMachine.getMachineType().getName());
		} else {
			nameTextbox.setValue(null);
			yearIntbox.setValue(currentYear);
			machineTypeBandbox.setValue(null);
			machineTypeListbox.clearSelection();
		}
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		machineCreationWindow.detach();
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(Strings.isBlank(nameTextbox.getText())){
			Clients.showNotification("Debe ingresar un nombre", nameTextbox);
			return;
		}
		if(yearIntbox.getValue() == null || yearIntbox.getValue() <= 0) {
			Clients.showNotification("Debe ingresar valor mayor a cero", yearIntbox);
			return;
		}
		if(Strings.isBlank(machineTypeBandbox.getText())){
			Clients.showNotification("Debe seleccionar un tipo de maquina", machineTypeBandbox);
			return;
		}
		String name = nameTextbox.getText();
		int year = yearIntbox.getValue();
		MachineType machineType = machineTypeRepository.findFirstByName(machineTypeBandbox.getValue());
		Duration duration = null;
		if(currentMachine == null) {
			// nuevo
			currentMachine = new Machine(machineType, name, year, duration);
		} else {
			// edicion
			if(currentMachine.getMachineType().getId()!=machineType.getId() && isMachineAssigned(currentMachine)) {
				Messagebox.show("No se puede modificar el tipo de maquina asignado, la maquina se encuentra asignada a uno o mas ordenes de produccion.", "Informacion", Messagebox.OK, Messagebox.ERROR);
				return;
			}
			currentMachine.setName(name);
			currentMachine.setYear(year);
			currentMachine.setMachineType(machineType);
		}
		machineRepository.save(currentMachine);
		EventQueue<Event> eq = EventQueues.lookup("Machine Update Queue", EventQueues.DESKTOP, true);
		eq.publish(new Event("onMachineUpdate"));
		machineCreationWindow.detach();
	}
	
	private boolean isMachineAssigned(Machine machine) {
		for(ProductionOrder eachProductionOrder : productionOrderRepository.findAll()) {
			for(ProductionOrderDetail eachProductionOrderDetail : eachProductionOrder.getDetails()) {
				if(eachProductionOrderDetail.getMachine()!=null && eachProductionOrderDetail.getMachine().getId() == machine.getId()) {
					return true;
				}
			}
		}
		return false;
	}
}