package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.SupplyRequirement;
import ar.edu.utn.sigmaproject.service.SupplyRequirementService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class SupplyRequirementServiceImpl implements SupplyRequirementService {

	static List<SupplyRequirement> supplyRequirementList = new ArrayList<SupplyRequirement>();
	private SerializationService serializator = new SerializationService("supply_requirement");

	public SupplyRequirementServiceImpl() {
		@SuppressWarnings("unchecked")
		List<SupplyRequirement> aux = serializator.obtenerLista();
		if(aux != null) {
			supplyRequirementList = aux;
		} else {
			serializator.grabarLista(supplyRequirementList);
		}
	}

	//synchronized para prevenir acceso concurrente al servicio de lista
	public synchronized List<SupplyRequirement> getSupplyRequirementList() {
		List<SupplyRequirement> list = new ArrayList<SupplyRequirement>();
		for(SupplyRequirement each:supplyRequirementList) {
			list.add(SupplyRequirement.clone(each));
		}
		return list;
	}

	public synchronized List<SupplyRequirement> getSupplyRequirementList(Integer idProductionPlan) {
		List<SupplyRequirement> list = new ArrayList<SupplyRequirement>();
		for(SupplyRequirement each:supplyRequirementList) {
			if(each.getIdProductionPlan().equals(idProductionPlan)) {
				list.add(SupplyRequirement.clone(each));
			}
		}
		return list;
	}

	public synchronized SupplyRequirement getSupplyRequirement(Integer id) {
		int size = supplyRequirementList.size();
		for(int i = 0; i < size; i++) {
			SupplyRequirement aux = supplyRequirementList.get(i);
			if(aux.getId().equals(id)) {
				return SupplyRequirement.clone(aux);
			}
		}
		return null;
	}

	public synchronized SupplyRequirement getSupplyRequirement(Integer idProductionPlan, Integer idSupplyType) {
		int size = supplyRequirementList.size();
		for(int i = 0; i < size; i++) {
			SupplyRequirement aux = supplyRequirementList.get(i);
			if(aux.getIdProductionPlan().equals(idProductionPlan) && aux.getIdSupplyType().equals(idSupplyType)) {
				return SupplyRequirement.clone(aux);
			}
		}
		return null;
	}

	public synchronized SupplyRequirement saveSupplyRequirement(SupplyRequirement supplyRequirement) {
		if(supplyRequirement.getId() == null) {
			Integer newId = getNewId();
			supplyRequirement.setId(newId);
		}
		supplyRequirement = SupplyRequirement.clone(supplyRequirement);
		supplyRequirementList.add(supplyRequirement);
		serializator.grabarLista(supplyRequirementList);
		return supplyRequirement;
	}

	public synchronized SupplyRequirement updateSupplyRequirement(SupplyRequirement supplyRequirement) {
		if(supplyRequirement.getId() == null) {
			throw new IllegalArgumentException("can't update a null-id SupplyRequirement, save it first");
		} else {
			supplyRequirement = SupplyRequirement.clone(supplyRequirement);
			int size = supplyRequirementList.size();
			for(int i = 0; i < size; i++) {
				SupplyRequirement aux = supplyRequirementList.get(i);
				if(aux.getId().equals(supplyRequirement.getId())) {
					supplyRequirementList.set(i, supplyRequirement);
					serializator.grabarLista(supplyRequirementList);
					return supplyRequirement;
				}
			}
			throw new RuntimeException("SupplyRequirement not found "+supplyRequirement.getId());
		}
	}

	public synchronized void deleteSupplyRequirement(SupplyRequirement supplyRequirement) {
		if(supplyRequirement.getId() != null) {
			int size = supplyRequirementList.size();
			for(int i = 0; i < size; i++) {
				SupplyRequirement aux = supplyRequirementList.get(i);
				if(aux.getId().equals(supplyRequirement.getId())) {
					supplyRequirementList.remove(i);
					serializator.grabarLista(supplyRequirementList);
					return;
				}
			}
		}
	}

	public synchronized void deleteAll(Integer idProductionPlan) {
		List<SupplyRequirement> deleteList = getSupplyRequirementList(idProductionPlan);
		for(SupplyRequirement delete : deleteList) {
			deleteSupplyRequirement(delete);
		}
	}

	private synchronized Integer getNewId() {
		Integer lastId = 0;
		for(int i = 0; i < supplyRequirementList.size(); i++) {
			SupplyRequirement aux = supplyRequirementList.get(i);
			if(lastId < aux.getId()) {
				lastId = aux.getId();
			}
		}
		return lastId + 1;
	}
}
