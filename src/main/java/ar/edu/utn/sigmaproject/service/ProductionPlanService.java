package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.ProductionPlan;

public interface ProductionPlanService  {
	
	List<ProductionPlan> getProductionPlanList();
	
	ProductionPlan getProductionPlan(Integer idProductionPlan);

	ProductionPlan saveProductionPlan(ProductionPlan productionPlan);

	ProductionPlan updateProductionPlan(ProductionPlan productionPlan);

	void deleteProductionPlan(ProductionPlan productionPlan);

	Integer getNewId();

}