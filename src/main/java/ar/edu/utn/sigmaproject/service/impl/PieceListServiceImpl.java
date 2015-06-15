package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.service.PieceListService;

public class PieceListServiceImpl implements PieceListService {
	
	static List<Piece> pieceList = new ArrayList<Piece>();
	
	static{
		pieceList.add(new Piece(1, 1, 5L, 5L, 5L, 5L, 5L, true, 1));
	}
	
	public synchronized List<Piece> getPieceList() {
		List<Piece> list = new ArrayList<Piece>();
		for(Piece piece:pieceList){
			list.add(Piece.clone(piece));
		}
		return list;
	}
	
	public synchronized Piece getPiece(Integer id) {
		int size = pieceList.size();
		for(int i=0;i<size;i++){
			Piece t = pieceList.get(i);
			if(t.getId().equals(id)){
				return Piece.clone(t);
			}
		}
		return null;
	}
	
	public synchronized Piece savePiece(Piece piece) {
		piece = Piece.clone(piece);
		pieceList.add(piece);
		return piece;
	}
	
	public synchronized Piece updatePiece(Piece piece) {
		if(piece.getId()==null){
			throw new IllegalArgumentException("can't update a null-id piece, save it first");
		}else{
			piece = Piece.clone(piece);
			int size = pieceList.size();
			for(int i=0;i<size;i++){
				Piece t = pieceList.get(i);
				if(t.getId().equals(piece.getId())){
					pieceList.set(i, piece);
					return piece;
				}
			}
			throw new RuntimeException("Piece not found "+piece.getId());
		}
	}
	
	public synchronized void deletePiece(Piece piece) {
		if(piece.getId()!=null){
			int size = pieceList.size();
			for(int i=0;i<size;i++){
				Piece t = pieceList.get(i);
				if(t.getId().equals(piece.getId())){
					pieceList.remove(i);
					return;
				}
			}
		}
	}

}
