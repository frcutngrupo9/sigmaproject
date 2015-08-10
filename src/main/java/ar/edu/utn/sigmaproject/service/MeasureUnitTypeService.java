package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.MeasureUnitType;

public interface MeasureUnitTypeService {
    
    List<MeasureUnitType> getMeasureUnitTypeList();
    
    MeasureUnitType getMeasureUnitType(Integer idMeasureUnitType);
    
    MeasureUnitType getMeasureUnitType(String nameMeasureUnitType);

    MeasureUnitType saveMeasureUnitType(MeasureUnitType measureUnitType);

    MeasureUnitType updateMeasureUnitType(MeasureUnitType measureUnitType);

    void deleteMeasureUnitType(MeasureUnitType measureUnitType);

}