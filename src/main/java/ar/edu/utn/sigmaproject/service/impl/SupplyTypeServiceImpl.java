package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.service.SupplyTypeService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class SupplyTypeServiceImpl implements SupplyTypeService {
    
    static List<SupplyType> supplyTypeList = new ArrayList<SupplyType>();
    private SerializationService serializator = new SerializationService("supply_type");
    
    public SupplyTypeServiceImpl() {
        List<SupplyType> aux = serializator.obtenerLista();
        if(aux != null) {
        	supplyTypeList = aux;
        } else {
            serializator.grabarLista(supplyTypeList);
        }
    }
    
    public synchronized List<SupplyType> getSupplyTypeList() {
        List<SupplyType> list = new ArrayList<SupplyType>();
        for(SupplyType supplyType: supplyTypeList) {
            list.add(SupplyType.clone(supplyType));
        }
        return list;
    }
    
    public synchronized SupplyType getSupplyType(Integer id) {
        int size = supplyTypeList.size();
        for(int i=0; i<size; i++) {
        	SupplyType t = supplyTypeList.get(i);
            if(t.getId().equals(id)) {
                return SupplyType.clone(t);
            }
        }
        return null;
    }
    
    public synchronized SupplyType saveSupplyType(SupplyType supplyType) {
        if(supplyType.getId() == null) {
        	supplyType.setId(getNewId());
        }
        supplyType = SupplyType.clone(supplyType);
        supplyTypeList.add(supplyType);
        serializator.grabarLista(supplyTypeList);
        return supplyType;
    }
    
    public synchronized SupplyType updateSupplyType(SupplyType supplyType) {
        if(supplyType.getId() == null) {
            throw new IllegalArgumentException("can't update a null-id supply type, save it first");
        }else {
        	supplyType = SupplyType.clone(supplyType);
            int size = supplyTypeList.size();
            for(int i=0; i<size; i++) {
            	SupplyType t = supplyTypeList.get(i);
                if(t.getId().equals(supplyType.getId())){
                	supplyTypeList.set(i, supplyType);
                    serializator.grabarLista(supplyTypeList);
                    return supplyType;
                }
            }
            throw new RuntimeException("Supply Type not found " + supplyType.getId());
        }
    }
    
    public synchronized void deleteSupplyType(SupplyType supplyType) {
        if(supplyType.getId() != null) {
            int size = supplyTypeList.size();
            for(int i=0; i<size; i++) {
            	SupplyType t = supplyTypeList.get(i);
                if(t.getId().equals(supplyType.getId())){
                	supplyTypeList.remove(i);
                    serializator.grabarLista(supplyTypeList);
                    return;
                }
            }
        }
    }
    
    private synchronized Integer getNewId() {
        Integer lastId = 0;
        for(int i=0; i<supplyTypeList.size(); i++) {
        	SupplyType aux = supplyTypeList.get(i);
            if(lastId < aux.getId()){
                lastId = aux.getId();
            }
        }
        return lastId + 1;
    }
}
