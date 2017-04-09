package ar.edu.utn.sigmaproject.service;

import java.util.List;

import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.RawMaterialType;
import ar.edu.utn.sigmaproject.domain.Wood;
import ar.edu.utn.sigmaproject.domain.WoodReserved;
import ar.edu.utn.sigmaproject.domain.WoodType;

@Repository
public interface WoodRepository extends SearchableRepository<Wood, Long> {

	List<Wood> findByRawMaterialType(RawMaterialType rawMaterialType);
	
	Wood findFirstByWoodsReserved(WoodReserved woodReserved);
	
	Wood findByRawMaterialTypeAndWoodType(RawMaterialType rawMaterialType, WoodType woodType);

}
