package ar.edu.utn.sigmaproject.service;

import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.Replanning;

@Repository
public interface ReplanningRepository extends SearchableRepository<Replanning, Long> {

}