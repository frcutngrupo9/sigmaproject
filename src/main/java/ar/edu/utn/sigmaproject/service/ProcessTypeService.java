package ar.edu.utn.sigmaproject.service;

import java.util.List;
import ar.edu.utn.sigmaproject.domain.ProcessType;

public interface ProcessTypeService {
	
	/** get ProcessType list **/
	List<ProcessType> getProcessTypeList();
	
	/** get ProcessType by id **/
	ProcessType getProcessType(Integer id);

}
