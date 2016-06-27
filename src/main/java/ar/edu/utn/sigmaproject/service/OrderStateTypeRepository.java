package ar.edu.utn.sigmaproject.service;

import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.OrderStateType;

@Repository
public interface OrderStateTypeRepository extends SearchableRepository<OrderStateType, Long> {

	OrderStateType findFirstByName(String string);
	
}
