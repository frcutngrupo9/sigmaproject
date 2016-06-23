package ar.edu.utn.sigmaproject.util;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.MeasureUnit;
import ar.edu.utn.sigmaproject.domain.MeasureUnitType;
import ar.edu.utn.sigmaproject.service.MeasureUnitRepository;
import ar.edu.utn.sigmaproject.service.MeasureUnitTypeRepository;

public class RepositoryHelper {
	
	public void generateMeasureUnitTypeList(MeasureUnitRepository mUR, MeasureUnitTypeRepository mUTR) {
		List<MeasureUnitType> measureUnitTypeList = new ArrayList<MeasureUnitType>();
		measureUnitTypeList.add(new MeasureUnitType("Longitud"));
		measureUnitTypeList.add(new MeasureUnitType("Tiempo"));
		measureUnitTypeList.add(new MeasureUnitType("Masa"));
		measureUnitTypeList.add(new MeasureUnitType("Cantidad"));
		for(MeasureUnitType aux : measureUnitTypeList) {
			mUTR.save(aux);
		}
		// asumimos que como no estan creados los tipos de UM tampoco lo estan los UM
		// por lo tanto creamos los UM
		MeasureUnitType mUTLongitud = mUTR.findByName("Longitud");
		MeasureUnitType mUTTiempo = mUTR.findByName("Tiempo");
		MeasureUnitType mUTMasa = mUTR.findByName("Masa");
		MeasureUnitType mUTCantidad = mUTR.findByName("Cantidad");
		List<MeasureUnit> measureUnitList = new ArrayList<MeasureUnit>();
		measureUnitList.add(new MeasureUnit("Metros", "M", mUTLongitud));
		measureUnitList.add(new MeasureUnit("Centimetros", "Cm", mUTLongitud));
		measureUnitList.add(new MeasureUnit("Milimetros", "Mm", mUTLongitud));
		measureUnitList.add(new MeasureUnit("Pulgadas", "Pul", mUTLongitud));
		measureUnitList.add(new MeasureUnit("Minutos", "Min", mUTTiempo));
		measureUnitList.add(new MeasureUnit("Horas", "Hr", mUTTiempo));
		measureUnitList.add(new MeasureUnit("Dias", "D", mUTTiempo));
		measureUnitList.add(new MeasureUnit("Kilogramos", "Kg", mUTMasa));
		measureUnitList.add(new MeasureUnit("Gramos", "Gr", mUTMasa));
		measureUnitList.add(new MeasureUnit("Litros", "L", mUTMasa));
		measureUnitList.add(new MeasureUnit("Mililitros", "Ml", mUTMasa));
		measureUnitList.add(new MeasureUnit("Unidad", "Unid", mUTCantidad));
		for(MeasureUnit aux : measureUnitList) {
			mUR.save(aux);
		}
	}

}
