package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.RequirementPlanDetailRawMaterial;
import ar.edu.utn.sigmaproject.service.RequirementPlanDetailRawMaterialService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class RequirementPlanDetailRawMaterialServiceImpl implements RequirementPlanDetailRawMaterialService {
	
	static List<RequirementPlanDetailRawMaterial> requirementPlanDetailRawMaterialList = new ArrayList<RequirementPlanDetailRawMaterial>();
	private SerializationService serializator = new SerializationService("requirement_plan_detail_raw_material");
	
	public RequirementPlanDetailRawMaterialServiceImpl() {
		List<RequirementPlanDetailRawMaterial> aux = serializator.obtenerLista();
		if(aux != null) {
			requirementPlanDetailRawMaterialList = aux;
		} else {
			serializator.grabarLista(requirementPlanDetailRawMaterialList);
		}
	}
	
	//synchronized para prevenir acceso concurrente al servicio de lista
	public synchronized List<RequirementPlanDetailRawMaterial> getRequirementPlanDetailRawMaterialList() {
		List<RequirementPlanDetailRawMaterial> list = new ArrayList<RequirementPlanDetailRawMaterial>();
		for(RequirementPlanDetailRawMaterial each:requirementPlanDetailRawMaterialList) {
			list.add(RequirementPlanDetailRawMaterial.clone(each));
		}
		return list;
	}
	
	public synchronized List<RequirementPlanDetailRawMaterial> getRequirementPlanDetailRawMaterialList(Integer idRequirementPlan) {
		List<RequirementPlanDetailRawMaterial> list = new ArrayList<RequirementPlanDetailRawMaterial>();
		for(RequirementPlanDetailRawMaterial each:requirementPlanDetailRawMaterialList) {
			if(each.getIdRequirementPlan().equals(idRequirementPlan)) {
				list.add(RequirementPlanDetailRawMaterial.clone(each));
			}
		}
		return list;
	}
	
	public synchronized RequirementPlanDetailRawMaterial getRequirementPlanDetailRawMaterial(Integer idRequirementPlan, Integer idSupplyType) {
		int size = requirementPlanDetailRawMaterialList.size();
		for(int i = 0; i < size; i++) {
			RequirementPlanDetailRawMaterial aux = requirementPlanDetailRawMaterialList.get(i);
			if(aux.getIdRequirementPlan().equals(idRequirementPlan) && aux.getIdRawMaterialType().equals(idSupplyType)) {
				return RequirementPlanDetailRawMaterial.clone(aux);
			}
		}
		return null;
	}
	
	public synchronized RequirementPlanDetailRawMaterial saveRequirementPlanDetailRawMaterial(RequirementPlanDetailRawMaterial requirementPlanDetailRawMaterial) {
		requirementPlanDetailRawMaterial = RequirementPlanDetailRawMaterial.clone(requirementPlanDetailRawMaterial);
		requirementPlanDetailRawMaterialList.add(requirementPlanDetailRawMaterial);
		serializator.grabarLista(requirementPlanDetailRawMaterialList);
		return requirementPlanDetailRawMaterial;
	}
	
	public synchronized RequirementPlanDetailRawMaterial updateRequirementPlanDetailRawMaterial(RequirementPlanDetailRawMaterial requirementPlanDetailRawMaterial) {
		if(requirementPlanDetailRawMaterial.getIdRequirementPlan()==null || requirementPlanDetailRawMaterial.getIdRawMaterialType()==null) {
			throw new IllegalArgumentException("can't update a null-id RequirementPlanDetailRawMaterial, save it first");
		} else {
			requirementPlanDetailRawMaterial = RequirementPlanDetailRawMaterial.clone(requirementPlanDetailRawMaterial);
			int size = requirementPlanDetailRawMaterialList.size();
			for(int i = 0; i < size; i++) {
				RequirementPlanDetailRawMaterial aux = requirementPlanDetailRawMaterialList.get(i);
				if(aux.getIdRequirementPlan().equals(requirementPlanDetailRawMaterial.getIdRequirementPlan()) && aux.getIdRawMaterialType().equals(requirementPlanDetailRawMaterial.getIdRawMaterialType())) {
					requirementPlanDetailRawMaterialList.set(i, requirementPlanDetailRawMaterial);
					serializator.grabarLista(requirementPlanDetailRawMaterialList);
					return requirementPlanDetailRawMaterial;
				}
			}
			throw new RuntimeException("RequirementPlanDetailRawMaterial not found "+requirementPlanDetailRawMaterial.getIdRequirementPlan()+" "+requirementPlanDetailRawMaterial.getIdRawMaterialType());
		}
	}
	
	public synchronized void deleteRequirementPlanDetailRawMaterial(RequirementPlanDetailRawMaterial requirementPlanDetailRawMaterial) {
		if(requirementPlanDetailRawMaterial.getIdRequirementPlan()!=null && requirementPlanDetailRawMaterial.getIdRawMaterialType()!=null) {
			int size = requirementPlanDetailRawMaterialList.size();
			for(int i = 0; i < size; i++) {
				RequirementPlanDetailRawMaterial aux = requirementPlanDetailRawMaterialList.get(i);
				if(aux.getIdRequirementPlan().equals(requirementPlanDetailRawMaterial.getIdRequirementPlan()) && aux.getIdRawMaterialType().equals(requirementPlanDetailRawMaterial.getIdRawMaterialType())) {
					requirementPlanDetailRawMaterialList.remove(i);
					serializator.grabarLista(requirementPlanDetailRawMaterialList);
					return;
				}
			}
		}
	}

	public void deleteAll(Integer idRequirementPlan) {
		List<RequirementPlanDetailRawMaterial> deleteList = getRequirementPlanDetailRawMaterialList(idRequirementPlan);
		for(RequirementPlanDetailRawMaterial delete : deleteList) {
    		deleteRequirementPlanDetailRawMaterial(delete);
		}
	}
	
}
