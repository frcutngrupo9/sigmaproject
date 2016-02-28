package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.RawMaterialType;
import ar.edu.utn.sigmaproject.service.RawMaterialTypeService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class RawMaterialTypeServiceImpl implements RawMaterialTypeService {

	static List<RawMaterialType> rawMaterialTypeList = new ArrayList<RawMaterialType>();
	private SerializationService serializator = new SerializationService("raw_material_type");

	public RawMaterialTypeServiceImpl() {
		List<RawMaterialType> aux = serializator.obtenerLista();
		if(aux != null) {
			rawMaterialTypeList = aux;
		} else {
			serializator.grabarLista(rawMaterialTypeList);
		}
	}

	public synchronized List<RawMaterialType> getRawMaterialTypeList() {
		List<RawMaterialType> list = new ArrayList<RawMaterialType>();
		for(RawMaterialType rawMaterialType:rawMaterialTypeList) {
			list.add(RawMaterialType.clone(rawMaterialType));
		}
		return list;
	}

	public synchronized RawMaterialType getRawMaterialType(Integer id) {
		int size = rawMaterialTypeList.size();
		for(int i = 0; i < size; i++) {
			RawMaterialType t = rawMaterialTypeList.get(i);
			if(t.getId().equals(id)) {
				return RawMaterialType.clone(t);
			}
		}
		return null;
	}

	public synchronized RawMaterialType saveRawMaterialType(RawMaterialType rawMaterialType) {
		if(rawMaterialType.getId() == null) {
			rawMaterialType.setId(getNewId());
		}
		rawMaterialType = RawMaterialType.clone(rawMaterialType);
		rawMaterialTypeList.add(rawMaterialType);
		serializator.grabarLista(rawMaterialTypeList);
		return rawMaterialType;
	}

	public synchronized RawMaterialType updateRawMaterialType(RawMaterialType rawMaterialType) {
		if(rawMaterialType.getId() == null) {
			throw new IllegalArgumentException("can't update a null-id product, save it first");
		} else {
			rawMaterialType = RawMaterialType.clone(rawMaterialType);
			int size = rawMaterialTypeList.size();
			for(int i = 0; i < size; i++) {
				RawMaterialType t = rawMaterialTypeList.get(i);
				if(t.getId().equals(rawMaterialType.getId())) {
					rawMaterialTypeList.set(i, rawMaterialType);
					serializator.grabarLista(rawMaterialTypeList);
					return rawMaterialType;
				}
			}
			throw new RuntimeException("RawMaterialType not found "+rawMaterialType.getId());
		}
	}

	public synchronized void deleteRawMaterialType(RawMaterialType rawMaterialType) {
		if(rawMaterialType.getId() != null) {
			int size = rawMaterialTypeList.size();
			for(int i = 0; i < size; i++) {
				RawMaterialType t = rawMaterialTypeList.get(i);
				if(t.getId().equals(rawMaterialType.getId())) {
					rawMaterialTypeList.remove(i);
					serializator.grabarLista(rawMaterialTypeList);
					return;
				}
			}
		}
	}

	private synchronized Integer getNewId() {
		Integer lastId = 0;
		for(int i = 0; i < rawMaterialTypeList.size(); i++) {
			RawMaterialType aux = rawMaterialTypeList.get(i);
			if(lastId < aux.getId()) {
				lastId = aux.getId();
			}
		}
		return lastId + 1;
	}

}
