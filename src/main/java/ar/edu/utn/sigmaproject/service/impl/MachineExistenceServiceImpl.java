package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.MachineExistence;
import ar.edu.utn.sigmaproject.service.MachineExistenceService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class MachineExistenceServiceImpl implements MachineExistenceService {
	static List<MachineExistence> machineExistenceList = new ArrayList<MachineExistence>();
    private SerializationService serializator = new SerializationService("machine_existence");
    
    public MachineExistenceServiceImpl() {
        List<MachineExistence> aux = serializator.obtenerLista();
        if(aux != null) {
        	machineExistenceList = aux;
        } else {
            serializator.grabarLista(machineExistenceList);
        }
    }
    
    public synchronized List<MachineExistence> getMachineExistenceList() {
        List<MachineExistence> list = new ArrayList<MachineExistence>();
        for(MachineExistence each: machineExistenceList) {
            list.add(MachineExistence.clone(each));
        }
        return list;
    }
    
    public synchronized MachineExistence getMachineExistence(Integer id) {
        int size = machineExistenceList.size();
        for(int i=0; i<size; i++) {
        	MachineExistence t = machineExistenceList.get(i);
            if(t.getId().equals(id)) {
                return MachineExistence.clone(t);
            }
        }
        return null;
    }
    
    public synchronized MachineExistence saveMachineExistence(MachineExistence machineExistence) {
        if(machineExistence.getId() == null) {
        	machineExistence.setId(getNewId());
        }
        machineExistence = MachineExistence.clone(machineExistence);
        machineExistenceList.add(machineExistence);
        serializator.grabarLista(machineExistenceList);
        return machineExistence;
    }
    
    public synchronized MachineExistence updateMachineExistence(MachineExistence machineExistence) {
        if(machineExistence.getId() == null) {
            throw new IllegalArgumentException("can't update a null-id MachineExistence, save it first");
        }else {
        	machineExistence = MachineExistence.clone(machineExistence);
            int size = machineExistenceList.size();
            for(int i=0; i<size; i++) {
            	MachineExistence t = machineExistenceList.get(i);
                if(t.getId().equals(machineExistence.getId())){
                	machineExistenceList.set(i, machineExistence);
                    serializator.grabarLista(machineExistenceList);
                    return machineExistence;
                }
            }
            throw new RuntimeException("MachineExistence not found " + machineExistence.getId());
        }
    }
    
    public synchronized void deleteMachineExistence(MachineExistence machineExistence) {
        if(machineExistence.getId() != null) {
            int size = machineExistenceList.size();
            for(int i=0; i<size; i++) {
            	MachineExistence t = machineExistenceList.get(i);
                if(t.getId().equals(machineExistence.getId())){
                	machineExistenceList.remove(i);
                    serializator.grabarLista(machineExistenceList);
                    return;
                }
            }
        }
    }
    
    private synchronized Integer getNewId() {
        Integer lastId = 0;
        for(int i=0; i<machineExistenceList.size(); i++) {
        	MachineExistence aux = machineExistenceList.get(i);
            if(lastId < aux.getId()){
                lastId = aux.getId();
            }
        }
        return lastId + 1;
    }

}
