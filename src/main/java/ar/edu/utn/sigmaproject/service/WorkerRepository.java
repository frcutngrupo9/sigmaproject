package ar.edu.utn.sigmaproject.service;

import ar.edu.utn.sigmaproject.domain.Worker;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkerRepository extends SearchableRepository<Worker, Long> {

}
