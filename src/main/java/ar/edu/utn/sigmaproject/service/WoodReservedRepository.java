package ar.edu.utn.sigmaproject.service;

import java.util.List;

import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.RawMaterialRequirement;
import ar.edu.utn.sigmaproject.domain.WoodReserved;

@Repository
public interface WoodReservedRepository extends SearchableRepository<WoodReserved, Long> {

	WoodReserved findFirstByRawMaterialRequirement(RawMaterialRequirement rawMaterialRequirement);

	List<WoodReserved> findByRawMaterialRequirement(RawMaterialRequirement rawMaterialRequirement);

}
