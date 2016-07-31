package ar.edu.utn.sigmaproject.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.ProductionOrderStateType;

@Repository
public interface ProductionOrderStateTypeRepository extends JpaRepository<ProductionOrderStateType, Long> {

	ProductionOrderStateType findFirstByName(String string);

}