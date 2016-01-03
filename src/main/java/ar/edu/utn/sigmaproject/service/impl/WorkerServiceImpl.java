package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.Worker;
import ar.edu.utn.sigmaproject.service.WorkerService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class WorkerServiceImpl implements WorkerService {
	static List<Worker> workerList = new ArrayList<Worker>();
    private SerializationService serializator = new SerializationService("worker");

    public WorkerServiceImpl() {
        List<Worker> aux = serializator.obtenerLista();
        if(aux != null) {
        	workerList = aux;
        } else {
            serializator.grabarLista(workerList);
        }
    }
    
    public synchronized List<Worker> getWorkerList() {
        List<Worker> list = new ArrayList<Worker>();
        for(Worker each: workerList) {
            list.add(Worker.clone(each));
        }
        return list;
    }
    
    public synchronized Worker getWorker(Integer id) {
        int size = workerList.size();
        for(int i=0; i<size; i++) {
        	Worker t = workerList.get(i);
            if(t.getId().equals(id)) {
                return Worker.clone(t);
            }
        }
        return null;
    }
    
    public synchronized Worker saveWorker(Worker worker) {
        if(worker.getId() == null) {
        	worker.setId(getNewId());
        }
        worker = Worker.clone(worker);
        workerList.add(worker);
        serializator.grabarLista(workerList);
        return worker;
    }
    
    public synchronized Worker updateWorker(Worker worker) {
        if(worker.getId() == null) {
            throw new IllegalArgumentException("can't update a null-id Worker, save it first");
        }else {
        	worker = Worker.clone(worker);
            int size = workerList.size();
            for(int i=0; i<size; i++) {
            	Worker t = workerList.get(i);
                if(t.getId().equals(worker.getId())){
                	workerList.set(i, worker);
                    serializator.grabarLista(workerList);
                    return worker;
                }
            }
            throw new RuntimeException("Worker not found " + worker.getId());
        }
    }
    
    public synchronized void deleteWorker(Worker worker) {
        if(worker.getId() != null) {
            int size = workerList.size();
            for(int i=0; i<size; i++) {
            	Worker t = workerList.get(i);
                if(t.getId().equals(worker.getId())){
                	workerList.remove(i);
                    serializator.grabarLista(workerList);
                    return;
                }
            }
        }
    }
    
    private synchronized Integer getNewId() {
        Integer lastId = 0;
        for(int i=0; i<workerList.size(); i++) {
        	Worker aux = workerList.get(i);
            if(lastId < aux.getId()){
                lastId = aux.getId();
            }
        }
        return lastId + 1;
    }

	public Worker getWorker(String name) {
		int size = workerList.size();
        for(int i=0; i<size; i++) {
        	Worker t = workerList.get(i);
            if(t.getName().compareToIgnoreCase(name) == 0) {
                return Worker.clone(t);
            }
        }
        return null;
	}
}
