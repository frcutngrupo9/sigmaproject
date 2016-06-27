package ar.edu.utn.sigmaproject.service;

import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.SupplyRequirement;

/**
*
* @author danielrhelmfelt
*/
@Repository
public interface SupplyRequirementRepository extends SearchableRepository<SupplyRequirement, Long> {

}
