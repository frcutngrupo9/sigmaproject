package ar.edu.utn.sigmaproject.service;

import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.ProcessType;

@Repository
public interface ProcessTypeRepository extends SearchableRepository<ProcessType, Long> {

}
