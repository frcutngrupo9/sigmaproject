package ar.edu.utn.sigmaproject.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.Client;
import ar.edu.utn.sigmaproject.domain.Order;

/**
*
* @author gfzabarino
*/
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	
	List<Order> findByClient(Client client);

}
