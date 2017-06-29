package ar.edu.utn.sigmaproject.service;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.utn.sigmaproject.domain.OrderState;

public interface OrderStateRepository extends JpaRepository<OrderState, Long> {

}