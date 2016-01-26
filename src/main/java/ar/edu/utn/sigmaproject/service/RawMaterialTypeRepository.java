package ar.edu.utn.sigmaproject.service;

import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.RawMaterialType;

@Repository
public interface RawMaterialTypeRepository extends SearchableRepository<RawMaterialType, Long> {

}
