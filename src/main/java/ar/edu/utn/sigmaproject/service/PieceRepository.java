package ar.edu.utn.sigmaproject.service;

import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;

import org.springframework.stereotype.Repository;

@Repository
public interface PieceRepository extends SearchableRepository<Piece, Long> {

	public Piece findByProcesses(Process process);

}
