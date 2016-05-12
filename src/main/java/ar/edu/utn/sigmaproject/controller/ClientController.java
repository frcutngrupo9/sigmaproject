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
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Client;
import ar.edu.utn.sigmaproject.service.ClientRepository;
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

	// atributes
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
		currentClient = new Client("", "", "", "", "");
		refreshView();
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(Strings.isBlank(nameTextbox.getText())){
			Clients.showNotification("Debe ingresar un nombre", nameTextbox);
			return;
		}
		currentClient.setName(nameTextbox.getText());
		currentClient.setPhone(phoneTextbox.getText());
		currentClient.setEmail(emailTextbox.getText());
		currentClient.setAddress(addressTextbox.getText());
		currentClient.setDetails(detailsTextbox.getText());
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
		clientListbox.clearSelection();
	}

	private void refreshView() {
		if(currentClient == null) {// no se esta editando ni creando
			clientGrid.setVisible(false);
			nameTextbox.setValue(null);
			phoneTextbox.setValue(null);
			addressTextbox.setValue(null);
			detailsTextbox.setValue(null);

			saveButton.setDisabled(true);
			cancelButton.setDisabled(true);
			resetButton.setDisabled(true);
			deleteButton.setDisabled(true);
		} else {// editando o creando
			clientGrid.setVisible(true);
			nameTextbox.setValue(currentClient.getName());
			phoneTextbox.setValue(currentClient.getPhone());
			emailTextbox.setValue(currentClient.getEmail());
			addressTextbox.setValue(currentClient.getAddress());
			detailsTextbox.setValue(currentClient.getDetails());

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