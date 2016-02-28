package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.service.ProductionOrderDetailService;
import ar.edu.utn.sigmaproject.service.ProductionOrderService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class ProductionOrderServiceImpl implements ProductionOrderService {
	static List<ProductionOrder> productionOrderList = new ArrayList<ProductionOrder>();
	private SerializationService serializator = new SerializationService("production_order");

	public ProductionOrderServiceImpl() {
		List<ProductionOrder> aux = serializator.obtenerLista();
		if(aux != null) {
			productionOrderList = aux;
		} else {
			serializator.grabarLista(productionOrderList);
		}
	}

	public synchronized List<ProductionOrder> getProductionOrderList() {
		List<ProductionOrder> list = new ArrayList<ProductionOrder>();
		for(ProductionOrder each: productionOrderList) {
			list.add(ProductionOrder.clone(each));
		}
		return list;
	}

	public synchronized ProductionOrder getProductionOrder(Integer id) {
		int size = productionOrderList.size();
		for(int i=0; i<size; i++) {
			ProductionOrder t = productionOrderList.get(i);
			if(t.getId().equals(id)) {
				return ProductionOrder.clone(t);
			}
		}
		return null;
	}

	public synchronized ProductionOrder saveProductionOrder(ProductionOrder productionOrder) {
		if(productionOrder.getId() == null) {
			productionOrder.setId(getNewId());
		}
		productionOrder = ProductionOrder.clone(productionOrder);
		productionOrderList.add(productionOrder);
		serializator.grabarLista(productionOrderList);
		return productionOrder;
	}

	public synchronized ProductionOrder updateProductionOrder(ProductionOrder productionOrder) {
		if(productionOrder.getId() == null) {
			throw new IllegalArgumentException("can't update a null-id ProductionOrder, save it first");
		}else {
			productionOrder = ProductionOrder.clone(productionOrder);
			int size = productionOrderList.size();
			for(int i=0; i<size; i++) {
				ProductionOrder t = productionOrderList.get(i);
				if(t.getId().equals(productionOrder.getId())){
					productionOrderList.set(i, productionOrder);
					serializator.grabarLista(productionOrderList);
					return productionOrder;
				}
			}
			throw new RuntimeException("ProductionOrder not found " + productionOrder.getId());
		}
	}

	public synchronized void deleteProductionOrder(ProductionOrder productionOrder) {
		if(productionOrder.getId() != null) {
			int size = productionOrderList.size();
			for(int i=0; i<size; i++) {
				ProductionOrder t = productionOrderList.get(i);
				if(t.getId().equals(productionOrder.getId())){
					productionOrderList.remove(i);
					serializator.grabarLista(productionOrderList);
					return;
				}
			}
		}
	}

	private synchronized Integer getNewId() {
		Integer lastId = 0;
		for(int i=0; i<productionOrderList.size(); i++) {
			ProductionOrder aux = productionOrderList.get(i);
			if(lastId < aux.getId()){
				lastId = aux.getId();
			}
		}
		return lastId + 1;
	}

	public List<ProductionOrder> getProductionOrderList(Integer idProductionPlan) {
		List<ProductionOrder> list = new ArrayList<ProductionOrder>();
		for(ProductionOrder each: productionOrderList) {
			if(each.getIdProductionPlan().equals(idProductionPlan)) {
				list.add(ProductionOrder.clone(each));
			}
		}
		return list;
	}

	public ProductionOrder updateProductionOrder(
			ProductionOrder productionOrder,
			List<ProductionOrderDetail> productionOrderDetailList) {
		ProductionOrderDetailService productionOrderDetailService = new ProductionOrderDetailServiceImpl();
		productionOrder = updateProductionOrder(productionOrder);
		for(ProductionOrderDetail productionOrderDetail:productionOrderDetailList) {
			productionOrderDetailService.updateProductionOrderDetail(productionOrderDetail);
		}
		return productionOrder;
	}

	public ProductionOrder saveProductionOrder(ProductionOrder productionOrder,
			List<ProductionOrderDetail> productionOrderDetailList) {
		ProductionOrderDetailService productionOrderDetailService = new ProductionOrderDetailServiceImpl();
		productionOrder = saveProductionOrder(productionOrder);// devuelve con id agregado
		for(ProductionOrderDetail productionOrderDetail:productionOrderDetailList) {
			productionOrderDetail.setIdProductionOrder(productionOrder.getId());
			productionOrderDetailService.saveProductionOrderDetail(productionOrderDetail);
		}
		return productionOrder;
	}

}
