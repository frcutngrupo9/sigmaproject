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
		List<Machine> aux = serializator.obtenerLista();
		if(aux != null) {
			machineList = aux;
		} else {
			serializator.grabarLista(machineList);
		}
	}

	public synchronized List<Machine> getMachineList() {
		List<Machine> list = new ArrayList<Machine>();
		for(Machine each:machineList) {
			list.add(Machine.clone(each));
		}
		return list;
	}

	public synchronized List<Machine> getMachineList(Integer idProcessType) {
		List<Machine> list = new ArrayList<Machine>();
		for(Machine each:machineList) {
			if(each.getIdProcessType().equals(idProcessType)) {
				list.add(Machine.clone(each));
			}
		}
		return list;
	}

	public synchronized Machine getMachine(Integer idProcessType, Integer idMachineType) {
		int size = machineList.size();
		for(int i = 0; i < size; i++) {
			Machine aux = machineList.get(i);
			if(aux.getIdProcessType().equals(idProcessType) && aux.getIdMachineType().equals(idMachineType)) {
				return Machine.clone(aux);
			}
		}
		return null;
	}

	public synchronized Machine saveMachine(Machine machine) {
		machine = Machine.clone(machine);
		machineList.add(machine);
		serializator.grabarLista(machineList);
		return machine;
	}

	public synchronized Machine updateMachine(Machine supply) {
		if(supply.getIdProcessType()==null || supply.getIdMachineType()==null) {
			throw new IllegalArgumentException("can't update a null-id Machine, save it first");
		} else {
			supply = Machine.clone(supply);
			int size = machineList.size();
			for(int i = 0; i < size; i++) {
				Machine aux = machineList.get(i);
				if(aux.getIdProcessType().equals(supply.getIdProcessType()) && aux.getIdMachineType().equals(supply.getIdMachineType())) {
					machineList.set(i, supply);
					serializator.grabarLista(machineList);
					return supply;
				}
			}
			throw new RuntimeException("Machine not found "+supply.getIdProcessType()+" "+supply.getIdMachineType());
		}
	}

	public synchronized void deleteMachine(Machine supply) {
		if(supply.getIdProcessType()!=null && supply.getIdMachineType()!=null) {
			int size = machineList.size();
			for(int i = 0; i < size; i++) {
				Machine aux = machineList.get(i);
				if(aux.getIdProcessType().equals(supply.getIdProcessType()) && aux.getIdMachineType().equals(supply.getIdMachineType())) {
					machineList.remove(i);
					serializator.grabarLista(machineList);
					return;
				}
			}
		}
	}
}
