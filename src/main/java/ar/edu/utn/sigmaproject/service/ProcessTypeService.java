package ar.edu.utn.sigmaproject.service;

import java.util.List;
import ar.edu.utn.sigmaproject.domain.ProcessType;

public interface ProcessTypeService {

	List<ProcessType> getProcessTypeList();

	ProcessType getProcessType(Integer id);

}
