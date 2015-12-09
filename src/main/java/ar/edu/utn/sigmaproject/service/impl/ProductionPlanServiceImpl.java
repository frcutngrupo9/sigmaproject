package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.OrderState;
import ar.edu.utn.sigmaproject.domain.OrderStateType;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanDetail;
import ar.edu.utn.sigmaproject.domain.ProductionPlanState;
import ar.edu.utn.sigmaproject.service.OrderStateService;
import ar.edu.utn.sigmaproject.service.OrderStateTypeService;
import ar.edu.utn.sigmaproject.service.ProductionPlanDetailService;
import ar.edu.utn.sigmaproject.service.ProductionPlanService;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class ProductionPlanServiceImpl  implements ProductionPlanService {
	
	static List<ProductionPlan> productionPlanList = new ArrayList<ProductionPlan>();
	private SerializationService serializator = new SerializationService("production_plan");
	
	public ProductionPlanServiceImpl() {
		List<ProductionPlan> aux = serializator.obtenerLista();
		if(aux != null) {
			productionPlanList = aux;
		} else {
			serializator.grabarLista(productionPlanList);
		}
	}
	
	public synchronized List<ProductionPlan> getProductionPlanList() {
		List<ProductionPlan> list = new ArrayList<ProductionPlan>();
		for(ProductionPlan productionPlan:productionPlanList) {
			list.add(ProductionPlan.clone(productionPlan));
		}
		return list;
	}
	
	public synchronized ProductionPlan getProductionPlan(Integer id) {
		int size = productionPlanList.size();
		for(int i = 0; i < size; i++) {
			ProductionPlan t = productionPlanList.get(i);
			if(t.getId().equals(id)) {
				return ProductionPlan.clone(t);
			}
		}
		return null;
	}
	
	public synchronized ProductionPlan saveProductionPlan(ProductionPlan productionPlan) {
		if(productionPlan.getId() == null) {
			productionPlan.setId(getNewId());
        }
		productionPlan = ProductionPlan.clone(productionPlan);
		productionPlanList.add(productionPlan);
		serializator.grabarLista(productionPlanList);
		return productionPlan;
	}
	
	public synchronized ProductionPlan updateProductionPlan(ProductionPlan productionPlan) {
	    if(productionPlan.getId() == null) {
	        throw new IllegalArgumentException("can't update a null-id production plan, save it first");
	    } else {
	        productionPlan = ProductionPlan.clone(productionPlan);
	        int size = productionPlanList.size();
	        for(int i = 0; i < size; i++) {
	            ProductionPlan aux = productionPlanList.get(i);
	            if(aux.getId().equals(productionPlan.getId())) {
	                productionPlanList.set(i, productionPlan);
	                serializator.grabarLista(productionPlanList);
	                return productionPlan;
	            }
	        }
	        throw new RuntimeException("Product Plan not found "+productionPlan.getId());
	    }
	}
	
	public synchronized void deleteProductionPlan(ProductionPlan productionPlan) {
		if(productionPlan.getId() != null) {
			// primero eliminamos los detalles y cambiamos los estados
			ProductionPlanDetailService productionPlanDetailService = new ProductionPlanDetailServiceImpl();
			List<ProductionPlanDetail> productionPlanDetailList = productionPlanDetailService.getProductionPlanDetailList();
			for(ProductionPlanDetail productionPlanDetail : productionPlanDetailList) {
				// debemos volver el estado de los pedidos a "iniciado"
				setNewOrderState("iniciado", productionPlanDetail.getIdOrder());// grabamos el estado del pedido
	    		// eliminamos el detalle
	    		productionPlanDetailService.deleteProductionPlanDetail(productionPlanDetail);
			}
			int size = productionPlanList.size();
			for(int i = 0; i < size; i++) {
				ProductionPlan t = productionPlanList.get(i);
				if(t.getId().equals(productionPlan.getId())) {
					productionPlanList.remove(i);
					serializator.grabarLista(productionPlanList);
					return;
				}
			}
		}
	}
	
	public synchronized Integer getNewId() {
		Integer lastId = 0;
		for(int i = 0; i < productionPlanList.size(); i++) {
			ProductionPlan aux = productionPlanList.get(i);
			if(lastId < aux.getId()) {
				lastId = aux.getId();
			}
		}
		return lastId + 1;
	}

	public synchronized ProductionPlan saveProductionPlan(ProductionPlan productionPlan,
			Integer productionPlanStateTypeId,
			List<ProductionPlanDetail> productionPlanDetailList) {
		productionPlan = saveProductionPlan(productionPlan);// se obtiene un plan con id agregado
		ProductionPlanDetailService productionPlanDetailService = new ProductionPlanDetailServiceImpl();
		OrderStateTypeService orderStateTypeService = new OrderStateTypeServiceImpl();
		OrderStateService orderStateService = new OrderStateServiceImpl();
		for(ProductionPlanDetail productionPlanDetail : productionPlanDetailList) {// agregamos el id del plan a todos los detalles y los guardamos
			productionPlanDetail.setIdProductionPlan(productionPlan.getId());
			productionPlanDetail = productionPlanDetailService.saveProductionPlanDetail(productionPlanDetail);
			// debemos cambiar el estado de todos los pedidos a "planificado"
        	OrderStateType order_state_type = orderStateTypeService.getOrderStateType("planificado");
        	OrderState aux = new OrderState(productionPlanDetail.getIdOrder(), order_state_type.getId(), new Date());
    		orderStateService.saveOrderState(aux);// grabamos el estado del pedido
		}
		ProductionPlanState aux = new ProductionPlanState(productionPlan.getId(), productionPlanStateTypeId, new Date());
		ProductionPlanStateService productionPlanStateService = new ProductionPlanStateServiceImpl();
		productionPlanStateService.saveProductionPlanState(aux);// grabamos el estado del plan
		return productionPlan;
	}

	public synchronized ProductionPlan updateProductionPlan(ProductionPlan productionPlan,
			Integer productionPlanStateTypeId,
			List<ProductionPlanDetail> productionPlanDetailList) {
		productionPlan = updateProductionPlan(productionPlan);
		ProductionPlanDetailService productionPlanDetailService = new ProductionPlanDetailServiceImpl();
		for(ProductionPlanDetail productionPlanDetail : productionPlanDetailList) {// hay que actualizar los detalles que existen y agregar los que no
			ProductionPlanDetail aux = productionPlanDetailService.getProductionPlanDetail(productionPlanDetail.getIdProductionPlan(), productionPlanDetail.getIdOrder());
			if(aux == null) {// no existe se agrega
				productionPlanDetail.setIdOrder(productionPlan.getId());// agregamos el id del plan
				productionPlanDetailService.saveProductionPlanDetail(productionPlanDetail);
				// debemos cambiar el estado a "planificado"
	        	setNewOrderState("planificado", productionPlanDetail.getIdOrder());// grabamos el estado del pedido
			} else {// existe, se actualiza
				productionPlanDetailService.updateProductionPlanDetail(productionPlanDetail);
			}
		}
		List<ProductionPlanDetail> serializedProductionPlanDetailList = productionPlanDetailService.getProductionPlanDetailList(productionPlan.getId());// buscamos todos los detalles serializados de ese plan de produccion
		for(ProductionPlanDetail serializedProductionPlanDetail:serializedProductionPlanDetailList) {
			Boolean is_in_list = false;
			for(ProductionPlanDetail productionPlanDetail:productionPlanDetailList) {
				if(serializedProductionPlanDetail.getIdProductionPlan().equals(productionPlanDetail.getIdProductionPlan())) {// si esta en la lista
					is_in_list = true;
					break;
				}
			}
			System.out.println("LLego aca");
			if(is_in_list == false) {// no esta en la lista que sera grabada, por lo tanto se debe eliminar
				// debemos volver el estado de los pedidos a "iniciado"
				System.out.println("a punto de eliminar detalle");
				setNewOrderState("iniciado", serializedProductionPlanDetail.getIdOrder());// grabamos el estado del pedido
	    		// eliminamos el detalle
	    		productionPlanDetailService.deleteProductionPlanDetail(serializedProductionPlanDetail);
			}
		}
		ProductionPlanStateService productionPlanStateService = new ProductionPlanStateServiceImpl();
		if(productionPlanStateTypeId != null) {
			int production_plan_id = productionPlan.getId();
			if(productionPlanStateTypeId != productionPlanStateService.getLastProductionPlanState(production_plan_id).getIdProductionPlanStateType()) {// si el estado seleccionado es diferente al ultimo guardado
				ProductionPlanState aux = new ProductionPlanState(production_plan_id, productionPlanStateTypeId, new Date());
				productionPlanStateService.saveProductionPlanState(aux);
			}
		}
		return productionPlan;
	}
	
	private void setNewOrderState(String stateName, Integer idOrder) {
		OrderStateTypeService orderStateTypeService = new OrderStateTypeServiceImpl();
		OrderStateService orderStateService = new OrderStateServiceImpl();
		OrderStateType order_state_type = orderStateTypeService.getOrderStateType(stateName);
    	OrderState aux = new OrderState(idOrder, order_state_type.getId(), new Date());
		orderStateService.saveOrderState(aux);// grabamos el estado del pedido
	}

}
