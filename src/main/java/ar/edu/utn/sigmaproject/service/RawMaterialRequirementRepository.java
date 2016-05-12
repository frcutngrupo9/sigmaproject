package ar.edu.utn.sigmaproject.service;

import ar.edu.utn.sigmaproject.domain.RawMaterialRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawMaterialRequirementRepository extends JpaRepository<RawMaterialRequirement, Long> {

}
