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
    private Textbox searchTextbox;
    
    @Wire
    private Listbox clientListbox;
    
    @Wire
    private Button saveButton;
    
    @Wire
    private Button cancelButton;
    
    @Wire
    private Button resetButton;
    
    @Wire
    private Button newButton;
    
    @Wire
    private Textbox nameTextBox;

    @Wire
    private Textbox phoneTextBox;

    @Wire
    private Textbox addressTextBox;

    @Wire
    private Textbox detailsTextBox;
    
    
    @Wire
    private Grid clientGrid;
    
    private Client selectedClient;
    private ClientService clientService = new ClientServiceImpl();
    private ListModelList<Client> clientListModel;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception{
        super.doAfterCompose(comp);
        List<Client> clientList = clientService.getClientList();
        clientListModel = new ListModelList<Client>(clientList);
        clientListbox.setModel(clientListModel);
        selectedClient = null;
        
        updateUI();
    }
    
    @Listen("onClick = #searchButton")
    public void search() {
    }
    
    @Listen("onClick = #newButton")
    public void newButtonClick() {
        selectedClient = new Client(null, "", "", "", "");
        updateUI();
    }
    
    @Listen("onClick = #saveButton")
    public void saveButtonClick() {
        if(Strings.isBlank(nameTextBox.getText())){
            Clients.showNotification("Debe ingresar un nombre", nameTextBox);
            return;
        }
        selectedClient.setName(nameTextBox.getText());
        selectedClient.setPhone(phoneTextBox.getText());
        selectedClient.setAddress(addressTextBox.getText());
        selectedClient.setDetails(detailsTextBox.getText());
        if(selectedClient.getId() == null) {
            selectedClient = clientService.saveClient(selectedClient);
        } else {
            //si es una actualizacion
            selectedClient = clientService.updateClient(selectedClient);
        }
        List<Client> clientList = clientService.getClientList();
        clientListModel = new ListModelList<Client>(clientList);
        clientListbox.setModel(clientListModel);
        selectedClient = null;
        updateUI();
    }
    
    @Listen("onClick = #cancelButton")
    public void cancelButtonClick() {
        selectedClient = null;
        updateUI();
    }
    
    @Listen("onClick = #resetButton")
    public void resetButtonClick() {
        updateUI();
    }
    
    @Listen("onSelect = #clientListbox")
    public void doListBoxSelect() {
        if(clientListModel.isSelectionEmpty()){
            //just in case for the no selection
            selectedClient = null;
        }else{
            selectedClient = clientListModel.getSelection().iterator().next();
        }
        updateUI();
    }
    
    private void updateUI() {  
        if(selectedClient == null) {
            clientGrid.setVisible(false);
            nameTextBox.setValue(null);
            phoneTextBox.setValue(null);
            addressTextBox.setValue(null);
            detailsTextBox.setValue(null);
            
            saveButton.setDisabled(true);
            cancelButton.setDisabled(true);
            resetButton.setDisabled(true);
            newButton.setDisabled(false);
            clientListbox.clearSelection();
        }else {
            clientGrid.setVisible(true);
            nameTextBox.setValue(selectedClient.getName());
            phoneTextBox.setValue(selectedClient.getPhone());
            addressTextBox.setValue(selectedClient.getAddress());
            detailsTextBox.setValue(selectedClient.getDetails());
            
            saveButton.setDisabled(false);
            cancelButton.setDisabled(false);
            resetButton.setDisabled(false);
            newButton.setDisabled(true);
        }
    }
}