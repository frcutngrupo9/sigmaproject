package ar.edu.utn.sigmaproject.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.Item;
import ar.edu.utn.sigmaproject.domain.MaterialRequirement;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;

@Repository
public interface MaterialRequirementRepository extends JpaRepository<MaterialRequirement, Long> {

	public MaterialRequirement findByProductionPlanAndItem(ProductionPlan productionPlan, Item item);

}