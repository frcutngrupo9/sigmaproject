package ar.edu.utn.sigmaproject.controller;

import java.util.List;

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Client;
import ar.edu.utn.sigmaproject.service.ClientService;
import ar.edu.utn.sigmaproject.service.impl.ClientServiceImpl;

public class ClientController extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Listbox clientListbox;
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
	private ClientService clientService = new ClientServiceImpl();

	// atributes
	private Client currentClient;

	// list
	private List<Client> clientList;

	// list models
	private ListModelList<Client> clientListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		clientList = clientService.getClientList();
		clientListModel = new ListModelList<Client>(clientList);
		clientListbox.setModel(clientListModel);
		currentClient = null;
		refreshView();
	}

	@Listen("onClick = #searchButton")
	public void search() {
	}

	@Listen("onClick = #newButton")
	public void newButtonClick() {
		currentClient = null;
		refreshView();
		clientGrid.setVisible(true);
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(Strings.isBlank(nameTextbox.getText())){
			Clients.showNotification("Debe ingresar un nombre", nameTextbox);
			return;
		}
		String name = nameTextbox.getText();
		String phone = phoneTextbox.getText();
		String email = emailTextbox.getText();
		String address = addressTextbox.getText();
		String details = detailsTextbox.getText();
		if(currentClient == null) {// nuevo
			currentClient = new Client(null, name, phone, email, address, details);
			currentClient = clientService.saveClient(currentClient);
		} else {// actualizacion
			currentClient.setName(name);
			currentClient.setPhone(phone);
			currentClient.setEmail(email);
			currentClient.setAddress(address);
			currentClient.setDetails(details);
			currentClient = clientService.updateClient(currentClient);
		}
		clientList = clientService.getClientList();
		clientListModel = new ListModelList<Client>(clientList);
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
		clientService.deleteClient(currentClient);
		clientListModel.remove(currentClient);
		currentClient = null;
		refreshView();
	}

	@Listen("onSelect = #clientListbox")
	public void doListBoxSelect() {
		if(clientListModel.isSelectionEmpty()) {
			//just in case for the no selection
			currentClient = null;
		} else {
			if(currentClient == null) {// si no hay nada editandose
				currentClient = clientListModel.getSelection().iterator().next();
				refreshView();
			}
		}
		clientListModel.clearSelection();
	}

	private void refreshView() {
		clientListModel.clearSelection();
		clientListbox.setModel(clientListModel);// se actualiza la lista
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