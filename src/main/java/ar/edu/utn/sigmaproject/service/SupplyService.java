package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.Supply;

public interface SupplyService {

	List<Supply> getSupplyList();

	List<Supply> getSupplyList(Integer idProduct);

	Supply getSupply(Integer id);

	Supply getSupply(Integer idProduct, Integer idSupplyType);

	Supply saveSupply(Supply supply);

	Supply updateSupply(Supply supply);

	void deleteSupply(Supply supply);

}
