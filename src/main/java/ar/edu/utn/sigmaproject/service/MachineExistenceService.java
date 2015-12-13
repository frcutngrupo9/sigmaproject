package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.MachineExistence;

public interface MachineExistenceService {
	List<MachineExistence> getMachineExistenceList();
    
	MachineExistence getMachineExistence(Integer idMachineExistence);

	MachineExistence saveMachineExistence(MachineExistence machineExistence);

	MachineExistence updateMachineExistence(MachineExistence machineExistence);

    void deleteMachineExistence(MachineExistence machineExistence);

}
