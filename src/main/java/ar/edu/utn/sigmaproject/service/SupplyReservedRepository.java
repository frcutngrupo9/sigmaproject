package ar.edu.utn.sigmaproject.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.SupplyRequirement;
import ar.edu.utn.sigmaproject.domain.SupplyReserved;

@Repository
public interface SupplyReservedRepository extends JpaRepository<SupplyReserved, Long> {

	SupplyReserved findBySupplyRequirement(SupplyRequirement supplyRequirement);

}
