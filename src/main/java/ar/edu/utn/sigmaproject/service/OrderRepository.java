package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderStateType;

/**
*
* @author gfzabarino
*/
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	
	List<Order> findByClient(Client client);
	List<Order> findByCurrentStateType(OrderStateType orderStateType);

}
