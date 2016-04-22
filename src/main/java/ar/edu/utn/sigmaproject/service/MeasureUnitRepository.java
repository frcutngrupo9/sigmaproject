package ar.edu.utn.sigmaproject.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.MeasureUnit;

@Repository
public interface MeasureUnitRepository extends JpaRepository<MeasureUnit, Long> {

	MeasureUnit findByName(String name);
	
}
