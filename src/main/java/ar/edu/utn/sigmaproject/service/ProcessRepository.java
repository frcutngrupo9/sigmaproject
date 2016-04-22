package ar.edu.utn.sigmaproject.service;

import ar.edu.utn.sigmaproject.domain.Process;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessRepository extends SearchableRepository<Process, Long> {
}
