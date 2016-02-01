package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.RequirementPlan;
import ar.edu.utn.sigmaproject.domain.RequirementPlanDetailRawMaterial;
import ar.edu.utn.sigmaproject.domain.RequirementPlanDetailSupply;
import ar.edu.utn.sigmaproject.service.RequirementPlanService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class RequirementPlanServiceImpl implements RequirementPlanService {
	
	static List<RequirementPlan> requirementPlanList = new ArrayList<RequirementPlan>();
	private SerializationService serializator = new SerializationService("requirement_plan");
	
	public RequirementPlanServiceImpl() {
		List<RequirementPlan> aux = serializator.obtenerLista();
		if(aux != null) {
			requirementPlanList = aux;
		} else {
			serializator.grabarLista(requirementPlanList);
		}
	}
	
	public synchronized List<RequirementPlan> getRequirementPlanList() {
		List<RequirementPlan> list = new ArrayList<RequirementPlan>();
		for(RequirementPlan each:requirementPlanList) {
			list.add(RequirementPlan.clone(each));
		}
		return list;
	}
	
	public synchronized RequirementPlan getRequirementPlan(Integer id) {
		int size = requirementPlanList.size();
		for(int i = 0; i < size; i++) {
			RequirementPlan t = requirementPlanList.get(i);
			if(t.getId().equals(id)) {
				return RequirementPlan.clone(t);
			}
		}
		return null;
	}
	
	public synchronized RequirementPlan saveRequirementPlan(RequirementPlan requirementPlan) {
		if(requirementPlan.getId() == null) {
			requirementPlan.setId(getNewId());
        }
		requirementPlan = RequirementPlan.clone(requirementPlan);
		requirementPlanList.add(requirementPlan);
		serializator.grabarLista(requirementPlanList);
		return requirementPlan;
	}
	
	public synchronized RequirementPlan updateRequirementPlan(RequirementPlan requirementPlan) {
	    if(requirementPlan.getId() == null) {
	        throw new IllegalArgumentException("can't update a null-id requirement plan, save it first");
	    } else {
	        requirementPlan = RequirementPlan.clone(requirementPlan);
	        int size = requirementPlanList.size();
	        for(int i = 0; i < size; i++) {
	        	RequirementPlan aux = requirementPlanList.get(i);
	            if(aux.getId().equals(requirementPlan.getId())) {
	                requirementPlanList.set(i, requirementPlan);
	                serializator.grabarLista(requirementPlanList);
	                return requirementPlan;
	            }
	        }
	        throw new RuntimeException("Requirement Plan not found "+requirementPlan.getId());
	    }
	}
	
	public synchronized void deleteRequirementPlan(RequirementPlan requirementPlan) {
		if(requirementPlan.getId() != null) {
			int size = requirementPlanList.size();
			for(int i = 0; i < size; i++) {
				RequirementPlan t = requirementPlanList.get(i);
				if(t.getId().equals(requirementPlan.getId())) {
					requirementPlanList.remove(i);// eliminamos el plan
					serializator.grabarLista(requirementPlanList);
					return;
				}
			}
		}
	}
	
	private synchronized Integer getNewId() {
		Integer lastId = 0;
		for(int i = 0; i < requirementPlanList.size(); i++) {
			RequirementPlan aux = requirementPlanList.get(i);
			if(lastId < aux.getId()) {
				lastId = aux.getId();
			}
		}
		return lastId + 1;
	}

	public RequirementPlan saveRequirementPlan(
			RequirementPlan requirementPlan,
			List<RequirementPlanDetailSupply> requirementPlanDetailSupplyList,
			List<RequirementPlanDetailRawMaterial> requirementPlanDetailRawMaterialList) {
		// TODO Auto-generated method stub
		return null;
	}

	public RequirementPlan updateRequirementPlan(
			RequirementPlan requirementPlan,
			List<RequirementPlanDetailSupply> requirementPlanDetailSupplyList,
			List<RequirementPlanDetailRawMaterial> requirementPlanDetailRawMaterialList) {
		// TODO Auto-generated method stub
		return null;
	}
}
