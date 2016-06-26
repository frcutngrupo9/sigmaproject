package ar.edu.utn.sigmaproject.service;

import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.RawMaterial;

@Repository
public interface RawMaterialRepository extends SearchableRepository<RawMaterial, Long> {

}
