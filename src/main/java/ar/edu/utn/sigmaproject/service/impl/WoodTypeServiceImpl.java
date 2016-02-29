package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.WoodType;
import ar.edu.utn.sigmaproject.service.WoodTypeService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class WoodTypeServiceImpl implements WoodTypeService {
	static List<WoodType> woodTypeList = new ArrayList<WoodType>();
	private SerializationService serializator = new SerializationService("wood_type");
	static int woodTypeId = 1;
	static {
		woodTypeList.add(new WoodType(woodTypeId++,"Pino", "semi-pesada, semi-dura"));
		woodTypeList.add(new WoodType(woodTypeId++,"Caoba", "tradicional, dura y compacta"));
		woodTypeList.add(new WoodType(woodTypeId++,"Nogal", "dura, homogénea"));
		woodTypeList.add(new WoodType(woodTypeId++,"Roble", "resistente, duradera y compacta"));
	}

	public WoodTypeServiceImpl() {
		@SuppressWarnings("unchecked")
		List<WoodType> aux = serializator.obtenerLista();
		if(aux != null) {
			woodTypeList = aux;
		} else {
			serializator.grabarLista(woodTypeList);
		}
	}

	public synchronized List<WoodType> getWoodTypeList() {
		List<WoodType> list = new ArrayList<WoodType>();
		for(WoodType each: woodTypeList) {
			list.add(WoodType.clone(each));
		}
		return list;
	}

	public synchronized WoodType getWoodType(Integer id) {
		int size = woodTypeList.size();
		for(int i=0; i<size; i++) {
			WoodType t = woodTypeList.get(i);
			if(t.getId().equals(id)) {
				return WoodType.clone(t);
			}
		}
		return null;
	}

	public synchronized WoodType saveWoodType(WoodType woodType) {
		if(woodType.getId() == null) {
			woodType.setId(getNewId());
		}
		woodType = WoodType.clone(woodType);
		woodTypeList.add(woodType);
		serializator.grabarLista(woodTypeList);
		return woodType;
	}

	public synchronized WoodType updateWoodType(WoodType woodType) {
		if(woodType.getId() == null) {
			throw new IllegalArgumentException("can't update a null-id WoodType, save it first");
		}else {
			woodType = WoodType.clone(woodType);
			int size = woodTypeList.size();
			for(int i=0; i<size; i++) {
				WoodType t = woodTypeList.get(i);
				if(t.getId().equals(woodType.getId())){
					woodTypeList.set(i, woodType);
					serializator.grabarLista(woodTypeList);
					return woodType;
				}
			}
			throw new RuntimeException("WoodType not found " + woodType.getId());
		}
	}

	public synchronized void deleteWoodType(WoodType woodType) {
		if(woodType.getId() != null) {
			int size = woodTypeList.size();
			for(int i=0; i<size; i++) {
				WoodType t = woodTypeList.get(i);
				if(t.getId().equals(woodType.getId())){
					woodTypeList.remove(i);
					serializator.grabarLista(woodTypeList);
					return;
				}
			}
		}
	}

	private synchronized Integer getNewId() {
		Integer lastId = 0;
		for(int i=0; i<woodTypeList.size(); i++) {
			WoodType aux = woodTypeList.get(i);
			if(lastId < aux.getId()){
				lastId = aux.getId();
			}
		}
		return lastId + 1;
	}
}
