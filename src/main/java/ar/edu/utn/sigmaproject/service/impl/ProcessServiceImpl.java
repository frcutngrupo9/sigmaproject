package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.service.ProcessService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class ProcessServiceImpl implements ProcessService {

	static List<Process> processList = new ArrayList<Process>();
	private SerializationService serializator = new SerializationService("process");
	
	public ProcessServiceImpl() {
		List<Process> aux = serializator.obtenerLista();
		if(aux != null) {
			processList = aux;
		} else {
			serializator.grabarLista(processList);
		}
	}
	
	//synchronized para prevenir acceso concurrente al servicio de lista
	public synchronized List<Process> getProcessList() {
		List<Process> list = new ArrayList<Process>();
		for(Process process:processList){
			list.add(Process.clone(process));
		}
		return list;
	}
	
	public synchronized List<Process> getProcessList(Integer idPiece) {
		List<Process> list = new ArrayList<Process>();
		for(Process process:processList){
			if(process.getIdPiece().equals(idPiece)) {
				list.add(Process.clone(process));
			}
		}
		return list;
	}
	
	public synchronized Process getProcess(Integer idPiece, Integer idProcessType) {
		int size = processList.size();
  		for(int i = 0; i < size; i++) {
  			Process t = processList.get(i);
  			if(t.getIdPiece().equals(idPiece) && t.getIdProcessType().equals(idProcessType)) {
  				return Process.clone(t);
  			}
  		}
  		return null;
	}
	
	public synchronized Process saveProcess(Process process) {
		process = Process.clone(process);
		processList.add(process);
		serializator.grabarLista(processList);
		return process;
	}
	
	public synchronized Process updateProcess(Process process) {
		if(process.getIdPiece() == null && process.getIdProcessType() == null) {
			throw new IllegalArgumentException("can't update a null-id process, save it first");
		}else {
			process = Process.clone(process);
			int size = processList.size();
			for(int i = 0; i < size; i++) {
				Process t = processList.get(i);
				if(t.getIdPiece().equals(process.getIdPiece()) && t.getIdProcessType().equals(process.getIdProcessType())){
					processList.set(i, process);
					serializator.grabarLista(processList);
					return process;
				}
			}
			throw new RuntimeException("Process not found " + process.getIdPiece()+" "+process.getIdProcessType());
		}
	}
	
	public synchronized void deleteProcess(Process process) {
		if(process.getIdPiece()!=null && process.getIdProcessType()!=null){
			int size = processList.size();
			for(int i = 0; i < size; i++) {
				Process t = processList.get(i);
				if(t.getIdPiece().equals(process.getIdPiece()) && t.getIdProcessType().equals(process.getIdProcessType())) {
					processList.remove(i);
					serializator.grabarLista(processList);
					return;
				}
			}
		}
	}
}
