package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.Machine;
import ar.edu.utn.sigmaproject.service.MachineService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class MachineServiceImpl implements MachineService {
	static List<Machine> machineList = new ArrayList<Machine>();
	private SerializationService serializator = new SerializationService("machine");

	public MachineServiceImpl() {
		@SuppressWarnings("unchecked")
		List<Machine> aux = serializator.obtenerLista();
		if(aux != null) {
			machineList = aux;
		} else {
			serializator.grabarLista(machineList);
		}
	}

	public synchronized List<Machine> getMachineList() {
		List<Machine> list = new ArrayList<Machine>();
		for(Machine each: machineList) {
			list.add(Machine.clone(each));
		}
		return list;
	}

	public synchronized Machine getMachine(Integer id) {
		int size = machineList.size();
		for(int i=0; i<size; i++) {
			Machine t = machineList.get(i);
			if(t.getId().equals(id)) {
				return Machine.clone(t);
			}
		}
		return null;
	}

	public synchronized Machine saveMachine(Machine machine) {
		if(machine.getId() == null) {
			machine.setId(getNewId());
		}
		machine = Machine.clone(machine);
		machineList.add(machine);
		serializator.grabarLista(machineList);
		return machine;
	}

	public synchronized Machine updateMachine(Machine machine) {
		if(machine.getId() == null) {
			throw new IllegalArgumentException("can't update a null-id Machine, save it first");
		}else {
			machine = Machine.clone(machine);
			int size = machineList.size();
			for(int i=0; i<size; i++) {
				Machine t = machineList.get(i);
				if(t.getId().equals(machine.getId())){
					machineList.set(i, machine);
					serializator.grabarLista(machineList);
					return machine;
				}
			}
			throw new RuntimeException("Machine not found " + machine.getId());
		}
	}

	public synchronized void deleteMachine(Machine machine) {
		if(machine.getId() != null) {
			int size = machineList.size();
			for(int i=0; i<size; i++) {
				Machine t = machineList.get(i);
				if(t.getId().equals(machine.getId())){
					machineList.remove(i);
					serializator.grabarLista(machineList);
					return;
				}
			}
		}
	}

	private synchronized Integer getNewId() {
		Integer lastId = 0;
		for(int i=0; i<machineList.size(); i++) {
			Machine aux = machineList.get(i);
			if(lastId < aux.getId()){
				lastId = aux.getId();
			}
		}
		return lastId + 1;
	}

}
