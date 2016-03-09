package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.service.ProcessTypeService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class ProcessTypeServiceImpl implements ProcessTypeService {

	static int processTypeId = 1;
	static List<ProcessType> processTypeList = new ArrayList<ProcessType>();
	private SerializationService serializator = new SerializationService("process_type");
	static{
		processTypeList.add(new ProcessType(processTypeId++, null, "trozado de tablas", 1));
		processTypeList.add(new ProcessType(processTypeId++, null, "garlopeado", 2));
		processTypeList.add(new ProcessType(processTypeId++, null, "cepillado", 3));
		processTypeList.add(new ProcessType(processTypeId++, null, "cortar el ancho en escuadradora", 4));
		processTypeList.add(new ProcessType(processTypeId++, null, "cortar el largo en escuadradora", 5));
		processTypeList.add(new ProcessType(processTypeId++, null, "escoplado", 6));
		processTypeList.add(new ProcessType(processTypeId++, null, "espigado", 7));
		processTypeList.add(new ProcessType(processTypeId++, null, "hacer molduras", 8));
		processTypeList.add(new ProcessType(processTypeId++, null, "hacer canal", 9));
		processTypeList.add(new ProcessType(processTypeId++, null, "replanado", 10));
		processTypeList.add(new ProcessType(processTypeId++, null, "masillado", 11));
		processTypeList.add(new ProcessType(processTypeId++, null, "clavado", 12));
		processTypeList.add(new ProcessType(processTypeId++, null, "lijado", 13));
		processTypeList.add(new ProcessType(processTypeId++, null, "armado", 14));
	}
	
	public ProcessTypeServiceImpl() {
		@SuppressWarnings("unchecked")
		List<ProcessType> aux = serializator.obtenerLista();
		if(aux != null) {
			processTypeList = aux;
			int newId = getNewId();
			if(newId != processTypeId) {
				processTypeId = newId;
			}
		} else {
			serializator.grabarLista(processTypeList);
		}
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

	public ProcessType saveProcessType(ProcessType processType) {
		if(processType.getId() == null) {
			processType.setId(processTypeId++);
		}
		processType = ProcessType.clone(processType);
		processTypeList.add(processType);
		serializator.grabarLista(processTypeList);
		return processType;
	}

	public ProcessType updateProcessType(ProcessType processType) {
		if(processType.getId() == null) {
			throw new IllegalArgumentException("can't update a null-id process type, save it first");
		} else {
			processType = ProcessType.clone(processType);
			int size = processTypeList.size();
			for(int i=0; i<size; i++) {
				ProcessType t = processTypeList.get(i);
				if(t.getId().equals(processType.getId())){
					processTypeList.set(i, processType);
					serializator.grabarLista(processTypeList);
					return processType;
				}
			}
			throw new RuntimeException("Process Type not found " + processType.getId());
		}
	}

	public void deleteProcessType(ProcessType processType) {
		if(processType.getId() != null) {
			int size = processTypeList.size();
			for(int i=0; i<size; i++) {
				ProcessType t = processTypeList.get(i);
				if(t.getId().equals(processType.getId())){
					processTypeList.remove(i);
					serializator.grabarLista(processTypeList);
					return;
				}
			}
		}
	}
	
	private synchronized Integer getNewId() {
		Integer lastId = 0;
		for(int i=0; i<processTypeList.size(); i++) {
			ProcessType aux = processTypeList.get(i);
			if(lastId < aux.getId()){
				lastId = aux.getId();
			}
		}
		return lastId + 1;
	}
}
