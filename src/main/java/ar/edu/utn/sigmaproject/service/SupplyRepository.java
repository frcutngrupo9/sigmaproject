package ar.edu.utn.sigmaproject.service;

import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.Supply;

@Repository
public interface SupplyRepository extends SearchableRepository<Supply, Long> {

}
