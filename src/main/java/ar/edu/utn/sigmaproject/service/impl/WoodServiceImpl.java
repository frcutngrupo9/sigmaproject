package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.Wood;
import ar.edu.utn.sigmaproject.service.WoodService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class WoodServiceImpl implements WoodService {
	static List<Wood> woodList = new ArrayList<Wood>();
	private SerializationService serializator = new SerializationService("wood");

	public WoodServiceImpl() {
		@SuppressWarnings("unchecked")
		List<Wood> aux = serializator.obtenerLista();
		if(aux != null) {
			woodList = aux;
		} else {
			serializator.grabarLista(woodList);
		}
	}

	public synchronized List<Wood> getWoodList() {
		List<Wood> list = new ArrayList<Wood>();
		for(Wood each: woodList) {
			list.add(Wood.clone(each));
		}
		return list;
	}

	public synchronized Wood getWood(Integer id) {
		int size = woodList.size();
		for(int i=0; i<size; i++) {
			Wood t = woodList.get(i);
			if(t.getId().equals(id)) {
				return Wood.clone(t);
			}
		}
		return null;
	}

	public synchronized Wood saveWood(Wood wood) {
		if(wood.getId() == null) {
			wood.setId(getNewId());
		}
		wood = Wood.clone(wood);
		woodList.add(wood);
		serializator.grabarLista(woodList);
		return wood;
	}

	public synchronized Wood updateWood(Wood wood) {
		if(wood.getId() == null) {
			throw new IllegalArgumentException("can't update a null-id Wood, save it first");
		}else {
			wood = Wood.clone(wood);
			int size = woodList.size();
			for(int i=0; i<size; i++) {
				Wood t = woodList.get(i);
				if(t.getId().equals(wood.getId())){
					woodList.set(i, wood);
					serializator.grabarLista(woodList);
					return wood;
				}
			}
			throw new RuntimeException("Wood not found " + wood.getId());
		}
	}

	public synchronized void deleteWood(Wood wood) {
		if(wood.getId() != null) {
			int size = woodList.size();
			for(int i=0; i<size; i++) {
				Wood t = woodList.get(i);
				if(t.getId().equals(wood.getId())){
					woodList.remove(i);
					serializator.grabarLista(woodList);
					return;
				}
			}
		}
	}

	private synchronized Integer getNewId() {
		Integer lastId = 0;
		for(int i=0; i<woodList.size(); i++) {
			Wood aux = woodList.get(i);
			if(lastId < aux.getId()){
				lastId = aux.getId();
			}
		}
		return lastId + 1;
	}
}
