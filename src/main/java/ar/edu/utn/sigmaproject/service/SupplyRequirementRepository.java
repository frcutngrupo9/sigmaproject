package ar.edu.utn.sigmaproject.service;

import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.SupplyRequirement;
import ar.edu.utn.sigmaproject.domain.SupplyType;

/**
*
* @author danielrhelmfelt
*/
@Repository
public interface SupplyRequirementRepository extends SearchableRepository<SupplyRequirement, Long> {
	
	SupplyRequirement findByProductionPlanAndSupplyType(ProductionPlan productionPlan, SupplyType supplyType);

}
