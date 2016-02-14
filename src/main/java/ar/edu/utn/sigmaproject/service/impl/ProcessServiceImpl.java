package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.service.ProcessService;
import ar.edu.utn.sigmaproject.service.ProductionOrderDetailService;
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
		for(Process process:processList) {
			if(process.isClone() == false) {// se devuelven solo los que no sean clones
				list.add(Process.clone(process));
			}
		}
		return list;
	}
	
	public synchronized List<Process> getProcessList(Integer idPiece) {
		List<Process> list = new ArrayList<Process>();
		for(Process process:processList) {
			if(process.getIdPiece().equals(idPiece) && process.isClone() == false) {
				list.add(Process.clone(process));
			}
		}
		return list;
	}
	
	public synchronized List<Process> getCompleteProcessList(Integer idPiece) {// devuelve tambien procesos clones
		List<Process> list = new ArrayList<Process>();
		for(Process process:processList) {
			if(process.getIdPiece().equals(idPiece)) {
				list.add(Process.clone(process));
			}
		}
		return list;
	}
	
	public synchronized Process getProcess(Integer id) {
		int size = processList.size();
  		for(int i = 0; i < size; i++) {
  			Process t = processList.get(i);
  			if(t.getId().equals(id)) {
  				return Process.clone(t);
  			}
  		}
  		return null;
	}
	
	public synchronized Process getProcess(Integer idPiece, Integer idProcessType) {
		int size = processList.size();
  		for(int i = 0; i < size; i++) {
  			Process aux = processList.get(i);
  			if(aux.getIdPiece().equals(idPiece) && aux.getIdProcessType().equals(idProcessType) && aux.isClone() == false) {
  				return Process.clone(aux);
  			}
  		}
  		return null;
	}
	
	public synchronized Process saveProcess(Process process) {
		if(process.getId() == null) {
			Integer newId = getNewId();
			process.setId(newId);
		}
		process = Process.clone(process);
		processList.add(process);
		serializator.grabarLista(processList);
		return process;
	}
	
	public synchronized Process updateProcess(Process process) {
		if(process.getId() == null) {
			throw new IllegalArgumentException("can't update a null-id process, save it first");
		}else {
			process = Process.clone(process);
			int size = processList.size();
			for(int i = 0; i < size; i++) {
				Process t = processList.get(i);
				if(t.getId().equals(process.getId())) {
					if(isInsideProductionOrder(process.getId())) {
						// debemos clonar el proceso y asignar el detalle de la orden de produccion al clon
						cloneAndAssignToProductionOrder(process);
					}
					processList.set(i, process);
					serializator.grabarLista(processList);
					return process;
				}
			}
			throw new RuntimeException("Process not found " + process.getId());
		}
	}
	
	public synchronized void deleteProcess(Process process) {
		if(process.getId() != null) {
			int size = processList.size();
			for(int i = 0; i < size; i++) {
				Process t = processList.get(i);
				if(t.getId().equals(process.getId())) {
					if(isInsideProductionOrder(process.getId())) {
						// debemos clonar el proceso y asignar el detalle de la orden de produccion al clon
						cloneAndAssignToProductionOrder(process);
					}
					processList.remove(i);
					serializator.grabarLista(processList);
					return;
				}
			}
		}
	}

	public synchronized void deleteAll(Integer idPiece) {
		List<Process> listDelete = getProcessList(idPiece);
		for(Process delete:listDelete) {
			deleteProcess(delete);
		}
	}
	
	public synchronized Integer getNewId() {
		Integer lastId = 0;
		for(int i = 0; i < processList.size(); i++) {
			Process aux = processList.get(i);
			if(lastId < aux.getId()) {
				lastId = aux.getId();
			}
		}
		return lastId + 1;
	}

	public Process generateClone(Process other) {
		Process aux = new Process(null, other.getIdPiece(), other.getIdProcessType(), other.getDetails(), other.getTime());
		aux.setClone(true);
		return aux;
	}
	
	private boolean isInsideProductionOrder(Integer id) {
		ProductionOrderDetailService productionOrderDetailService = new ProductionOrderDetailServiceImpl();
		List<ProductionOrderDetail> productionOrderDetailList = productionOrderDetailService.getProductionOrderDetailListByProcessId(id);
		boolean value = false;
		if(productionOrderDetailList.size() >= 1) {
			value = true;// existe en por lo menos 1 detalle
		}
		return value;
	}
	
	private void cloneAndAssignToProductionOrder(Process process) {
		Process clone = generateClone(process);
		clone = saveProcess(clone);// para que devuelva un proceso con id agregado
		ProductionOrderDetailService productionOrderDetailService = new ProductionOrderDetailServiceImpl();
		List<ProductionOrderDetail> productionOrderDetailList = productionOrderDetailService.getProductionOrderDetailListByProcessId(process.getId());
		for(ProductionOrderDetail productionOrderDetail:productionOrderDetailList) {
			productionOrderDetail.setIdProcess(clone.getId());// asignamos la referencia al clon
			productionOrderDetailService.updateProductionOrderDetail(productionOrderDetail);// actualizamos el detalle
		}
	}
	
}
