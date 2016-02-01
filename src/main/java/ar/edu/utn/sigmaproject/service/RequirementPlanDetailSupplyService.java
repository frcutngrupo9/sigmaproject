package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.RequirementPlanDetailSupply;

public interface RequirementPlanDetailSupplyService {
	
	List<RequirementPlanDetailSupply> getRequirementPlanDetailSupplyList();
	
	List<RequirementPlanDetailSupply> getRequirementPlanDetailSupplyList(Integer idRequirementPlan);
	
	RequirementPlanDetailSupply getRequirementPlanDetailSupply(Integer idRequirementPlan, Integer idSupplyType);

	RequirementPlanDetailSupply saveRequirementPlanDetailSupply(RequirementPlanDetailSupply requirementPlanDetailSupply);

	RequirementPlanDetailSupply updateRequirementPlanDetailSupply(RequirementPlanDetailSupply requirementPlanDetailSupply);

	void deleteRequirementPlanDetailSupply(RequirementPlanDetailSupply requirementPlanDetailSupply);

	void deleteAll(Integer idRequirementPlan);

}
