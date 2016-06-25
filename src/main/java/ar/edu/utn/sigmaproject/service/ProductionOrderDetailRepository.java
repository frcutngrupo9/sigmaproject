package ar.edu.utn.sigmaproject.service;

import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;

/**
*
* @author danielrhelmfelt
*/
@Repository
public interface ProductionOrderDetailRepository extends SearchableRepository<ProductionOrderDetail, Long> {

}
