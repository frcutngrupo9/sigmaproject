package ar.edu.utn.sigmaproject.service;

import ar.edu.utn.sigmaproject.domain.Piece;
import org.springframework.stereotype.Repository;

@Repository
public interface PieceRepository extends SearchableRepository<Piece, Long> {

}
