package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.Client;

public interface ClientService  {
    
    List<Client> getClientList();
    
    Client getClient(Integer idClient);

    Client saveClient(Client client);

    Client updateClient(Client client);

    void deleteClient(Client client);

    Integer getNewId();

}