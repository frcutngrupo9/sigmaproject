package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.Supply;

public interface SupplyService {
    
    List<Supply> getSupplyList();
    
    Supply getSupply(Integer idSupply);

    Supply saveSupply(Supply supply);

    Supply updateSupply(Supply supply);

    void deleteSupply(Supply supply);
}
