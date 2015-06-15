package ar.edu.utn.sigmaproject.service;

import java.util.List;
import ar.edu.utn.sigmaproject.domain.Process;

public interface ProcessListService {
	
	List<Process> getProcessList();
	
	Process getProcess(Integer idPiece, Integer idProcessType);

	Process saveProcess(Process process);

	Process updateProcess(Process process);

	void deleteProcess(Process process);

}

