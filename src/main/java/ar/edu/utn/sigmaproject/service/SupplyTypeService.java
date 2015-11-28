package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.SupplyType;

public interface SupplyTypeService {
    
    List<SupplyType> getSupplyTypeList();
    
    SupplyType getSupplyType(Integer idSupplyType);

    SupplyType saveSupplyType(SupplyType supplyType);

    SupplyType updateSupplyType(SupplyType supplyType);

    void deleteSupplyType(SupplyType supply);
}
