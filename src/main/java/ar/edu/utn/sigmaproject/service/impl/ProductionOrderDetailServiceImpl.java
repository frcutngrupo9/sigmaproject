package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.service.ProductionOrderDetailService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class ProductionOrderDetailServiceImpl implements ProductionOrderDetailService {

	static List<ProductionOrderDetail> productionOrderDetailList = new ArrayList<ProductionOrderDetail>();
	private SerializationService serializator = new SerializationService("production_order_detail");
	
	public ProductionOrderDetailServiceImpl() {
		List<ProductionOrderDetail> aux = serializator.obtenerLista();
		if(aux != null) {
			productionOrderDetailList = aux;
		} else {
			serializator.grabarLista(productionOrderDetailList);
		}
	}
	
	//synchronized para prevenir acceso concurrente al servicio de lista
	public synchronized List<ProductionOrderDetail> getProductionOrderDetailList() {
		List<ProductionOrderDetail> list = new ArrayList<ProductionOrderDetail>();
		for(ProductionOrderDetail each:productionOrderDetailList) {
			list.add(ProductionOrderDetail.clone(each));
		}
		return list;
	}
	
	public synchronized List<ProductionOrderDetail> getProductionOrderDetailList(Integer idProductionOrder) {
		List<ProductionOrderDetail> list = new ArrayList<ProductionOrderDetail>();
		for(ProductionOrderDetail productionPlanDetail:productionOrderDetailList) {
			if(productionPlanDetail.getIdProductionOrder().equals(idProductionOrder)) {
				list.add(ProductionOrderDetail.clone(productionPlanDetail));
			}
		}
		return list;
	}
	
	public synchronized ProductionOrderDetail getProductionOrderDetail(Integer idProductionOrder, Integer idProcess) {
		int size = productionOrderDetailList.size();
		for(int i = 0; i < size; i++) {
			ProductionOrderDetail aux = productionOrderDetailList.get(i);
			if(aux.getIdProductionOrder().equals(idProductionOrder) && aux.getIdProcess().equals(idProcess)) {
				return ProductionOrderDetail.clone(aux);
			}
		}
		return null;
	}
	
	public synchronized ProductionOrderDetail saveProductionOrderDetail(ProductionOrderDetail productionOrderDetail) {
		productionOrderDetail = ProductionOrderDetail.clone(productionOrderDetail);
		productionOrderDetailList.add(productionOrderDetail);
		serializator.grabarLista(productionOrderDetailList);
		return productionOrderDetail;
	}
	
	public synchronized ProductionOrderDetail updateProductionOrderDetail(ProductionOrderDetail productionOrderDetail) {
		if(productionOrderDetail.getIdProductionOrder()==null || productionOrderDetail.getIdProcess()==null) {
			throw new IllegalArgumentException("can't update a null-id ProductionOrderDetail, save it first");
		} else {
			productionOrderDetail = ProductionOrderDetail.clone(productionOrderDetail);
			int size = productionOrderDetailList.size();
			for(int i = 0; i < size; i++) {
				ProductionOrderDetail aux = productionOrderDetailList.get(i);
				if(aux.getIdProductionOrder().equals(productionOrderDetail.getIdProductionOrder()) && aux.getIdProcess().equals(productionOrderDetail.getIdProcess())) {
					productionOrderDetailList.set(i, productionOrderDetail);
					serializator.grabarLista(productionOrderDetailList);
					return productionOrderDetail;
				}
			}
			throw new RuntimeException("ProductionOrderDetail not found "+productionOrderDetail.getIdProductionOrder()+" "+productionOrderDetail.getIdProcess());
		}
	}
	
	public synchronized void deleteProductionOrderDetail(ProductionOrderDetail productionOrderDetail) {
		if(productionOrderDetail.getIdProductionOrder()!=null && productionOrderDetail.getIdProcess()!=null) {
			int size = productionOrderDetailList.size();
			for(int i = 0; i < size; i++) {
				ProductionOrderDetail aux = productionOrderDetailList.get(i);
				if(aux.getIdProductionOrder().equals(productionOrderDetail.getIdProductionOrder()) && aux.getIdProcess().equals(productionOrderDetail.getIdProcess())) {
					productionOrderDetailList.remove(i);
					serializator.grabarLista(productionOrderDetailList);
					return;
				}
			}
		}
	}

	public void deleteAll(Integer idProductionOrder) {
		List<ProductionOrderDetail> deleteList = getProductionOrderDetailList(idProductionOrder);
		for(ProductionOrderDetail delete : deleteList) {
    		deleteProductionOrderDetail(delete);
		}
	}
}
