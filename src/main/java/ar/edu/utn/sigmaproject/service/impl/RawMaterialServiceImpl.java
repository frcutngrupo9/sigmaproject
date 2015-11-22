package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.RawMaterial;
import ar.edu.utn.sigmaproject.service.RawMaterialService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class RawMaterialServiceImpl implements RawMaterialService {
	
	static List<RawMaterial> rawMaterialList = new ArrayList<RawMaterial>();
	private SerializationService serializator = new SerializationService("raw_material");
	
	public RawMaterialServiceImpl() {
		List<RawMaterial> aux = serializator.obtenerLista();
		if(aux != null) {
			rawMaterialList = aux;
		} else {
			serializator.grabarLista(rawMaterialList);
		}
	}
	
	public synchronized List<RawMaterial> getRawMaterialList() {
		List<RawMaterial> list = new ArrayList<RawMaterial>();
		for(RawMaterial rawMaterial:rawMaterialList) {
			list.add(RawMaterial.clone(rawMaterial));
		}
		return list;
	}
	
	public synchronized RawMaterial getRawMaterial(Integer id) {
		int size = rawMaterialList.size();
		for(int i = 0; i < size; i++) {
			RawMaterial t = rawMaterialList.get(i);
			if(t.getId().equals(id)) {
				return RawMaterial.clone(t);
			}
		}
		return null;
	}
	
	public synchronized RawMaterial saveRawMaterial(RawMaterial rawMaterial) {
		if(rawMaterial.getId() == null) {
			rawMaterial.setId(getNewId());
        }
		rawMaterial = RawMaterial.clone(rawMaterial);
		rawMaterialList.add(rawMaterial);
		serializator.grabarLista(rawMaterialList);
		return rawMaterial;
	}
	
	public synchronized RawMaterial updateRawMaterial(RawMaterial rawMaterial) {
	    if(rawMaterial.getId() == null) {
	        throw new IllegalArgumentException("can't update a null-id product, save it first");
	    } else {
	        rawMaterial = RawMaterial.clone(rawMaterial);
	        int size = rawMaterialList.size();
	        for(int i = 0; i < size; i++) {
	            RawMaterial t = rawMaterialList.get(i);
	            if(t.getId().equals(rawMaterial.getId())) {
	                rawMaterialList.set(i, rawMaterial);
	                serializator.grabarLista(rawMaterialList);
	                return rawMaterial;
	            }
	        }
	        throw new RuntimeException("RawMaterial not found "+rawMaterial.getId());
	    }
	}
	
	public synchronized void deleteRawMaterial(RawMaterial rawMaterial) {
		if(rawMaterial.getId() != null) {
			int size = rawMaterialList.size();
			for(int i = 0; i < size; i++) {
				RawMaterial t = rawMaterialList.get(i);
				if(t.getId().equals(rawMaterial.getId())) {
					rawMaterialList.remove(i);
					serializator.grabarLista(rawMaterialList);
					return;
				}
			}
		}
	}
	
	private synchronized Integer getNewId() {
		Integer lastId = 0;
		for(int i = 0; i < rawMaterialList.size(); i++) {
			RawMaterial aux = rawMaterialList.get(i);
			if(lastId < aux.getId()) {
				lastId = aux.getId();
			}
		}
		return lastId + 1;
	}

}
