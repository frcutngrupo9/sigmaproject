package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.ProductionPlanState;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateService;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class ProductionPlanStateServiceImpl implements ProductionPlanStateService {

	static List<ProductionPlanState> productionPlanStateList = new ArrayList<ProductionPlanState>();
	private SerializationService serializator = new SerializationService("production_plan_state");

	public ProductionPlanStateServiceImpl() {
		@SuppressWarnings("unchecked")
		List<ProductionPlanState> aux = serializator.obtenerLista();
		if(aux != null) {
			productionPlanStateList = aux;
		} else {
			serializator.grabarLista(productionPlanStateList);
		}
	}

	public synchronized List<ProductionPlanState> getProductionPlanStateList() {
		List<ProductionPlanState> list = new ArrayList<ProductionPlanState>();
		for(ProductionPlanState productionPlanState:productionPlanStateList) {
			list.add(ProductionPlanState.clone(productionPlanState));
		}
		return list;
	}

	public synchronized List<ProductionPlanState> getProductionPlanStateList(Integer idProductionPlan) {
		List<ProductionPlanState> list = new ArrayList<ProductionPlanState>();
		for(ProductionPlanState productionPlanState:productionPlanStateList) {
			if(productionPlanState.getIdProductionPlan().equals(idProductionPlan)) {
				list.add(ProductionPlanState.clone(productionPlanState));
			}
		}
		return list;
	}

	public synchronized ProductionPlanState getProductionPlanState(Integer idProductionPlan, Integer idProductionPlanStateType) {
		int size = productionPlanStateList.size();
		for(int i = 0; i < size; i++) {
			ProductionPlanState t = productionPlanStateList.get(i);
			if(t.getIdProductionPlan().equals(idProductionPlan) && t.getIdProductionPlanStateType().equals(idProductionPlanStateType)) {
				return ProductionPlanState.clone(t);
			}
		}
		return null;
	}

	public synchronized ProductionPlanState getLastProductionPlanState(Integer idProductionPlan) {
		List<ProductionPlanState> list = getProductionPlanStateList(idProductionPlan);
		ProductionPlanState aux = null;
		for(ProductionPlanState productionPlanState:list) {
			if(aux != null) {
				if(aux.getDate().before(productionPlanState.getDate())) {
					aux = productionPlanState;
				}
			} else {
				aux = productionPlanState;
			}
		}
		return aux;
	}

	public synchronized ProductionPlanState saveProductionPlanState(ProductionPlanState productionPlanState) {
		productionPlanState = ProductionPlanState.clone(productionPlanState);
		productionPlanStateList.add(productionPlanState);
		serializator.grabarLista(productionPlanStateList);
		return productionPlanState;
	}

	public synchronized ProductionPlanState updateProductionPlanState(ProductionPlanState productionPlanState) {
		if(productionPlanState.getIdProductionPlan() == null || productionPlanState.getIdProductionPlanStateType() == null) {
			throw new IllegalArgumentException("can't update a null-id ProductionPlanState, save it first");
		}else {
			productionPlanState = ProductionPlanState.clone(productionPlanState);
			int size = productionPlanStateList.size();
			for(int i = 0; i < size; i++) {
				ProductionPlanState t = productionPlanStateList.get(i);
				if(t.getIdProductionPlan().equals(productionPlanState.getIdProductionPlan()) && t.getIdProductionPlanStateType().equals(productionPlanState.getIdProductionPlanStateType())) {
					productionPlanStateList.set(i, productionPlanState);
					serializator.grabarLista(productionPlanStateList);
					return productionPlanState;
				}
			}
			throw new RuntimeException("ProductionPlanState not found " + productionPlanState.getIdProductionPlan()+" "+productionPlanState.getIdProductionPlanStateType());
		}
	}

	public synchronized void deleteProductionPlanState(ProductionPlanState productionPlanState) {
		if(productionPlanState.getIdProductionPlan()!=null && productionPlanState.getIdProductionPlanStateType()!=null) {
			int size = productionPlanStateList.size();
			for(int i = 0; i < size; i++) {
				ProductionPlanState t = productionPlanStateList.get(i);
				if(t.getIdProductionPlan().equals(productionPlanState.getIdProductionPlan()) && t.getIdProductionPlanStateType().equals(productionPlanState.getIdProductionPlanStateType())) {
					productionPlanStateList.remove(i);
					serializator.grabarLista(productionPlanStateList);
					return;
				}
			}
		}
	}

	public synchronized void deleteAll(Integer idProductionPlan) {
		List<ProductionPlanState> listDelete = getProductionPlanStateList(idProductionPlan);
		for(ProductionPlanState delete:listDelete) {
			deleteProductionPlanState(delete);
		}
	}

	public void setNewProductionPlanState(String stateName, Integer idProductionPlan) {// crea un nuevo estado en base al nombre pasado por parametro
		ProductionPlanStateTypeService productionPlanStateTypeService = new ProductionPlanStateTypeServiceImpl();
		ProductionPlanStateType production_plan_state_type = productionPlanStateTypeService.getProductionPlanStateType(stateName);
		ProductionPlanState aux = new ProductionPlanState(idProductionPlan, production_plan_state_type.getId(), new Date());
		saveProductionPlanState(aux);
	}
}
