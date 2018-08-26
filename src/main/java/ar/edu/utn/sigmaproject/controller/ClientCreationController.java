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
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.Client;
import ar.edu.utn.sigmaproject.domain.WorkHour;
import ar.edu.utn.sigmaproject.service.ClientRepository;
import ar.edu.utn.sigmaproject.service.WorkHourRepository;

public class ClientCreationController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;
	
	@Wire
	Window clientCreationWindow;
	@Wire
	Textbox nameTextbox;
	@Wire
	Textbox phoneTextbox;
	@Wire
	Textbox emailTextbox;
	@Wire
	Textbox addressTextbox;
	@Wire
	Textbox detailsTextbox;

	@WireVariable
	private ClientRepository clientRepository;

	private Client currentClient;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentClient = (Client) Executions.getCurrent().getAttribute("selected_client");
		refreshView();
	}

	private void refreshView() {
		if(currentClient != null) {
			nameTextbox.setValue(currentClient.getName());
			phoneTextbox.setValue(currentClient.getPhone());
			emailTextbox.setValue(currentClient.getEmail());
			addressTextbox.setValue(currentClient.getAddress());
			detailsTextbox.setValue(currentClient.getDetails());
		} else {
			nameTextbox.setValue(null);
			phoneTextbox.setValue(null);
			emailTextbox.setValue(null);
			addressTextbox.setValue(null);
			detailsTextbox.setValue(null);
		}
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		clientCreationWindow.detach();
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(Strings.isBlank(nameTextbox.getText())) {
			Clients.showNotification("Debe ingresar un nombre", nameTextbox);
			return;
		}
		String name = nameTextbox.getText().toUpperCase();
		String phone = phoneTextbox.getText();
		String email = emailTextbox.getText();
		String address = addressTextbox.getText();
		String details = detailsTextbox.getText();
		if(currentClient == null) {
			// nuevo
			currentClient = new Client(name, phone, email, address, details);
		} else {
			// edicion
			currentClient.setName(name);
			currentClient.setPhone(phone);
			currentClient.setEmail(email);
			currentClient.setAddress(address);
			currentClient.setDetails(details);
		}
		clientRepository.save(currentClient);
		EventQueue<Event> eq = EventQueues.lookup("Client Update Queue", EventQueues.DESKTOP, true);
		eq.publish(new Event("onClientUpdate", null, currentClient));
		clientCreationWindow.detach();
	}

}
