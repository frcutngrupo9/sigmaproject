package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.MeasureUnitType;
import ar.edu.utn.sigmaproject.service.MeasureUnitTypeService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class MeasureUnitTypeServiceImpl implements MeasureUnitTypeService {
    
    static List<MeasureUnitType> measureUnitTypeList = new ArrayList<MeasureUnitType>();
    private SerializationService serializator = new SerializationService("measure_unit_type");
    static int measureUnitTypeId = 1;
    static {
        measureUnitTypeList.add(new MeasureUnitType(measureUnitTypeId++,"Longitud"));
        measureUnitTypeList.add(new MeasureUnitType(measureUnitTypeId++,"Tiempo"));
        measureUnitTypeList.add(new MeasureUnitType(measureUnitTypeId++,"Masa"));
        measureUnitTypeList.add(new MeasureUnitType(measureUnitTypeId++,"Cantidad"));
    }
    
    public MeasureUnitTypeServiceImpl() {
        List<MeasureUnitType> aux = serializator.obtenerLista();
        if(aux != null) {
            measureUnitTypeList = aux;
        } else {
            serializator.grabarLista(measureUnitTypeList);
        }
    }
    
    public synchronized List<MeasureUnitType> getMeasureUnitTypeList() {
        List<MeasureUnitType> list = new ArrayList<MeasureUnitType>();
        for(MeasureUnitType aux:measureUnitTypeList) {
            list.add(MeasureUnitType.clone(aux));
        }
        return list;
    }
    
    public synchronized MeasureUnitType getMeasureUnitType(Integer id) {
        for(MeasureUnitType aux:measureUnitTypeList) {
            if(aux.getId().compareTo(id) == 0) {
                return MeasureUnitType.clone(aux);
            }
        }
        return null;
    }
    
    public synchronized MeasureUnitType getMeasureUnitType(String name) {
        for(MeasureUnitType aux:measureUnitTypeList) {
            if(aux.getName().compareToIgnoreCase(name) == 0) {
                return MeasureUnitType.clone(aux);
            }
        }
        return null;
    }
    
    public synchronized MeasureUnitType saveMeasureUnitType(MeasureUnitType aux) {
        aux = MeasureUnitType.clone(aux);
        measureUnitTypeList.add(aux);
        serializator.grabarLista(measureUnitTypeList);
        return aux;
    }
    
    public synchronized MeasureUnitType updateMeasureUnitType(MeasureUnitType aux) {
        if(aux.getId()!=null && aux.getId()==getNewId()) {
            aux = saveMeasureUnitType(aux);
            return aux;
        }
        else {
            if(aux.getId()==null) {
                throw new IllegalArgumentException("can't update a null-id MeasureUnit, save it first");
            }else {
                aux = MeasureUnitType.clone(aux);
                int size = measureUnitTypeList.size();
                for(int i=0;i<size;i++) {
                    MeasureUnitType t = measureUnitTypeList.get(i);
                    if(t.getId().equals(aux.getId())) {
                        measureUnitTypeList.set(i, aux);
                        return aux;
                    }
                }
                throw new RuntimeException("Product not found "+aux.getId());
            }
        }
    }
    
    public synchronized void deleteMeasureUnitType(MeasureUnitType aux) {
        if(aux.getId()!=null) {
            int size = measureUnitTypeList.size();
            for(int i=0;i<size;i++) {
                MeasureUnitType t = measureUnitTypeList.get(i);
                if(t.getId().equals(aux.getId())) {
                    measureUnitTypeList.remove(i);
                    serializator.grabarLista(measureUnitTypeList);
                    return;
                }
            }
        }
    }
    
    public synchronized Integer getNewId() {
        Integer lastId = 0;
        for(int i=0;i<measureUnitTypeList.size();i++){
            MeasureUnitType aux = measureUnitTypeList.get(i);
            if(lastId < aux.getId()) {
                lastId = aux.getId();
            }
        }
        return lastId + 1;
    }
   
}