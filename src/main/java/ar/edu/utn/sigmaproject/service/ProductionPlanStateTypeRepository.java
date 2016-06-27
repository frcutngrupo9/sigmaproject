package ar.edu.utn.sigmaproject.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;

@Repository
public interface ProductionPlanStateTypeRepository extends JpaRepository<ProductionPlanStateType, Long> {

	ProductionPlanStateType findFirstByName(String string);	
	
}
