package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.SupplyRequirement;

public interface SupplyRequirementService {

	List<SupplyRequirement> getSupplyRequirementList();

	List<SupplyRequirement> getSupplyRequirementList(Integer idProductionPlan);

	SupplyRequirement getSupplyRequirement(Integer id);

	SupplyRequirement getSupplyRequirement(Integer idProductionPlan, Integer idSupplyType);

	SupplyRequirement saveSupplyRequirement(SupplyRequirement supplyRequirement);

	SupplyRequirement updateSupplyRequirement(SupplyRequirement supplyRequirement);

	void deleteSupplyRequirement(SupplyRequirement supplyRequirement);

	void deleteAll(Integer idProductionPlan);

}
