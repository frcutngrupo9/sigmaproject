package ar.edu.utn.sigmaproject.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.MeasureUnitType;

@Repository
public interface MeasureUnitTypeRepository extends JpaRepository<MeasureUnitType, Long> {

	public MeasureUnitType findFirstByName(String name);

}