package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.WoodExistence;
import ar.edu.utn.sigmaproject.domain.Wood;
import ar.edu.utn.sigmaproject.service.WoodExistenceService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class WoodExistenceServiceImpl implements WoodExistenceService {

	static List<WoodExistence> woodExistenceList = new ArrayList<WoodExistence>();
	private SerializationService serializator = new SerializationService("wood_existence");

	public WoodExistenceServiceImpl() {
		List<WoodExistence> aux = serializator.obtenerLista();
		if(aux != null) {
			woodExistenceList = aux;
		} else {
			serializator.grabarLista(woodExistenceList);
		}
	}

	public synchronized List<WoodExistence> getWoodExistenceList() {
		List<WoodExistence> list = new ArrayList<WoodExistence>();
		for(WoodExistence each : woodExistenceList) {
			list.add(WoodExistence.clone(each));
		}
		return list;
	}

	public synchronized WoodExistence getWoodExistence(Integer idWood) {
		int size = woodExistenceList.size();
		for(int i = 0; i < size; i++) {
			WoodExistence t = woodExistenceList.get(i);
			if(t.getIdWood().equals(idWood)) {
				return WoodExistence.clone(t);
			}
		}
		return null;
	}

	public synchronized WoodExistence saveWoodExistence(WoodExistence woodExistence) {
		if(woodExistence.getIdWood() == null) {
			throw new IllegalArgumentException("can't save a null-id WoodExistence");
		} else {
			Wood aux = (new WoodServiceImpl()).getWood(woodExistence.getIdWood());
			if(aux == null) {
				throw new IllegalArgumentException("Wood referenced by WoodExistence not found");
			} else {
				woodExistence = WoodExistence.clone(woodExistence);
				woodExistenceList.add(woodExistence);
				serializator.grabarLista(woodExistenceList);
			}
		}
		return woodExistence;
	}

	public synchronized WoodExistence updateWoodExistence(WoodExistence woodExistence) {
		if(woodExistence.getIdWood() == null) {
			throw new IllegalArgumentException("can't update a null-id WoodExistence, save it first");
		} else {
			woodExistence = WoodExistence.clone(woodExistence);
			int size = woodExistenceList.size();
			for(int i = 0; i < size; i++) {
				WoodExistence t = woodExistenceList.get(i);
				if(t.getIdWood().equals(woodExistence.getIdWood())) {
					woodExistenceList.set(i, woodExistence);
					serializator.grabarLista(woodExistenceList);
					return woodExistence;
				}
			}
			throw new RuntimeException("WoodExistence not found " + woodExistence.getIdWood());
		}
	}

	public synchronized void deleteWoodExistence(WoodExistence woodExistence) {
		if(woodExistence.getIdWood() != null) {
			int size = woodExistenceList.size();
			for(int i = 0; i < size; i++) {
				WoodExistence t = woodExistenceList.get(i);
				if(t.getIdWood().equals(woodExistence.getIdWood())) {
					woodExistenceList.remove(i);
					serializator.grabarLista(woodExistenceList);
					return;
				}
			}
		}
	}

}
