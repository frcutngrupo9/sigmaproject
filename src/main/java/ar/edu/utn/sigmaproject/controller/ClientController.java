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
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zk.ui.event.Event;

import ar.edu.utn.sigmaproject.domain.Client;
import ar.edu.utn.sigmaproject.service.ClientRepository;
import ar.edu.utn.sigmaproject.service.OrderRepository;
import ar.edu.utn.sigmaproject.util.SortingPagingHelper;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ClientController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Button searchButton;
	@Wire
	Listbox clientListbox;
	@Wire
	Paging pager;
	@Wire
	Button newButton;
	@Wire
	Grid clientGrid;
	@Wire
	Button saveButton;
	@Wire
	Button cancelButton;
	@Wire
	Button resetButton;
	@Wire
	Button deleteButton;
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

	// services
	@WireVariable
	private ClientRepository clientRepository;
	@WireVariable
	private OrderRepository orderRepository;

	// atributes
	@SuppressWarnings("unused")
	private String query;
	private Client currentClient;
	private SortingPagingHelper<Client> sortingPagingHelper;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		Map<Integer, String> sortProperties = new HashMap<>();
		sortProperties.put(0, "name");
		sortingPagingHelper = new SortingPagingHelper<>(clientRepository, clientListbox, searchButton, searchTextbox, pager, sortProperties);
		refreshView();
	}

	@Listen("onClick = #newButton")
	public void newButtonClick() {
		currentClient = null;
		refreshView();
		clientGrid.setVisible(true);
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
		if(currentClient == null) {// nuevo
			currentClient = new Client(name, phone, email, address, details);
		} else {// actualizacion
			currentClient.setName(name);
			currentClient.setPhone(phone);
			currentClient.setEmail(email);
			currentClient.setAddress(address);
			currentClient.setDetails(details);
		}
		currentClient = clientRepository.save(currentClient);
		currentClient = null;
		refreshView();
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		currentClient = null;
		refreshView();
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
	}

	@Listen("onClick = #deleteButton")
	public void deleteButtonClick() {
		// busca si el cliente esta agregado a algun pedido y se cancela la eliminacion en ese caso
		if(orderRepository.findByClient(currentClient).isEmpty() == false) {
			Messagebox.show("No se puede eliminar, el cliente se encuentra asignado a 1 o mas pedidos.", "Informacion", Messagebox.OK, Messagebox.ERROR);
			return;
		}
                Messagebox.show("Esta seguro que quiere eliminar el cliente?", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
				public void onEvent(Event evt) throws InterruptedException {
					if (evt.getName().equals("onOK")) {
						clientRepository.delete(currentClient);
                                                currentClient = null;
                                                refreshView();
                                                
						alert("Cliente eliminado.");
					}
				}
			});
		
	}

	@Listen("onSelect = #clientListbox")
	public void doListBoxSelect() {
		if(clientListbox.getSelectedItem() == null) {
			// just in case for the no selection
			currentClient = null;
		} else if (currentClient == null) {// si no hay nada editandose
			currentClient = clientListbox.getSelectedItem().getValue();
			refreshView();
		}
		clientListbox.clearSelection();
	}

	private void refreshView() {
		sortingPagingHelper.reset();// se actualiza la lista
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		newButton.setDisabled(false);
		if(currentClient == null) {// creando
			clientGrid.setVisible(false);
			nameTextbox.setValue(null);
			phoneTextbox.setValue(null);
			emailTextbox.setValue(null);
			addressTextbox.setValue(null);
			detailsTextbox.setValue(null);
			deleteButton.setDisabled(true);
			resetButton.setDisabled(true);// al crear, el boton new cumple la misma funcion q el reset
		} else {// editando
			clientGrid.setVisible(true);
			nameTextbox.setValue(currentClient.getName());
			phoneTextbox.setValue(currentClient.getPhone());
			emailTextbox.setValue(currentClient.getEmail());
			addressTextbox.setValue(currentClient.getAddress());
			detailsTextbox.setValue(currentClient.getDetails());
			deleteButton.setDisabled(false);
			resetButton.setDisabled(false);
		}
	}
}