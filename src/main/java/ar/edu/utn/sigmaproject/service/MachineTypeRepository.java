package ar.edu.utn.sigmaproject.service;

import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.MachineType;

@Repository
public interface MachineTypeRepository extends SearchableRepository<MachineType, Long> {

	MachineType findFirstByName(String string);

}
