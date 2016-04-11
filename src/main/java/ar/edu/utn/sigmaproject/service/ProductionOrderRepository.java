package ar.edu.utn.sigmaproject.service;

import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.ProductionOrder;

@Repository
public interface ProductionOrderRepository extends SearchableRepository<ProductionOrder, Long> {

}
