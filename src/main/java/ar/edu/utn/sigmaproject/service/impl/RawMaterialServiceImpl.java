package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

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
			if(rawMaterial.isClone() == false) {
				list.add(RawMaterial.clone(rawMaterial));
			}
		}
		return list;
	}
	
	public synchronized List<RawMaterial> getRawMaterialList(Integer idProduct) {
		List<RawMaterial> list = new ArrayList<RawMaterial>();
		for(RawMaterial rawMaterial:rawMaterialList) {
			if(rawMaterial.getIdProduct().equals(idProduct) && rawMaterial.isClone() == false) {
				list.add(RawMaterial.clone(rawMaterial));
			}
		}
		return list;
	}
	
	public synchronized RawMaterial getRawMaterial(Integer id) {
		int size = rawMaterialList.size();
		for(int i = 0; i < size; i++) {
			RawMaterial aux = rawMaterialList.get(i);
			if(aux.getId().equals(id)) {
				return RawMaterial.clone(aux);
			}
		}
		return null;
	}
	
	public synchronized RawMaterial getRawMaterial(Integer idProduct, Integer idRawMaterialType) {
		int size = rawMaterialList.size();
		for(int i = 0; i < size; i++) {
			RawMaterial aux = rawMaterialList.get(i);
			if(aux.getIdProduct().equals(idProduct) && aux.getIdRawMaterialType().equals(idRawMaterialType) && aux.isClone() == false) {
				return RawMaterial.clone(aux);
			}
		}
		return null;
	}
	
	public synchronized RawMaterial saveRawMaterial(RawMaterial rawMaterial) {
		if(rawMaterial.getId() == null) {
			Integer newId = getNewId();
			rawMaterial.setId(newId);
		}
		rawMaterial = RawMaterial.clone(rawMaterial);
		rawMaterialList.add(rawMaterial);
		serializator.grabarLista(rawMaterialList);
		return rawMaterial;
	}
	
	public synchronized RawMaterial updateRawMaterial(RawMaterial rawMaterial) {
		if(rawMaterial.getId() == null) {
			throw new IllegalArgumentException("can't update a null-id raw material, save it first");
		} else {
			rawMaterial = RawMaterial.clone(rawMaterial);
			int size = rawMaterialList.size();
			for(int i = 0; i < size; i++) {
				RawMaterial aux = rawMaterialList.get(i);
				if(aux.getId().equals(rawMaterial.getId())) {
					rawMaterialList.set(i, rawMaterial);
					serializator.grabarLista(rawMaterialList);
					return rawMaterial;
				}
			}
			throw new RuntimeException("RawMaterial not found " + rawMaterial.getId());
		}
	}
	
	public synchronized void deleteRawMaterial(RawMaterial rawMaterial) {
		if(rawMaterial.getId() != null) {
			int size = rawMaterialList.size();
			for(int i = 0; i < size; i++) {
				RawMaterial aux = rawMaterialList.get(i);
				if(aux.getId().equals(rawMaterial.getId())) {
					rawMaterialList.remove(i);
					serializator.grabarLista(rawMaterialList);
					return;
				}
			}
		}
	}
	
	public synchronized void deleteAll(Integer idProduct) {
		List<RawMaterial> listDelete = getRawMaterialList(idProduct);
		for(RawMaterial delete:listDelete) {
			deleteRawMaterial(delete);
		}
	}
	
	public synchronized Integer getNewId() {
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
