package ar.edu.utn.sigmaproject.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.MaterialsOrderDetail;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;

@Repository
public interface MaterialsOrderDetailRepository extends JpaRepository<MaterialsOrderDetail, Long> {

	List<MaterialsOrderDetail> findByProductionPlan(ProductionPlan productionPlan);

}
