package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.RawMaterialRequirement;
import ar.edu.utn.sigmaproject.service.RawMaterialRequirementService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class RawMaterialRequirementServiceImpl implements RawMaterialRequirementService {

	static List<RawMaterialRequirement> rawMaterialRequirementList = new ArrayList<RawMaterialRequirement>();
	private SerializationService serializator = new SerializationService("raw_material_requirement");

	public RawMaterialRequirementServiceImpl() {
		List<RawMaterialRequirement> aux = serializator.obtenerLista();
		if(aux != null) {
			rawMaterialRequirementList = aux;
		} else {
			serializator.grabarLista(rawMaterialRequirementList);
		}
	}

	//synchronized para prevenir acceso concurrente al servicio de lista
	public synchronized List<RawMaterialRequirement> getRawMaterialRequirementList() {
		List<RawMaterialRequirement> list = new ArrayList<RawMaterialRequirement>();
		for(RawMaterialRequirement each:rawMaterialRequirementList) {
			list.add(RawMaterialRequirement.clone(each));
		}
		return list;
	}

	public synchronized List<RawMaterialRequirement> getRawMaterialRequirementList(Integer idProductionPlan) {
		List<RawMaterialRequirement> list = new ArrayList<RawMaterialRequirement>();
		for(RawMaterialRequirement each:rawMaterialRequirementList) {
			if(each.getIdProductionPlan().equals(idProductionPlan)) {
				list.add(RawMaterialRequirement.clone(each));
			}
		}
		return list;
	}

	public synchronized RawMaterialRequirement getRawMaterialRequirement(Integer id) {
		int size = rawMaterialRequirementList.size();
		for(int i = 0; i < size; i++) {
			RawMaterialRequirement aux = rawMaterialRequirementList.get(i);
			if(aux.getId().equals(id)) {
				return RawMaterialRequirement.clone(aux);
			}
		}
		return null;
	}

	public synchronized RawMaterialRequirement getRawMaterialRequirement(Integer idProductionPlan, Integer idRawMaterialType) {
		int size = rawMaterialRequirementList.size();
		for(int i = 0; i < size; i++) {
			RawMaterialRequirement aux = rawMaterialRequirementList.get(i);
			if(aux.getIdProductionPlan().equals(idProductionPlan) && aux.getIdRawMaterialType().equals(idRawMaterialType)) {
				return RawMaterialRequirement.clone(aux);
			}
		}
		return null;
	}

	public synchronized RawMaterialRequirement saveRawMaterialRequirement(RawMaterialRequirement rawMaterialRequirement) {
		if(rawMaterialRequirement.getId() == null) {
			Integer newId = getNewId();
			rawMaterialRequirement.setId(newId);
		}
		rawMaterialRequirement = RawMaterialRequirement.clone(rawMaterialRequirement);
		rawMaterialRequirementList.add(rawMaterialRequirement);
		serializator.grabarLista(rawMaterialRequirementList);
		return rawMaterialRequirement;
	}

	public synchronized RawMaterialRequirement updateRawMaterialRequirement(RawMaterialRequirement rawMaterialRequirement) {
		if(rawMaterialRequirement.getId() == null) {
			throw new IllegalArgumentException("can't update a null-id RawMaterialRequirement, save it first");
		} else {
			rawMaterialRequirement = RawMaterialRequirement.clone(rawMaterialRequirement);
			int size = rawMaterialRequirementList.size();
			for(int i = 0; i < size; i++) {
				RawMaterialRequirement aux = rawMaterialRequirementList.get(i);
				if(aux.getId().equals(rawMaterialRequirement.getId())) {
					rawMaterialRequirementList.set(i, rawMaterialRequirement);
					serializator.grabarLista(rawMaterialRequirementList);
					return rawMaterialRequirement;
				}
			}
			throw new RuntimeException("RawMaterialRequirement not found "+rawMaterialRequirement.getId());
		}
	}

	public synchronized void deleteRawMaterialRequirement(RawMaterialRequirement rawMaterialRequirement) {
		if(rawMaterialRequirement.getId() != null) {
			int size = rawMaterialRequirementList.size();
			for(int i = 0; i < size; i++) {
				RawMaterialRequirement aux = rawMaterialRequirementList.get(i);
				if(aux.getId().equals(rawMaterialRequirement.getId())) {
					rawMaterialRequirementList.remove(i);
					serializator.grabarLista(rawMaterialRequirementList);
					return;
				}
			}
		}
	}

	public void deleteAll(Integer idProductionPlan) {
		List<RawMaterialRequirement> deleteList = getRawMaterialRequirementList(idProductionPlan);
		for(RawMaterialRequirement delete : deleteList) {
			deleteRawMaterialRequirement(delete);
		}
	}

	private synchronized Integer getNewId() {
		Integer lastId = 0;
		for(int i = 0; i < rawMaterialRequirementList.size(); i++) {
			RawMaterialRequirement aux = rawMaterialRequirementList.get(i);
			if(lastId < aux.getId()) {
				lastId = aux.getId();
			}
		}
		return lastId + 1;
	}
}
