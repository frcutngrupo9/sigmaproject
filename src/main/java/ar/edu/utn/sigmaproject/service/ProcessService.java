package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.Process;

public interface ProcessService {
	
	List<Process> getProcessList();
	
	List<Process> getProcessList(Integer idPiece);
	
	Process getProcess(Integer id);
	
	Process getProcess(Integer idPiece, Integer idProcessType);

	Process saveProcess(Process process);

	Process updateProcess(Process process);

	void deleteProcess(Process process);

	void deleteAll(Integer idPiece);
	
	Process generateClone(Process process);

}

