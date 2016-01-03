package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.ProductionOrder;

public interface ProductionOrderService {
	
	List<ProductionOrder> getProductionOrderList();
	
	List<ProductionOrder> getProductionOrderList(Integer idProductionPlan);
	
	ProductionOrder getProductionOrder(Integer idProductionOrder);

	ProductionOrder saveProductionOrder(ProductionOrder productionOrder);

	ProductionOrder updateProductionOrder(ProductionOrder productionOrder);

	void deleteProductionOrder(ProductionOrder productionOrder);

}
