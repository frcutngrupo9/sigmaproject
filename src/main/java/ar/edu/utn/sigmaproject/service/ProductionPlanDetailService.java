package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.ProductionPlanDetail;

public interface ProductionPlanDetailService {
	
	List<ProductionPlanDetail> getProductionPlanDetailList();
	
	ProductionPlanDetail getProductionPlanDetail(Integer idProductionPlan, Integer idProduct);

	ProductionPlanDetail saveProductionPlanDetail(ProductionPlanDetail process);

	ProductionPlanDetail updateProductionPlanDetail(ProductionPlanDetail process);

	void deleteProductionPlanDetail(ProductionPlanDetail process);

}
