package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;

public interface ProductionOrderService {
	
	List<ProductionOrder> getProductionOrderList();
	
	List<ProductionOrder> getProductionOrderList(Integer idProductionPlan);
	
	ProductionOrder getProductionOrder(Integer idProductionOrder);

	ProductionOrder saveProductionOrder(ProductionOrder productionOrder);
	
	ProductionOrder saveProductionOrder(ProductionOrder productionOrder, List<ProductionOrderDetail> productionOrderDetailList);

	ProductionOrder updateProductionOrder(ProductionOrder productionOrder);
	
	ProductionOrder updateProductionOrder(ProductionOrder productionOrder, List<ProductionOrderDetail> productionOrderDetailList);

	void deleteProductionOrder(ProductionOrder productionOrder);

}
