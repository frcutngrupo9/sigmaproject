package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.SupplyReserved;

public interface SupplyReservedService {

	List<SupplyReserved> getSupplyReservedList();

	SupplyReserved getSupplyReserved(Integer idSupplyReserved);

	SupplyReserved getSupplyReserved(Integer idSupplyType, Integer idSupplyRequirement);

	SupplyReserved saveSupplyReserved(SupplyReserved supplyReserved);

	SupplyReserved updateSupplyReserved(SupplyReserved supplyReserved);

	void deleteSupplyReserved(SupplyReserved supplyReserved);

}
