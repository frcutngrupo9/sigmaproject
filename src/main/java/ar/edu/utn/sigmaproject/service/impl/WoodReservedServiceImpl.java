package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.WoodReserved;
import ar.edu.utn.sigmaproject.service.WoodReservedService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class WoodReservedServiceImpl implements WoodReservedService {
	static List<WoodReserved> woodReservedList = new ArrayList<WoodReserved>();
	private SerializationService serializator = new SerializationService("wood_reserved");

	public WoodReservedServiceImpl() {
		@SuppressWarnings("unchecked")
		List<WoodReserved> aux = serializator.obtenerLista();
		if(aux != null) {
			woodReservedList = aux;
		} else {
			serializator.grabarLista(woodReservedList);
		}
	}

	public synchronized List<WoodReserved> getWoodReservedList() {
		List<WoodReserved> list = new ArrayList<WoodReserved>();
		for(WoodReserved each : woodReservedList) {
			list.add(WoodReserved.clone(each));
		}
		return list;
	}

	public synchronized WoodReserved getWoodReserved(Integer id) {
		int size = woodReservedList.size();
		for(int i = 0; i < size; i++) {
			WoodReserved t = woodReservedList.get(i);
			if(t.getId().equals(id)) {
				return WoodReserved.clone(t);
			}
		}
		return null;
	}

	public synchronized WoodReserved saveWoodReserved(WoodReserved woodReserved) {
		if(woodReserved.getId() == null) {
			Integer newId = getNewId();
			woodReserved.setId(newId);
		}
		woodReserved = WoodReserved.clone(woodReserved);
		woodReservedList.add(woodReserved);
		serializator.grabarLista(woodReservedList);
		return woodReserved;
	}

	public synchronized WoodReserved updateWoodReserved(WoodReserved woodReserved) {
		if(woodReserved.getId() == null) {
			throw new IllegalArgumentException("can't update a null-id WoodReserved, save it first");
		} else {
			woodReserved = WoodReserved.clone(woodReserved);
			int size = woodReservedList.size();
			for(int i = 0; i < size; i++) {
				WoodReserved t = woodReservedList.get(i);
				if(t.getId().equals(woodReserved.getId())) {
					woodReservedList.set(i, woodReserved);
					serializator.grabarLista(woodReservedList);
					return woodReserved;
				}
			}
			throw new RuntimeException("WoodReserved not found " + woodReserved.getId());
		}
	}

	public synchronized void deleteWoodReserved(WoodReserved woodReserved) {
		if(woodReserved.getId() != null) {
			int size = woodReservedList.size();
			for(int i = 0; i < size; i++) {
				WoodReserved t = woodReservedList.get(i);
				if(t.getId().equals(woodReserved.getId())) {
					woodReservedList.remove(i);
					serializator.grabarLista(woodReservedList);
					return;
				}
			}
		}
	}
	
	private synchronized Integer getNewId() {
		Integer lastId = 0;
		for(int i = 0; i < woodReservedList.size(); i++) {
			WoodReserved aux = woodReservedList.get(i);
			if(lastId < aux.getId()) {
				lastId = aux.getId();
			}
		}
		return lastId + 1;
	}
}
