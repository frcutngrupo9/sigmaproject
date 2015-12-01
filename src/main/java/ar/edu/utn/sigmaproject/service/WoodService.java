package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.Wood;

public interface WoodService {
    
    List<Wood> getWoodList();
    
    Wood getWood(Integer idWood);

    Wood saveWood(Wood wood);

    Wood updateWood(Wood wood);

    void deleteWood(Wood wood);
}
