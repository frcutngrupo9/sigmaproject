package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.service.ProcessTypeService;

public class ProcessTypeServiceImpl implements ProcessTypeService {

	static int processTypeId = 1;
	static List<ProcessType> processTypeList = new ArrayList<ProcessType>();  
	static{
		processTypeList.add(new ProcessType(processTypeId++,"trozado de tablas"));
		processTypeList.add(new ProcessType(processTypeId++,"garlopeado"));
		processTypeList.add(new ProcessType(processTypeId++,"cepillado"));
		processTypeList.add(new ProcessType(processTypeId++,"cortar el ancho en escuadradora"));
		processTypeList.add(new ProcessType(processTypeId++,"cortar el largo en escuadradora"));
		processTypeList.add(new ProcessType(processTypeId++,"escoplado"));
		processTypeList.add(new ProcessType(processTypeId++,"espigado"));
		processTypeList.add(new ProcessType(processTypeId++,"hacer molduras"));
		processTypeList.add(new ProcessType(processTypeId++,"hacer canal"));
		processTypeList.add(new ProcessType(processTypeId++,"replanado"));
		processTypeList.add(new ProcessType(processTypeId++,"masillado"));
		processTypeList.add(new ProcessType(processTypeId++,"clavado"));
		processTypeList.add(new ProcessType(processTypeId++,"lijado"));
		processTypeList.add(new ProcessType(processTypeId++,"armado"));
	}
	
	public List<ProcessType> getProcessTypeList() {
		List<ProcessType> list = new ArrayList<ProcessType>();
		for(ProcessType processType:processTypeList){
			list.add(ProcessType.clone(processType));
		}
		return list;
	}
	
	public synchronized ProcessType getProcessType(Integer id){
		int size = processTypeList.size();
		for(int i=0;i<size;i++){
			ProcessType t = processTypeList.get(i);
			if(t.getId().equals(id)){
				return ProcessType.clone(t);
			}
		}
		return null;
	}
}
