package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeService;

public class ProductionPlanStateTypeServiceImpl implements ProductionPlanStateTypeService {

	static int productionPlanStateTypeId = 1;
	static List<ProductionPlanStateType> productionPlanStateTypeList = new ArrayList<ProductionPlanStateType>();  
	static{
		productionPlanStateTypeList.add(new ProductionPlanStateType(productionPlanStateTypeId++,"iniciado", ""));
		productionPlanStateTypeList.add(new ProductionPlanStateType(productionPlanStateTypeId++,"cancelado", ""));
		productionPlanStateTypeList.add(new ProductionPlanStateType(productionPlanStateTypeId++,"abastecido", ""));
		productionPlanStateTypeList.add(new ProductionPlanStateType(productionPlanStateTypeId++,"lanzado", ""));
		productionPlanStateTypeList.add(new ProductionPlanStateType(productionPlanStateTypeId++,"en produccion", ""));
		productionPlanStateTypeList.add(new ProductionPlanStateType(productionPlanStateTypeId++,"finalizado", ""));
	}

	public synchronized List<ProductionPlanStateType> getProductionPlanStateTypeList() {
		List<ProductionPlanStateType> list = new ArrayList<ProductionPlanStateType>();
		for(ProductionPlanStateType orderStateType:productionPlanStateTypeList) {
			list.add(ProductionPlanStateType.clone(orderStateType));
		}
		return list;
	}

	public synchronized ProductionPlanStateType getProductionPlanStateType(Integer id) {
		int size = productionPlanStateTypeList.size();
		for(int i = 0; i < size; i++){
			ProductionPlanStateType t = productionPlanStateTypeList.get(i);
			if(t.getId().equals(id)) {
				return ProductionPlanStateType.clone(t);
			}
		}
		return null;
	}

	public synchronized ProductionPlanStateType getProductionPlanStateType(String name) {
		int size = productionPlanStateTypeList.size();
		for(int i = 0; i < size; i++){
			ProductionPlanStateType t = productionPlanStateTypeList.get(i);
			if(t.getName().equalsIgnoreCase(name)) {
				return ProductionPlanStateType.clone(t);
			}
		}
		return null;
	}
}
