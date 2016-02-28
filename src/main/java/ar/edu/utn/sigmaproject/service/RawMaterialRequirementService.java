package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.RawMaterialRequirement;

public interface RawMaterialRequirementService {

	List<RawMaterialRequirement> getRawMaterialRequirementList();

	List<RawMaterialRequirement> getRawMaterialRequirementList(Integer idProductionPlan);

	RawMaterialRequirement getRawMaterialRequirement(Integer id);

	RawMaterialRequirement getRawMaterialRequirement(Integer idProductionPlan, Integer idRawMaterialType);

	RawMaterialRequirement saveRawMaterialRequirement(RawMaterialRequirement rawMaterialRequirement);

	RawMaterialRequirement updateRawMaterialRequirement(RawMaterialRequirement rawMaterialRequirement);

	void deleteRawMaterialRequirement(RawMaterialRequirement rawMaterialRequirement);

	void deleteAll(Integer idProductionPlan);

}
