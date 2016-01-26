package ar.edu.utn.sigmaproject.service;

import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.MeasureUnitType;

@Repository
public interface MeasureUnitTypeRepository extends SearchableRepository<MeasureUnitType, Long> {

	MeasureUnitType findByName(String name);
	
}
