package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.MachineType;
import ar.edu.utn.sigmaproject.service.MachineTypeService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class MachineTypeServiceImpl implements MachineTypeService {
	static List<MachineType> machineTypeList = new ArrayList<MachineType>();
	private SerializationService serializator = new SerializationService("machine_type");

	public MachineTypeServiceImpl() {
		List<MachineType> aux = serializator.obtenerLista();
		if(aux != null) {
			machineTypeList = aux;
		} else {
			serializator.grabarLista(machineTypeList);
		}
	}

	public synchronized List<MachineType> getMachineTypeList() {
		List<MachineType> list = new ArrayList<MachineType>();
		for(MachineType each: machineTypeList) {
			list.add(MachineType.clone(each));
		}
		return list;
	}

	public synchronized MachineType getMachineType(Integer id) {
		int size = machineTypeList.size();
		for(int i=0; i<size; i++) {
			MachineType t = machineTypeList.get(i);
			if(t.getId().equals(id)) {
				return MachineType.clone(t);
			}
		}
		return null;
	}

	public synchronized MachineType saveMachineType(MachineType machineType) {
		if(machineType.getId() == null) {
			machineType.setId(getNewId());
		}
		machineType = MachineType.clone(machineType);
		machineTypeList.add(machineType);
		serializator.grabarLista(machineTypeList);
		return machineType;
	}

	public synchronized MachineType updateMachineType(MachineType machineType) {
		if(machineType.getId() == null) {
			throw new IllegalArgumentException("can't update a null-id MachineType, save it first");
		}else {
			machineType = MachineType.clone(machineType);
			int size = machineTypeList.size();
			for(int i=0; i<size; i++) {
				MachineType t = machineTypeList.get(i);
				if(t.getId().equals(machineType.getId())){
					machineTypeList.set(i, machineType);
					serializator.grabarLista(machineTypeList);
					return machineType;
				}
			}
			throw new RuntimeException("MachineType not found " + machineType.getId());
		}
	}

	public synchronized void deleteMachineType(MachineType machineType) {
		if(machineType.getId() != null) {
			int size = machineTypeList.size();
			for(int i=0; i<size; i++) {
				MachineType t = machineTypeList.get(i);
				if(t.getId().equals(machineType.getId())){
					machineTypeList.remove(i);
					serializator.grabarLista(machineTypeList);
					return;
				}
			}
		}
	}

	private synchronized Integer getNewId() {
		Integer lastId = 0;
		for(int i=0; i<machineTypeList.size(); i++) {
			MachineType aux = machineTypeList.get(i);
			if(lastId < aux.getId()){
				lastId = aux.getId();
			}
		}
		return lastId + 1;
	}
}
