package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.WoodType;

public interface WoodTypeService {

	List<WoodType> getWoodTypeList();

	WoodType getWoodType(Integer idWoodType);

	WoodType saveWoodType(WoodType woodType);

	WoodType updateWoodType(WoodType woodType);

	void deleteWoodType(WoodType woodType);
}
