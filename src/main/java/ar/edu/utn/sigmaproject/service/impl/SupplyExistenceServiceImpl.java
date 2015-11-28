package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.SupplyExistence;
import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.service.SupplyExistenceService;
import ar.edu.utn.sigmaproject.service.impl.SupplyTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class SupplyExistenceServiceImpl implements SupplyExistenceService {
	
	static List<SupplyExistence> supplyExistenceList = new ArrayList<SupplyExistence>();
    private SerializationService serializator = new SerializationService("supply_existence");
    
    public SupplyExistenceServiceImpl() {
        List<SupplyExistence> aux = serializator.obtenerLista();
        if(aux != null) {
            supplyExistenceList = aux;
        } else {
            serializator.grabarLista(supplyExistenceList);
        }
    }
    
    public synchronized List<SupplyExistence> getSupplyExistenceList() {
        List<SupplyExistence> list = new ArrayList<SupplyExistence>();
        for(SupplyExistence supplyExistence : supplyExistenceList) {
            list.add(SupplyExistence.clone(supplyExistence));
        }
        return list;
    }
    
    public synchronized SupplyExistence getSupplyExistence(Integer idSupplyType) {
        int size = supplyExistenceList.size();
        for(int i = 0; i < size; i++) {
        	SupplyExistence t = supplyExistenceList.get(i);
            if(t.getIdSupplyType().equals(idSupplyType)) {
                return SupplyExistence.clone(t);
            }
        }
        return null;
    }
    
    public synchronized SupplyExistence saveSupplyExistence(SupplyExistence supplyExistence) {
    	if(supplyExistence.getIdSupplyType() == null) {
    		throw new IllegalArgumentException("can't save a null-id SupplyExistence");
        } else {
        	SupplyType aux = (new SupplyTypeServiceImpl()).getSupplyType(supplyExistence.getIdSupplyType());
        	if(aux == null) {
        		throw new IllegalArgumentException("SupplyType referenced by SupplyExistence not found");
        	} else {
        		supplyExistence = SupplyExistence.clone(supplyExistence);
                supplyExistenceList.add(supplyExistence);
                serializator.grabarLista(supplyExistenceList);
        	}
        }
        return supplyExistence;
    }
    
    public synchronized SupplyExistence updateSupplyExistence(SupplyExistence supplyExistence) {
        if(supplyExistence.getIdSupplyType() == null) {
            throw new IllegalArgumentException("can't update a null-id SupplyExistence, save it first");
        } else {
            supplyExistence = SupplyExistence.clone(supplyExistence);
            int size = supplyExistenceList.size();
            for(int i = 0; i < size; i++) {
            	SupplyExistence t = supplyExistenceList.get(i);
                if(t.getIdSupplyType().equals(supplyExistence.getIdSupplyType())) {
                    supplyExistenceList.set(i, supplyExistence);
                    serializator.grabarLista(supplyExistenceList);
                    return supplyExistence;
                }
            }
            throw new RuntimeException("SupplyExistence not found " + supplyExistence.getIdSupplyType());
        }
    }
    
    public synchronized void deleteSupplyExistence(SupplyExistence supplyExistence) {
        if(supplyExistence.getIdSupplyType() != null) {
            int size = supplyExistenceList.size();
            for(int i = 0; i < size; i++) {
            	SupplyExistence t = supplyExistenceList.get(i);
                if(t.getIdSupplyType().equals(supplyExistence.getIdSupplyType())) {
                    supplyExistenceList.remove(i);
                    serializator.grabarLista(supplyExistenceList);
                    return;
                }
            }
        }
    }

}
