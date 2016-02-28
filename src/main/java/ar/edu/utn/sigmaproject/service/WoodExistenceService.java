package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.WoodExistence;

public interface WoodExistenceService {

	List<WoodExistence> getWoodExistenceList();

	WoodExistence getWoodExistence(Integer idWood);

	WoodExistence saveWoodExistence(WoodExistence woodExistence);

	WoodExistence updateWoodExistence(WoodExistence woodExistence);

	void deleteWoodExistence(WoodExistence woodExistence);

}
