package ar.edu.utn.sigmaproject.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.MaterialsOrder;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;

@Repository
public interface MaterialsOrderRepository extends JpaRepository<MaterialsOrder, Long> {

	public List<MaterialsOrder> findByProductionPlan(ProductionPlan productionPlan);

}