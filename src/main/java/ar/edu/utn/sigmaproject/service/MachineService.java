package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.Machine;

public interface MachineService {
	List<Machine> getMachineExistenceList();

	Machine getMachineExistence(Integer idMachineExistence);

	Machine saveMachineExistence(Machine machineExistence);

	Machine updateMachineExistence(Machine machineExistence);

	void deleteMachineExistence(Machine machineExistence);

}
