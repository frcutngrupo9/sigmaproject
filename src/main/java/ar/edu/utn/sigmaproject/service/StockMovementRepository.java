package ar.edu.utn.sigmaproject.service;

import ar.edu.utn.sigmaproject.domain.Item;
import ar.edu.utn.sigmaproject.domain.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementRepository<T extends Item> extends JpaRepository<StockMovement<T>, Long> {



}
