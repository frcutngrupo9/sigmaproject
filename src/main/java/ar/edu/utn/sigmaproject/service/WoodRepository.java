package ar.edu.utn.sigmaproject.service;

import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.Wood;

@Repository
public interface WoodRepository extends SearchableRepository<Wood, Long> {

}