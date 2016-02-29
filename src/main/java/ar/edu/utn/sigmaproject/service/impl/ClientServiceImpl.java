package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.Client;
import ar.edu.utn.sigmaproject.service.ClientService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class ClientServiceImpl implements ClientService {

	static List<Client> clientList = new ArrayList<Client>();
	private SerializationService serializator = new SerializationService("client");

	public ClientServiceImpl() {
		@SuppressWarnings("unchecked")
		List<Client> aux = serializator.obtenerLista();
		if(aux != null) {
			clientList = aux;
		}else {
			serializator.grabarLista(clientList);
		}
	}

	public synchronized List<Client> getClientList() {
		List<Client> list = new ArrayList<Client>();
		for(Client client: clientList) {
			list.add(Client.clone(client));
		}
		return list;
	}

	public synchronized Client getClient(Integer id) {
		int size = clientList.size();
		for(int i=0; i<size; i++) {
			Client t = clientList.get(i);
			if(t.getId().equals(id)) {
				return Client.clone(t);
			}
		}
		return null;
	}

	public synchronized Client saveClient(Client client) {
		if(client.getId() == null) {
			client.setId(getNewId());
		}
		client = Client.clone(client);
		clientList.add(client);
		serializator.grabarLista(clientList);
		return client;
	}

	public synchronized Client updateClient(Client client) {
		if(client.getId() == null) {
			throw new IllegalArgumentException("can't update a null-id client, save it first");
		}else {
			client = Client.clone(client);
			int size = clientList.size();
			for(int i=0;i<size;i++) {
				Client t = clientList.get(i);
				if(t.getId().equals(client.getId())) {
					clientList.set(i, client);
					serializator.grabarLista(clientList);
					return client;
				}
			}
			throw new RuntimeException("Client not found "+client.getId());
		}
	}

	public synchronized void deleteClient(Client client) {
		if(client.getId() != null) {
			int size = clientList.size();
			for(int i=0; i<size; i++){
				Client t = clientList.get(i);
				if(t.getId().equals(client.getId())) {
					clientList.remove(i);
					serializator.grabarLista(clientList);
					return;
				}
			}
		}
	}

	private synchronized Integer getNewId() {
		Integer lastId = 0;
		for(int i=0;i<clientList.size();i++){
			Client aux = clientList.get(i);
			if(lastId < aux.getId()){
				lastId = aux.getId();
			}
		}
		return lastId + 1;
	}

	public synchronized Client getClient(String name) {
		int size = clientList.size();
		for(int i=0; i<size; i++) {
			Client t = clientList.get(i);
			if(t.getName().equalsIgnoreCase(name)) {
				return Client.clone(t);
			}
		}
		return null;
	}

}
