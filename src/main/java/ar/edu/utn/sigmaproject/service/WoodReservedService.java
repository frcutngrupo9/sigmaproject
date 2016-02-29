package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.WoodReserved;

public interface WoodReservedService {

	List<WoodReserved> getWoodReservedList();

	WoodReserved getWoodReserved(Integer id);

	WoodReserved saveWoodReserved(WoodReserved woodReserved);

	WoodReserved updateWoodReserved(WoodReserved woodReserved);

	void deleteWoodReserved(WoodReserved woodReserved);

}
