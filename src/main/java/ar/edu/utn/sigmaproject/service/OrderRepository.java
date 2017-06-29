package ar.edu.utn.sigmaproject.service;

import java.util.List;

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

	public List<Order> findByCurrentStateType(OrderStateType currentStateType);

}