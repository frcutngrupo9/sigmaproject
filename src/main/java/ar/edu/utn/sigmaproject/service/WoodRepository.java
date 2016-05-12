package ar.edu.utn.sigmaproject.service;

import ar.edu.utn.sigmaproject.domain.Wood;
import org.springframework.stereotype.Repository;

@Repository
public interface WoodRepository extends SearchableRepository<Wood, Long> {

}
