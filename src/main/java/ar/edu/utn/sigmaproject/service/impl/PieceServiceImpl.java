package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.service.PieceService;
import ar.edu.utn.sigmaproject.service.ProcessService;
import ar.edu.utn.sigmaproject.service.ProductionOrderDetailService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class PieceServiceImpl implements PieceService {

	static List<Piece> pieceList = new ArrayList<Piece>();
	private SerializationService serializator = new SerializationService("piece");

	public PieceServiceImpl() {
		@SuppressWarnings("unchecked")
		List<Piece> aux = serializator.obtenerLista();
		if(aux != null) {
			pieceList = aux;
		} else {
			serializator.grabarLista(pieceList);
		}
	}

	public synchronized List<Piece> getPieceList() {
		List<Piece> list = new ArrayList<Piece>();
		for(Piece piece:pieceList) {
			if(piece.isClone() == false) {// se devuelven solo los que no sean clones
				list.add(Piece.clone(piece));
			}
		}
		return list;
	}

	public synchronized List<Piece> getCompletePieceList() {// devuelve la lista con los clones incluidos
		List<Piece> list = new ArrayList<Piece>();
		for(Piece piece:pieceList) {
			list.add(Piece.clone(piece));
		}
		return list;
	}

	public synchronized List<Piece> getPieceList(Integer idProduct) {
		List<Piece> list = new ArrayList<Piece>();
		for(Piece piece:pieceList) {
			if(piece.getIdProduct().equals(idProduct) && piece.isClone() == false) {
				list.add(Piece.clone(piece));
			}
		}
		return list;
	}

	public synchronized List<Piece> getCompletePieceList(Integer idProduct) {// devuelve la lista con los clones incluidos
		List<Piece> list = new ArrayList<Piece>();
		for(Piece piece:pieceList) {
			if(piece.getIdProduct().equals(idProduct)) {
				list.add(Piece.clone(piece));
			}
		}
		return list;
	}

	public synchronized Piece getPiece(Integer id) {
		int size = pieceList.size();
		for(int i = 0; i < size; i++) {
			Piece t = pieceList.get(i);
			if(t.getId().equals(id)) {
				return Piece.clone(t);
			}
		}
		return null;
	}

	public synchronized Piece savePiece(Piece piece) {
		if(piece.getId() == null) {
			Integer newId = getNewId();
			piece.setId(newId);
		}
		piece = Piece.clone(piece);
		pieceList.add(piece);
		serializator.grabarLista(pieceList);
		return piece;
	}

	public synchronized Piece updatePiece(Piece piece) {
		if(piece.getId() == null) {
			throw new IllegalArgumentException("can't update a null-id piece, save it first");
		}else {
			piece = Piece.clone(piece);
			int size = pieceList.size();
			for(int i = 0; i < size; i++) {
				Piece t = pieceList.get(i);
				if(t.getId().equals(piece.getId())) {
					pieceList.set(i, piece);
					serializator.grabarLista(pieceList);
					return piece;
				}
			}
			throw new RuntimeException("Piece not found "+piece.getId());
		}
	}

	public synchronized void deletePiece(Piece piece) {
		if(piece.getId() != null) {
			// se realiza una eliminacion en cascada de los procesos relacionados a la pieza
			new ProcessServiceImpl().deleteAll(piece.getId());
			int size = pieceList.size();
			for(int i = 0; i < size; i++) {
				Piece t = pieceList.get(i);
				if(t.getId().equals(piece.getId())) {
					if(isInsideProductionOrder(piece.getId())) {
						cloneAndAssignToProcess(piece);// se crea un clon de la pieza y se asignan todos los procesos al clon
					}
					pieceList.remove(i);
					serializator.grabarLista(pieceList);
					return;
				}
			}
		}
	}

	public synchronized Integer getNewId() {
		Integer lastId = 0;
		for(int i = 0; i < pieceList.size(); i++) {
			Piece aux = pieceList.get(i);
			if(lastId < aux.getId()) {
				lastId = aux.getId();
			}
		}
		return lastId + 1;
	}

	public synchronized void deleteAll(Integer idProduct) {
		List<Piece> listDelete = getPieceList(idProduct);
		for(Piece delete:listDelete) {
			deletePiece(delete);
		}
	}

	private boolean isInsideProductionOrder(Integer id) {// si la pieza es referenciada por alguno de los procesos que esta en algun detalle de orden de produccion
		ProcessService processService = new ProcessServiceImpl();
		List<Process> processList = processService.getCompleteProcessList(id);
		for(Process process:processList) {
			ProductionOrderDetailService productionOrderDetailService = new ProductionOrderDetailServiceImpl();
			List<ProductionOrderDetail> productionOrderDetailList = productionOrderDetailService.getProductionOrderDetailListByProcessId(process.getId());
			if(productionOrderDetailList.size() >= 1) {// quiere decir que el proceso de la pieza se encuentra en al menos 1 detalle de orden de produccion
				return true;
			}
		}
		return false;
	}

	private synchronized void cloneAndAssignToProcess(Piece piece) {
		Piece clone = new Piece(null, piece.getIdProduct(), piece.getName(), piece.getLength(), piece.getLengthIdMeasureUnit(), piece.getDepth(), piece.getDepthIdMeasureUnit(), piece.getWidth(), piece.getWidthIdMeasureUnit(), piece.getSize(), piece.isGroup(), piece.getUnits());
		clone.setClone(true);
		clone = savePiece(clone);// para que devuelva una pieza con id agregado

		ProcessService processService = new ProcessServiceImpl();
		List<Process> processList = processService.getCompleteProcessList(piece.getId());

		for(Process process:processList) {
			process.setIdPiece(clone.getId());
			processService.updateProcess(process);
		}
	}
}
