package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.SupplyExistence;

public interface SupplyExistenceService {

	List<SupplyExistence> getSupplyExistenceList();

	SupplyExistence getSupplyExistence(Integer idSupplyType);

	SupplyExistence saveSupplyExistence(SupplyExistence supplyExistence);

	SupplyExistence updateSupplyExistence(SupplyExistence supplyExistence);

	void deleteSupplyExistence(SupplyExistence supplyExistence);

}
