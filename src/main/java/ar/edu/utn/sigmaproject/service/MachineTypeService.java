package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.MachineType;

public interface MachineTypeService {

	List<MachineType> getMachineTypeList();

	MachineType getMachineType(Integer idMachineType);

	MachineType saveMachineType(MachineType machineType);

	MachineType updateMachineType(MachineType machineType);

	void deleteMachineType(MachineType machineType);
}
