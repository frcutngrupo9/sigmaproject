package ar.edu.utn.sigmaproject.service;

import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.Piece;

@Repository
public interface PieceRepository extends SearchableRepository<Piece, Long> {

}