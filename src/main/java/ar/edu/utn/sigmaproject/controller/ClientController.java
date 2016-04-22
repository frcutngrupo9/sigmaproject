package ar.edu.utn.sigmaproject.controller;

import java.util.LinkedHashMap;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.ext.Selectable;

import ar.edu.utn.sigmaproject.domain.Client;
import ar.edu.utn.sigmaproject.service.ClientRepository;
import ar.edu.utn.sigmaproject.util.SortingPagingHelper;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ClientController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
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
	Textbox nameTextBox;
	@Wire
	Textbox phoneTextBox;
	@Wire
	Textbox emailTextBox;
	@Wire
	Textbox addressTextBox;
	@Wire
	Textbox detailsTextBox;

	// services
	@WireVariable
	private ClientRepository clientRepository;

	// atributes
	private String query;

	private Client currentClient;

	private SortingPagingHelper<Client> sortingPagingHelper;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		LinkedHashMap<String, Boolean> sortProperties = new LinkedHashMap<String, Boolean>();
		sortProperties.put("name", Boolean.TRUE);
		sortingPagingHelper = new SortingPagingHelper<>(clientRepository, clientListbox, searchTextbox, pager, sortProperties, 0);
		refreshView();
	}

	@Listen("onClick = #searchButton")
	public void search() {
		query = searchTextbox.getValue();
		sortingPagingHelper.resetUnsorted();
	}

	@Listen("onClick = #newButton")
	public void newButtonClick() {
		currentClient = new Client("", "", "", "", "");
		refreshView();
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(Strings.isBlank(nameTextBox.getText())){
			Clients.showNotification("Debe ingresar un nombre", nameTextBox);
			return;
		}
		currentClient.setName(nameTextBox.getText());
		currentClient.setPhone(phoneTextBox.getText());
		currentClient.setEmail(emailTextBox.getText());
		currentClient.setAddress(addressTextBox.getText());
		currentClient.setDetails(detailsTextBox.getText());
		currentClient = clientRepository.save(currentClient);
		sortingPagingHelper.reloadCurrentPage();
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
		clientRepository.delete(currentClient);
		currentClient = null;
		refreshView();
	}

	@SuppressWarnings("unchecked")
	@Listen("onSelect = #clientListbox")
	public void doListBoxSelect() {
		if (clientListbox.getSelectedItem() == null) {
			// just in case for the no selection
			currentClient = null;
		} else if (currentClient == null) {// si no hay nada editandose
			currentClient = clientListbox.getSelectedItem().getValue();
			refreshView();
		}
		((Selectable<Client>) clientListbox.getListModel()).clearSelection();
	}

	private void refreshView() {
		if(currentClient == null) {// no se esta editando ni creando
			clientGrid.setVisible(false);
			nameTextBox.setValue(null);
			phoneTextBox.setValue(null);
			addressTextBox.setValue(null);
			detailsTextBox.setValue(null);

			saveButton.setDisabled(true);
			cancelButton.setDisabled(true);
			resetButton.setDisabled(true);
			deleteButton.setDisabled(true);
		} else {// editando o creando
			clientGrid.setVisible(true);
			nameTextBox.setValue(currentClient.getName());
			phoneTextBox.setValue(currentClient.getPhone());
			emailTextBox.setValue(currentClient.getEmail());
			addressTextBox.setValue(currentClient.getAddress());
			detailsTextBox.setValue(currentClient.getDetails());

			saveButton.setDisabled(false);
			cancelButton.setDisabled(false);
			resetButton.setDisabled(false);
			if(currentClient.getId() == null) {
				deleteButton.setDisabled(true);
			} else {
				deleteButton.setDisabled(false);
			}
		}
	}
}