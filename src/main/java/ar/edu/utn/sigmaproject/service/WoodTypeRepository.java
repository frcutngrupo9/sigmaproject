package ar.edu.utn.sigmaproject.service;

import ar.edu.utn.sigmaproject.domain.WoodType;

import org.springframework.stereotype.Repository;

@Repository
public interface WoodTypeRepository extends SearchableRepository<WoodType, Long> {

	WoodType findFirstByName(String string);

}
