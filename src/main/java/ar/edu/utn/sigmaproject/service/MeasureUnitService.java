package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.MeasureUnit;

public interface MeasureUnitService {
	
	List<MeasureUnit> getMeasureUnitList();
	
	List<MeasureUnit> getMeasureUnitList(Integer idMeasureUnitType);
	
	MeasureUnit getMeasureUnit(Integer idMeasureUnit);
	
	MeasureUnit getMeasureUnit(String nameMeasureUnit);

	MeasureUnit saveMeasureUnit(MeasureUnit measureUnit);

	MeasureUnit updateMeasureUnit(MeasureUnit measureUnit);

	void deleteMeasureUnit(MeasureUnit measureUnit);

}