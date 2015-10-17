package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.ProductionPlanDetail;

public interface ProductionPlanDetailService {
	
	List<ProductionPlanDetail> getProductionPlanDetailList();
	
	List<ProductionPlanDetail> getProductionPlanDetailList(Integer idProductionPlan);
	
	ProductionPlanDetail getProductionPlanDetail(Integer idProductionPlan, Integer idProduct);

	ProductionPlanDetail saveProductionPlanDetail(ProductionPlanDetail productionPlanDetail);

	ProductionPlanDetail updateProductionPlanDetail(ProductionPlanDetail productionPlanDetail);

	void deleteProductionPlanDetail(ProductionPlanDetail productionPlanDetail);

}
