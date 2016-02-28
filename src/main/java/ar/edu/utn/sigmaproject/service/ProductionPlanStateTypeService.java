package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;

public interface ProductionPlanStateTypeService {

	List<ProductionPlanStateType> getProductionPlanStateTypeList();

	ProductionPlanStateType getProductionPlanStateType(Integer id);

	ProductionPlanStateType getProductionPlanStateType(String name);

}
