package ar.edu.utn.sigmaproject.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.MaterialRequirement;
import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;

@Repository
public interface ProductionPlanRepository extends JpaRepository<ProductionPlan, Long> {

	public ProductionPlan findByMaterialRequirements(MaterialRequirement materialRequirement);

	public List<ProductionPlan> findByCurrentStateType(ProductionPlanStateType productionPlanStateType);

	public ProductionPlan findByPlanDetailsOrder(Order order);

}