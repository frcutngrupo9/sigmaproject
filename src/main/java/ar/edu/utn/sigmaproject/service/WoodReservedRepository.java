package ar.edu.utn.sigmaproject.service;

import java.util.List;

import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.RawMaterialRequirement;
import ar.edu.utn.sigmaproject.domain.Wood;
import ar.edu.utn.sigmaproject.domain.WoodReserved;

@Repository
public interface WoodReservedRepository extends SearchableRepository<WoodReserved, Long> {

	WoodReserved findByRawMaterialRequirement(RawMaterialRequirement rawMaterialRequirement);

	List<WoodReserved> findByRawMaterialRequirementWood(Wood wood);

}
