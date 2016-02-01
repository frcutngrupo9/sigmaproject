package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.RequirementPlanDetailSupply;
import ar.edu.utn.sigmaproject.service.RequirementPlanDetailSupplyService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class RequirementPlanDetailSupplyServiceImpl implements RequirementPlanDetailSupplyService {
	
	static List<RequirementPlanDetailSupply> requirementPlanDetailSupplyList = new ArrayList<RequirementPlanDetailSupply>();
	private SerializationService serializator = new SerializationService("requirement_plan_detail_supply");
	
	public RequirementPlanDetailSupplyServiceImpl() {
		List<RequirementPlanDetailSupply> aux = serializator.obtenerLista();
		if(aux != null) {
			requirementPlanDetailSupplyList = aux;
		} else {
			serializator.grabarLista(requirementPlanDetailSupplyList);
		}
	}
	
	//synchronized para prevenir acceso concurrente al servicio de lista
	public synchronized List<RequirementPlanDetailSupply> getRequirementPlanDetailSupplyList() {
		List<RequirementPlanDetailSupply> list = new ArrayList<RequirementPlanDetailSupply>();
		for(RequirementPlanDetailSupply each:requirementPlanDetailSupplyList) {
			list.add(RequirementPlanDetailSupply.clone(each));
		}
		return list;
	}
	
	public synchronized List<RequirementPlanDetailSupply> getRequirementPlanDetailSupplyList(Integer idRequirementPlan) {
		List<RequirementPlanDetailSupply> list = new ArrayList<RequirementPlanDetailSupply>();
		for(RequirementPlanDetailSupply each:requirementPlanDetailSupplyList) {
			if(each.getIdRequirementPlan().equals(idRequirementPlan)) {
				list.add(RequirementPlanDetailSupply.clone(each));
			}
		}
		return list;
	}
	
	public synchronized RequirementPlanDetailSupply getRequirementPlanDetailSupply(Integer idRequirementPlan, Integer idSupplyType) {
		int size = requirementPlanDetailSupplyList.size();
		for(int i = 0; i < size; i++) {
			RequirementPlanDetailSupply aux = requirementPlanDetailSupplyList.get(i);
			if(aux.getIdRequirementPlan().equals(idRequirementPlan) && aux.getIdSupplyType().equals(idSupplyType)) {
				return RequirementPlanDetailSupply.clone(aux);
			}
		}
		return null;
	}
	
	public synchronized RequirementPlanDetailSupply saveRequirementPlanDetailSupply(RequirementPlanDetailSupply requirementPlanDetailSupply) {
		requirementPlanDetailSupply = RequirementPlanDetailSupply.clone(requirementPlanDetailSupply);
		requirementPlanDetailSupplyList.add(requirementPlanDetailSupply);
		serializator.grabarLista(requirementPlanDetailSupplyList);
		return requirementPlanDetailSupply;
	}
	
	public synchronized RequirementPlanDetailSupply updateRequirementPlanDetailSupply(RequirementPlanDetailSupply requirementPlanDetailSupply) {
		if(requirementPlanDetailSupply.getIdRequirementPlan()==null || requirementPlanDetailSupply.getIdSupplyType()==null) {
			throw new IllegalArgumentException("can't update a null-id RequirementPlanDetailSupply, save it first");
		} else {
			requirementPlanDetailSupply = RequirementPlanDetailSupply.clone(requirementPlanDetailSupply);
			int size = requirementPlanDetailSupplyList.size();
			for(int i = 0; i < size; i++) {
				RequirementPlanDetailSupply aux = requirementPlanDetailSupplyList.get(i);
				if(aux.getIdRequirementPlan().equals(requirementPlanDetailSupply.getIdRequirementPlan()) && aux.getIdSupplyType().equals(requirementPlanDetailSupply.getIdSupplyType())) {
					requirementPlanDetailSupplyList.set(i, requirementPlanDetailSupply);
					serializator.grabarLista(requirementPlanDetailSupplyList);
					return requirementPlanDetailSupply;
				}
			}
			throw new RuntimeException("RequirementPlanDetailSupply not found "+requirementPlanDetailSupply.getIdRequirementPlan()+" "+requirementPlanDetailSupply.getIdSupplyType());
		}
	}
	
	public synchronized void deleteRequirementPlanDetailSupply(RequirementPlanDetailSupply requirementPlanDetailSupply) {
		if(requirementPlanDetailSupply.getIdRequirementPlan()!=null && requirementPlanDetailSupply.getIdSupplyType()!=null) {
			int size = requirementPlanDetailSupplyList.size();
			for(int i = 0; i < size; i++) {
				RequirementPlanDetailSupply aux = requirementPlanDetailSupplyList.get(i);
				if(aux.getIdRequirementPlan().equals(requirementPlanDetailSupply.getIdRequirementPlan()) && aux.getIdSupplyType().equals(requirementPlanDetailSupply.getIdSupplyType())) {
					requirementPlanDetailSupplyList.remove(i);
					serializator.grabarLista(requirementPlanDetailSupplyList);
					return;
				}
			}
		}
	}

	public void deleteAll(Integer idRequirementPlan) {
		List<RequirementPlanDetailSupply> deleteList = getRequirementPlanDetailSupplyList(idRequirementPlan);
		for(RequirementPlanDetailSupply delete : deleteList) {
    		deleteRequirementPlanDetailSupply(delete);
		}
	}
	
}
