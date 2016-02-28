package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
			productionPlanDetailService.deleteAll(productionPlan.getId());

			int size = productionPlanList.size();
			for(int i = 0; i < size; i++) {
				ProductionPlan t = productionPlanList.get(i);
				if(t.getId().equals(productionPlan.getId())) {
					productionPlanList.remove(i);// eliminamos el plan
					serializator.grabarLista(productionPlanList);
					return;
				}
			}
			ProductionPlanStateService productionPlanStateService = new ProductionPlanStateServiceImpl();
			// eliminamos todos los estados del plan
			productionPlanStateService.deleteAll(productionPlan.getId());
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
			orderStateService.setNewOrderState("planificado", productionPlanDetail.getIdOrder());
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
		OrderStateService orderStateService = new OrderStateServiceImpl();
		List<ProductionPlanDetail> serializedProductionPlanDetailList = productionPlanDetailService.getProductionPlanDetailList(productionPlan.getId());// buscamos todos los detalles del plan de produccion
		for(ProductionPlanDetail productionPlanDetail : productionPlanDetailList) {// hay que actualizar los detalles que existen y agregar los que no
			if(productionPlanDetail.getIdProductionPlan() == null) {
				productionPlanDetail.setIdProductionPlan(productionPlan.getId());// agregamos el id del plan a los detalles en caso de que se haya agregado un detalle
			}
			ProductionPlanDetail aux = productionPlanDetailService.getProductionPlanDetail(productionPlanDetail.getIdProductionPlan(), productionPlanDetail.getIdOrder());
			if(aux == null) {// no existe se agrega
				productionPlanDetailService.saveProductionPlanDetail(productionPlanDetail);
				// debemos cambiar el estado del pedido a "planificado"
				orderStateService.setNewOrderState("planificado", productionPlanDetail.getIdOrder());// grabamos el estado del pedido
			} else {// existe, se actualiza (es irrelevante actualizarlo mientras no posea mas atributos que los dos id de referencia al plan y al pedido
				productionPlanDetailService.updateProductionPlanDetail(productionPlanDetail);
			}
		}
		// eliminamos los detalles que ya no estan en la lista
		for(ProductionPlanDetail serializedProductionPlanDetail:serializedProductionPlanDetailList) {
			Boolean is_in_list = false;
			for(ProductionPlanDetail productionPlanDetail:productionPlanDetailList) {
				if(serializedProductionPlanDetail.getIdOrder().equals(productionPlanDetail.getIdOrder())) {// si esta en la lista
					is_in_list = true;
					break;
				}
			}
			if(is_in_list == false) {// no esta en la lista que sera grabada, por lo tanto se debe eliminar
				// debemos volver el estado de los pedidos a "iniciado"
				orderStateService.setNewOrderState("iniciado", serializedProductionPlanDetail.getIdOrder());// grabamos el estado del pedido
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

}
