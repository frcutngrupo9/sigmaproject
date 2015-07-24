package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.MeasureUnit;
import ar.edu.utn.sigmaproject.service.MeasureUnitService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class MeasureUnitServiceImpl implements MeasureUnitService {
	
	static List<MeasureUnit> measureUnitList = new ArrayList<MeasureUnit>();
	private SerializationService serializator = new SerializationService("measure_unit");
	
	public MeasureUnitServiceImpl() {
		List<MeasureUnit> aux = serializator.obtenerLista();
		if(aux != null) {
			measureUnitList = aux;
		} else {
			serializator.grabarLista(measureUnitList);
		}
	}
	
	public synchronized List<MeasureUnit> getMeasureUnitList() {
		List<MeasureUnit> list = new ArrayList<MeasureUnit>();
		for(MeasureUnit aux:measureUnitList){
			list.add(MeasureUnit.clone(aux));
		}
		return list;
	}
	
	public synchronized MeasureUnit getMeasureUnit(Integer id) {
		int size = measureUnitList.size();
		for(int i=0;i<size;i++){
			MeasureUnit aux = measureUnitList.get(i);
			if(aux.getId().equals(id)){
				return MeasureUnit.clone(aux);
			}
		}
		return null;
	}
	
	public synchronized MeasureUnit saveMeasureUnit(MeasureUnit aux) {
		aux = MeasureUnit.clone(aux);
		measureUnitList.add(aux);
		serializator.grabarLista(measureUnitList);
		return aux;
	}
	
	public synchronized MeasureUnit updateMeasureUnit(MeasureUnit aux) {
		if(aux.getId()!=null && aux.getId()==getNewId())
		{
			aux = saveMeasureUnit(aux);
			return aux;
		}
		else {
			if(aux.getId()==null){
				throw new IllegalArgumentException("can't update a null-id MeasureUnit, save it first");
			}else{
				aux = MeasureUnit.clone(aux);
				int size = measureUnitList.size();
				for(int i=0;i<size;i++){
					MeasureUnit t = measureUnitList.get(i);
					if(t.getId().equals(aux.getId())){
						measureUnitList.set(i, aux);
						return aux;
					}
				}
				throw new RuntimeException("Product not found "+aux.getId());
			}
		}
	}
	
	public synchronized void deleteMeasureUnit(MeasureUnit aux) {
		if(aux.getId()!=null){
			int size = measureUnitList.size();
			for(int i=0;i<size;i++){
				MeasureUnit t = measureUnitList.get(i);
				if(t.getId().equals(aux.getId())){
					measureUnitList.remove(i);
					serializator.grabarLista(measureUnitList);
					return;
				}
			}
		}
	}
	
	public synchronized Integer getNewId() {
		Integer lastId = 0;
		for(int i=0;i<measureUnitList.size();i++){
			MeasureUnit aux = measureUnitList.get(i);
			if(lastId < aux.getId()){
				lastId = aux.getId();
			}
		}
		return lastId + 1;
	}
}
