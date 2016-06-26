package ar.edu.utn.sigmaproject.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.ProductionPlanDetail;

@Repository
public interface ProductionPlanDetailRepository extends JpaRepository<ProductionPlanDetail, Long> {

}
