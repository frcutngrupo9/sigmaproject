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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.WoodType;
import ar.edu.utn.sigmaproject.service.WoodTypeRepository;

public class WoodTypeCreationController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Window woodTypeCreationWindow;
	@Wire
	Textbox nameTextbox;
	@Wire
	Textbox detailsTextbox;

	@WireVariable
	private WoodTypeRepository woodTypeRepository;

	private WoodType currentWoodType;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentWoodType = (WoodType) Executions.getCurrent().getAttribute("selected_wood_type");
		refreshView();
	}

	private void refreshView() {
		if(currentWoodType != null) {
			nameTextbox.setValue(currentWoodType.getName());
			detailsTextbox.setValue(currentWoodType.getDetails());
		} else {
			nameTextbox.setValue(null);
			detailsTextbox.setValue(null);
		}
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		woodTypeCreationWindow.detach();
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(Strings.isBlank(nameTextbox.getText())){
			Clients.showNotification("Debe ingresar un nombre", nameTextbox);
			return;
		}
		String name = nameTextbox.getText();
		String details = detailsTextbox.getText();
		if(currentWoodType == null) {
			// nuevo
			currentWoodType = new WoodType(name, details);
		} else {
			// edicion
			currentWoodType.setName(name);
			currentWoodType.setDetails(details);
		}
		woodTypeRepository.save(currentWoodType);
		EventQueue<Event> eq = EventQueues.lookup("WoodType Update Queue", EventQueues.DESKTOP, true);
		eq.publish(new Event("onWoodTypeUpdate"));
		woodTypeCreationWindow.detach();
	}
}