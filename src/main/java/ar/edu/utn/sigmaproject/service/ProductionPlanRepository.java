package ar.edu.utn.sigmaproject.service;

import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.ProductionPlan;

@Repository
public interface ProductionPlanRepository extends SearchableRepository<ProductionPlan, Long> {

}
