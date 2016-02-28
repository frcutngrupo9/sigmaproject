package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.Worker;

public interface WorkerService {

	List<Worker> getWorkerList();

	Worker getWorker(Integer idWorker);

	Worker getWorker(String nameWorker);

	Worker saveWorker(Worker worker);

	Worker updateWorker(Worker worker);

	void deleteWorker(Worker worker);

}
