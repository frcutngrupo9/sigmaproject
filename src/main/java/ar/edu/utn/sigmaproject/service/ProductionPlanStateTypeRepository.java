package ar.edu.utn.sigmaproject.service;

import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;

@Repository
public interface ProductionPlanStateTypeRepository extends SearchableRepository<ProductionPlanStateType, Long> {

	ProductionPlanStateType findByName(String name);	
	
}
