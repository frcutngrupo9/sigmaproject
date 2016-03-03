package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.Machine;
import ar.edu.utn.sigmaproject.service.MachineService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class MachineServiceImpl implements MachineService {
	static List<Machine> machineExistenceList = new ArrayList<Machine>();
	private SerializationService serializator = new SerializationService("machine_existence");

	public MachineServiceImpl() {
		@SuppressWarnings("unchecked")
		List<Machine> aux = serializator.obtenerLista();
		if(aux != null) {
			machineExistenceList = aux;
		} else {
			serializator.grabarLista(machineExistenceList);
		}
	}

	public synchronized List<Machine> getMachineExistenceList() {
		List<Machine> list = new ArrayList<Machine>();
		for(Machine each: machineExistenceList) {
			list.add(Machine.clone(each));
		}
		return list;
	}

	public synchronized Machine getMachineExistence(Integer id) {
		int size = machineExistenceList.size();
		for(int i=0; i<size; i++) {
			Machine t = machineExistenceList.get(i);
			if(t.getId().equals(id)) {
				return Machine.clone(t);
			}
		}
		return null;
	}

	public synchronized Machine saveMachineExistence(Machine machineExistence) {
		if(machineExistence.getId() == null) {
			machineExistence.setId(getNewId());
		}
		machineExistence = Machine.clone(machineExistence);
		machineExistenceList.add(machineExistence);
		serializator.grabarLista(machineExistenceList);
		return machineExistence;
	}

	public synchronized Machine updateMachineExistence(Machine machineExistence) {
		if(machineExistence.getId() == null) {
			throw new IllegalArgumentException("can't update a null-id MachineExistence, save it first");
		}else {
			machineExistence = Machine.clone(machineExistence);
			int size = machineExistenceList.size();
			for(int i=0; i<size; i++) {
				Machine t = machineExistenceList.get(i);
				if(t.getId().equals(machineExistence.getId())){
					machineExistenceList.set(i, machineExistence);
					serializator.grabarLista(machineExistenceList);
					return machineExistence;
				}
			}
			throw new RuntimeException("MachineExistence not found " + machineExistence.getId());
		}
	}

	public synchronized void deleteMachineExistence(Machine machineExistence) {
		if(machineExistence.getId() != null) {
			int size = machineExistenceList.size();
			for(int i=0; i<size; i++) {
				Machine t = machineExistenceList.get(i);
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
			Machine aux = machineExistenceList.get(i);
			if(lastId < aux.getId()){
				lastId = aux.getId();
			}
		}
		return lastId + 1;
	}

}
