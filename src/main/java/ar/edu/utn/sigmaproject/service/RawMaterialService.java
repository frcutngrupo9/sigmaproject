package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.RawMaterial;

public interface RawMaterialService {
	
	List<RawMaterial> getRawMaterialList();
	
	RawMaterial getRawMaterial(Integer idRawMaterial);

	RawMaterial saveRawMaterial(RawMaterial rawMaterial);

	RawMaterial updateRawMaterial(RawMaterial rawMaterial);

	void deleteRawMaterial(RawMaterial rawMaterial);
}