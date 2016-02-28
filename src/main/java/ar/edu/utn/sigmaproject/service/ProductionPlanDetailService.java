package ar.edu.utn.sigmaproject.service;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.ProductTotal;
import ar.edu.utn.sigmaproject.domain.ProductionPlanDetail;

public interface ProductionPlanDetailService {

	List<ProductionPlanDetail> getProductionPlanDetailList();

	List<ProductionPlanDetail> getProductionPlanDetailList(Integer idProductionPlan);

	ProductionPlanDetail getProductionPlanDetail(Integer idProductionPlan, Integer idOrder);

	ArrayList<ProductTotal> getProductTotalList(Integer idProductionPlan);

	ProductionPlanDetail saveProductionPlanDetail(ProductionPlanDetail productionPlanDetail);

	ProductionPlanDetail updateProductionPlanDetail(ProductionPlanDetail productionPlanDetail);

	void deleteProductionPlanDetail(ProductionPlanDetail productionPlanDetail);

	void deleteAll(Integer idProductionPlan);

}
