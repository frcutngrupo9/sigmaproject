package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.service.PieceService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class PieceServiceImpl implements PieceService {
	
	static List<Piece> pieceList = new ArrayList<Piece>();
	private SerializationService serializator = new SerializationService("piece");
	
	public PieceServiceImpl() {
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
			list.add(Piece.clone(piece));
		}
		return list;
	}
	
	public synchronized List<Piece> getPieceList(Integer idProduct) {
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
			int size = pieceList.size();
			for(int i = 0; i < size; i++) {
				Piece t = pieceList.get(i);
				if(t.getId().equals(piece.getId())) {
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
	
	private boolean existsId(Integer id) {
		boolean value = false;
		for(int i=0; i<pieceList.size(); i++) {
			Piece aux = pieceList.get(i);
			if(id.compareTo(aux.getId()) == 0) {
				value = true;
				i = pieceList.size();
			}
		}
		return value;
	}
}
