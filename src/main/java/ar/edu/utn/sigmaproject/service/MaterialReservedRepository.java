package ar.edu.utn.sigmaproject.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.MaterialReserved;
import ar.edu.utn.sigmaproject.domain.MaterialType;

@Repository
public interface MaterialReservedRepository extends JpaRepository<MaterialReserved, Long> {

	public List<MaterialReserved> findAllByType(MaterialType type);
	
}
