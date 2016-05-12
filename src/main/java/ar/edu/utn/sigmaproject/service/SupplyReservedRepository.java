package ar.edu.utn.sigmaproject.service;

import ar.edu.utn.sigmaproject.domain.SupplyRequirement;
import ar.edu.utn.sigmaproject.domain.SupplyReserved;
import ar.edu.utn.sigmaproject.domain.SupplyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplyReservedRepository extends JpaRepository<SupplyReserved, Long> {

	SupplyReserved findBySupplyTypeAndSupplyRequirement(SupplyType supplyType, SupplyRequirement supplyRequirement);

}
