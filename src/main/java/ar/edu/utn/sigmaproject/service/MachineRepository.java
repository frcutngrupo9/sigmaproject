package ar.edu.utn.sigmaproject.service;

import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.Machine;

@Repository
public interface MachineRepository extends SearchableRepository<Machine, Long> {

}
