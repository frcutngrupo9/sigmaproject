package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.service.ProductionPlanService;
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
		for(ProductionPlan productionPlan:productionPlanList){
			list.add(ProductionPlan.clone(productionPlan));
		}
		return list;
	}
	
	public synchronized ProductionPlan getProductionPlan(Integer id) {
		int size = productionPlanList.size();
		for(int i=0;i<size;i++){
			ProductionPlan t = productionPlanList.get(i);
			if(t.getId().equals(id)){
				return ProductionPlan.clone(t);
			}
		}
		return null;
	}
	
	public synchronized ProductionPlan saveProductionPlan(ProductionPlan productionPlan) {
		productionPlan = ProductionPlan.clone(productionPlan);
		productionPlanList.add(productionPlan);
		serializator.grabarLista(productionPlanList);
		return productionPlan;
	}
	
	public synchronized ProductionPlan updateProductionPlan(ProductionPlan productionPlan) {
		if(productionPlan.getId()!=null && productionPlan.getId()==getNewId())
		{
			productionPlan = saveProductionPlan(productionPlan);
			return productionPlan;
		}
		else {
			if(productionPlan.getId()==null){
				throw new IllegalArgumentException("can't update a null-id production plan, save it first");
			}else{
				productionPlan = ProductionPlan.clone(productionPlan);
				int size = productionPlanList.size();
				for(int i=0;i<size;i++){
					ProductionPlan aux = productionPlanList.get(i);
					if(aux.getId().equals(productionPlan.getId())){
						productionPlanList.set(i, productionPlan);
						return productionPlan;
					}
				}
				throw new RuntimeException("Product Plan not found "+productionPlan.getId());
			}
		}
	}
	
	public synchronized void deleteProductionPlan(ProductionPlan productionPlan) {
		if(productionPlan.getId()!=null){
			int size = productionPlanList.size();
			for(int i=0;i<size;i++){
				ProductionPlan t = productionPlanList.get(i);
				if(t.getId().equals(productionPlan.getId())){
					productionPlanList.remove(i);
					serializator.grabarLista(productionPlanList);
					return;
				}
			}
		}
	}
	
	public synchronized Integer getNewId() {
		Integer lastId = 0;
		for(int i=0;i<productionPlanList.size();i++){
			ProductionPlan aux = productionPlanList.get(i);
			if(lastId < aux.getId()){
				lastId = aux.getId();
			}
		}
		return lastId + 1;
	}

}
