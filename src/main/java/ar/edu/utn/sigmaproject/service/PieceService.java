package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.Piece;

public interface PieceService {
	
	List<Piece> getPieceList();
	
	Piece getPiece(Integer idPiece);

	Piece savePiece(Piece piece);

	Piece updatePiece(Piece piece);

	void deletePiece(Piece piece);

	Integer getNewId();

}
