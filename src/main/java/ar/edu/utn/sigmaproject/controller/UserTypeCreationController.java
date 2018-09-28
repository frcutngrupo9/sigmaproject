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

import ar.edu.utn.sigmaproject.domain.UserType;
import ar.edu.utn.sigmaproject.service.UserTypeRepository;

public class UserTypeCreationController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Window userTypeCreationWindow;
	@Wire
	Textbox nameTextbox;
	@Wire
	Textbox detailsTextbox;

	@WireVariable
	private UserTypeRepository userTypeRepository;

	private UserType currentUserType;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentUserType = (UserType) Executions.getCurrent().getAttribute("selected_user_type");
		refreshView();
	}

	private void refreshView() {
		if(currentUserType != null) {
			nameTextbox.setValue(currentUserType.getName());
			detailsTextbox.setValue(currentUserType.getDetails());
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
		userTypeCreationWindow.detach();
	}

	private boolean isNameUsed(String name) {
		for(UserType each : userTypeRepository.findAll()) {
			if(each.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		String name = nameTextbox.getText();
		if(Strings.isBlank(name)) {
			Clients.showNotification("Debe ingresar un nombre", nameTextbox);
			return;
		}
		String details = detailsTextbox.getText();
		if(currentUserType == null) {
			// nuevo
			if(isNameUsed(name)) {
				Clients.showNotification("El tipo de usuario ya existe", nameTextbox);
				return;
			}
			currentUserType = new UserType(name, details);
		} else {
			// edicion
			currentUserType.setName(name);
			currentUserType.setDetails(details);
		}
		userTypeRepository.save(currentUserType);
		EventQueue<Event> eq = EventQueues.lookup("UserType Update Queue", EventQueues.DESKTOP, true);
		eq.publish(new Event("onUserTypeUpdate"));
		userTypeCreationWindow.detach();
	}
}