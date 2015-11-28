package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.ProductionPlanState;

public interface ProductionPlanStateService {

	List<ProductionPlanState> getProductionPlanStateList();
	
	List<ProductionPlanState> getProductionPlanStateList(Integer idProductionPlan);
	
	ProductionPlanState getProductionPlanState(Integer idProductionPlan, Integer idProductionPlanStateType);
	
	ProductionPlanState getLastProductionPlanState(Integer idProductionPlan);

	ProductionPlanState saveProductionPlanState(ProductionPlanState productionPlanState);

	ProductionPlanState updateProductionPlanState(ProductionPlanState productionPlanState);

	void deleteProductionPlanState(ProductionPlanState productionPlanState);

}
