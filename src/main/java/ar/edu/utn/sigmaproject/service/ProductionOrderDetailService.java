package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;

public interface ProductionOrderDetailService {

	List<ProductionOrderDetail> getProductionOrderDetailList();

	List<ProductionOrderDetail> getProductionOrderDetailList(Integer idProductionOrder);

	List<ProductionOrderDetail> getProductionOrderDetailListByProcessId(Integer idProcess);

	ProductionOrderDetail getProductionOrderDetail(Integer idProductionOrder, Integer idProcess);

	ProductionOrderDetail saveProductionOrderDetail(ProductionOrderDetail productionOrderDetail);

	ProductionOrderDetail updateProductionOrderDetail(ProductionOrderDetail productionOrderDetail);

	ProductionOrderDetail updateCloneProductionOrderDetail(ProductionOrderDetail productionOrderDetail, Process oldProcess);

	void deleteProductionOrderDetail(ProductionOrderDetail productionOrderDetail);

	void deleteAll(Integer idProductionOrder);

}
