package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.RawMaterialType;

public interface RawMaterialTypeService {

	List<RawMaterialType> getRawMaterialTypeList();

	RawMaterialType getRawMaterialType(Integer idRawMaterialType);

	RawMaterialType saveRawMaterialType(RawMaterialType rawMaterialType);

	RawMaterialType updateRawMaterialType(RawMaterialType rawMaterialType);

	void deleteRawMaterialType(RawMaterialType rawMaterialType);
}