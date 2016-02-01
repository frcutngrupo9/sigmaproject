package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.RequirementPlan;
import ar.edu.utn.sigmaproject.domain.RequirementPlanDetailSupply;
import ar.edu.utn.sigmaproject.domain.RequirementPlanDetailRawMaterial;

public interface RequirementPlanService {
	
	List<RequirementPlan> getRequirementPlanList();
	
	RequirementPlan getRequirementPlan(Integer idRequirementPlan);

	RequirementPlan saveRequirementPlan(RequirementPlan requirementPlan);

	RequirementPlan updateRequirementPlan(RequirementPlan requirementPlan);

	void deleteRequirementPlan(RequirementPlan requirementPlan);

	RequirementPlan saveRequirementPlan(RequirementPlan requirementPlan, List<RequirementPlanDetailSupply> requirementPlanDetailSupplyList, List<RequirementPlanDetailRawMaterial> requirementPlanDetailRawMaterialList);

	RequirementPlan updateRequirementPlan(RequirementPlan requirementPlan, List<RequirementPlanDetailSupply> requirementPlanDetailSupplyList, List<RequirementPlanDetailRawMaterial> requirementPlanDetailRawMaterialList);

}
