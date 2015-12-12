package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.RawMaterial;

public interface RawMaterialService {

	List<RawMaterial> getRawMaterialList();
	
	List<RawMaterial> getRawMaterialList(Integer idProduct);
	
	RawMaterial getRawMaterial(Integer idProduct, Integer idRawMaterialType);

	RawMaterial saveRawMaterial(RawMaterial rawMaterial);

	RawMaterial updateRawMaterial(RawMaterial rawMaterial);

	void deleteRawMaterial(RawMaterial rawMaterial);
	
	void deleteAll(Integer idProduct);

}
