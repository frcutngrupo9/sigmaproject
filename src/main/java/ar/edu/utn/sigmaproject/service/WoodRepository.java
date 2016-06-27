package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.RawMaterialType;
import ar.edu.utn.sigmaproject.domain.Wood;

import org.springframework.stereotype.Repository;

@Repository
public interface WoodRepository extends SearchableRepository<Wood, Long> {

	List<Wood> findByRawMaterialType(RawMaterialType rawMaterialType);

}
