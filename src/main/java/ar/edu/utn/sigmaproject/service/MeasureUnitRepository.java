package ar.edu.utn.sigmaproject.service;

import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.MeasureUnit;

@Repository
public interface MeasureUnitRepository extends SearchableRepository<MeasureUnit, Long> {

	MeasureUnit findByName(String name);
	
}
