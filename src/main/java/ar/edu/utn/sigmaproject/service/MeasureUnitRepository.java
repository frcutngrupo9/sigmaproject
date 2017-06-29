package ar.edu.utn.sigmaproject.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.MeasureUnit;
import ar.edu.utn.sigmaproject.domain.MeasureUnitType;

@Repository
public interface MeasureUnitRepository extends JpaRepository<MeasureUnit, Long> {

	public List<MeasureUnit> findByType(MeasureUnitType type);

	public MeasureUnit findFirstByName(String string);

}