package ar.edu.utn.sigmaproject.service;

import ar.edu.utn.sigmaproject.domain.ToolType;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolTypeRepository extends SearchableRepository<ToolType, Long> {

}