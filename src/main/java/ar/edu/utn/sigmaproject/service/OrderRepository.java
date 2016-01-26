package ar.edu.utn.sigmaproject.service;

import java.util.List;

import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderStateType;

/**
*
* @author gfzabarino
*/
@Repository
public interface OrderRepository extends SearchableRepository<Order, Long> {
	
	List<Order> findByCurrentStateType(OrderStateType orderStateType);
	
}
