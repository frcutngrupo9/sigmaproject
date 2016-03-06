package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.Machine;

public interface MachineService {
	List<Machine> getMachineList();

	Machine getMachine(Integer idMachine);

	Machine saveMachine(Machine machine);

	Machine updateMachine(Machine machine);

	void deleteMachine(Machine machine);

}
