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
        currentClient = new Client(null, "", "", "", "", "");
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
        if(currentClient.getId() == null) {// nuevo cliente
            currentClient = clientService.saveClient(currentClient);
        } else {
            // si es una actualizacion
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
    }
    
    private void refreshView() {
    	clientListModel.clearSelection();
    	clientListbox.setModel(clientListModel);// se actualiza la lista
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
            newButton.setDisabled(false);
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
            newButton.setDisabled(true);
        }
    }
}