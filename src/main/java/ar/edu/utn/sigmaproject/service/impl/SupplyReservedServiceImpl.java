package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.SupplyExistence;
import ar.edu.utn.sigmaproject.domain.SupplyReserved;
import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.service.SupplyReservedService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class SupplyReservedServiceImpl implements SupplyReservedService {
	
	static List<SupplyReserved> supplyReservedList = new ArrayList<SupplyReserved>();
    private SerializationService serializator = new SerializationService("supply_reserved");
    
    public SupplyReservedServiceImpl() {
        List<SupplyReserved> aux = serializator.obtenerLista();
        if(aux != null) {
            supplyReservedList = aux;
        } else {
            serializator.grabarLista(supplyReservedList);
        }
    }
    
    public synchronized List<SupplyReserved> getSupplyReservedList() {
        List<SupplyReserved> list = new ArrayList<SupplyReserved>();
        for(SupplyReserved supplyReserved : supplyReservedList) {
            list.add(SupplyReserved.clone(supplyReserved));
        }
        return list;
    }
    
    public synchronized SupplyReserved getSupplyReserved(Integer idSupplyType) {
        int size = supplyReservedList.size();
        for(int i = 0; i < size; i++) {
        	SupplyReserved t = supplyReservedList.get(i);
            if(t.getIdSupplyType().equals(idSupplyType)) {
                return SupplyReserved.clone(t);
            }
        }
        return null;
    }
    
    public synchronized SupplyReserved saveSupplyReserved(SupplyReserved supplyReserved) {
    	if(supplyReserved.getIdSupplyType() == null) {
    		throw new IllegalArgumentException("can't save a null-id SupplyReserved");
        } else {
        	SupplyType aux = (new SupplyTypeServiceImpl()).getSupplyType(supplyReserved.getIdSupplyType());
        	if(aux == null) {
        		throw new IllegalArgumentException("SupplyType referenced by SupplyReserved not found");
        	} else {
        		supplyReserved = SupplyReserved.clone(supplyReserved);
                supplyReservedList.add(supplyReserved);
                serializator.grabarLista(supplyReservedList);
        	}
        }
        return supplyReserved;
    }
    
    public synchronized SupplyReserved updateSupplyReserved(SupplyReserved supplyReserved) {
        if(supplyReserved.getIdSupplyType() == null) {
            throw new IllegalArgumentException("can't update a null-id SupplyReserved, save it first");
        } else {
            supplyReserved = SupplyReserved.clone(supplyReserved);
            int size = supplyReservedList.size();
            for(int i = 0; i < size; i++) {
            	SupplyReserved t = supplyReservedList.get(i);
                if(t.getIdSupplyType().equals(supplyReserved.getIdSupplyType())) {
                    supplyReservedList.set(i, supplyReserved);
                    serializator.grabarLista(supplyReservedList);
                    return supplyReserved;
                }
            }
            throw new RuntimeException("SupplyReserved not found " + supplyReserved.getIdSupplyType());
        }
    }
    
    public synchronized void deleteSupplyReserved(SupplyReserved supplyReserved) {
        if(supplyReserved.getIdSupplyType() != null) {
            int size = supplyReservedList.size();
            for(int i = 0; i < size; i++) {
            	SupplyReserved t = supplyReservedList.get(i);
                if(t.getIdSupplyType().equals(supplyReserved.getIdSupplyType())) {
                    supplyReservedList.remove(i);
                    serializator.grabarLista(supplyReservedList);
                    return;
                }
            }
        }
    }
}
