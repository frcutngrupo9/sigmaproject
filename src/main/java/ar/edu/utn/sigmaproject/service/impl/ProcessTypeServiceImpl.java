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
		processTypeList.add(new ProcessType(processTypeId++, null, "Trozar Tablas"));
		processTypeList.add(new ProcessType(processTypeId++, null, "Garlopear"));
		processTypeList.add(new ProcessType(processTypeId++, null, "Asentar"));
		processTypeList.add(new ProcessType(processTypeId++, null, "Cepillar"));
		processTypeList.add(new ProcessType(processTypeId++, null, "Cortar el Ancho"));
		processTypeList.add(new ProcessType(processTypeId++, null, "Cortar el Largo"));
		processTypeList.add(new ProcessType(processTypeId++, null, "Hacer Cortes Curvos"));
		processTypeList.add(new ProcessType(processTypeId++, null, "Hacer Escopladuras"));
		processTypeList.add(new ProcessType(processTypeId++, null, "Hacer Espigas"));
		processTypeList.add(new ProcessType(processTypeId++, null, "Hacer Molduras"));
		processTypeList.add(new ProcessType(processTypeId++, null, "Hacer Canales"));
		processTypeList.add(new ProcessType(processTypeId++, null, "Replanar"));
		processTypeList.add(new ProcessType(processTypeId++, null, "Masillar"));
		processTypeList.add(new ProcessType(processTypeId++, null, "Clavar"));
		processTypeList.add(new ProcessType(processTypeId++, null, "Lijar Cruzado"));
		processTypeList.add(new ProcessType(processTypeId++, null, "Lijar Derecho"));
		processTypeList.add(new ProcessType(processTypeId++, null, "Armar"));
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

	public synchronized List<ProcessType> getProcessTypeList() {
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

	public synchronized ProcessType saveProcessType(ProcessType processType) {
		if(processType.getId() == null) {
			processType.setId(processTypeId++);
		}
		processType = ProcessType.clone(processType);
		processTypeList.add(processType);
		serializator.grabarLista(processTypeList);
		return processType;
	}

	public synchronized ProcessType updateProcessType(ProcessType processType) {
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

	public synchronized void deleteProcessType(ProcessType processType) {
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
