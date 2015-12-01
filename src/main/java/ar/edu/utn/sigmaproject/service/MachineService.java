package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.Machine;

public interface MachineService {

	List<Machine> getMachineList();
	
	List<Machine> getMachineList(Integer idProcessType);
	
	Machine getMachine(Integer idProcessType, Integer idMachineType);

	Machine saveMachine(Machine machine);

	Machine updateMachine(Machine machine);

	void deleteMachine(Machine machine);

}
