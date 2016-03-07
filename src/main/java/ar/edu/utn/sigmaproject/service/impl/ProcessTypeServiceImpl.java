package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.service.ProcessTypeService;

public class ProcessTypeServiceImpl implements ProcessTypeService {

	static int processTypeId = 1;
	static List<ProcessType> processTypeList = new ArrayList<ProcessType>();  
	static{
		processTypeList.add(new ProcessType(processTypeId++, null, "trozado de tablas", 1));
		processTypeList.add(new ProcessType(processTypeId++, null, "garlopeado", 1));
		processTypeList.add(new ProcessType(processTypeId++, null, "cepillado", 1));
		processTypeList.add(new ProcessType(processTypeId++, null, "cortar el ancho en escuadradora", 1));
		processTypeList.add(new ProcessType(processTypeId++, null, "cortar el largo en escuadradora", 1));
		processTypeList.add(new ProcessType(processTypeId++, null, "escoplado", 1));
		processTypeList.add(new ProcessType(processTypeId++, null, "espigado", 1));
		processTypeList.add(new ProcessType(processTypeId++, null, "hacer molduras", 1));
		processTypeList.add(new ProcessType(processTypeId++, null, "hacer canal", 1));
		processTypeList.add(new ProcessType(processTypeId++, null, "replanado", 1));
		processTypeList.add(new ProcessType(processTypeId++, null, "masillado", 1));
		processTypeList.add(new ProcessType(processTypeId++, null, "clavado", 1));
		processTypeList.add(new ProcessType(processTypeId++, null, "lijado", 1));
		processTypeList.add(new ProcessType(processTypeId++, null, "armado", 1));
	}

	public List<ProcessType> getProcessTypeList() {
		List<ProcessType> list = new ArrayList<ProcessType>();
		for(ProcessType processType:processTypeList) {
			list.add(ProcessType.clone(processType));
		}
		return list;
	}

	public synchronized ProcessType getProcessType(Integer id) {
		int size = processTypeList.size();
		for(int i = 0; i < size; i++){
			ProcessType t = processTypeList.get(i);
			if(t.getId().equals(id)) {
				return ProcessType.clone(t);
			}
		}
		return null;
	}
}
