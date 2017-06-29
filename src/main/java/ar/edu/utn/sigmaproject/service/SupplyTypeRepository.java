package ar.edu.utn.sigmaproject.service;

import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.SupplyType;

@Repository
public interface SupplyTypeRepository extends SearchableRepository<SupplyType, Long> {

}