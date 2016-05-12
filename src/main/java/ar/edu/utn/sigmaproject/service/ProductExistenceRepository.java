package ar.edu.utn.sigmaproject.service;

import ar.edu.utn.sigmaproject.domain.ProductExistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductExistenceRepository extends JpaRepository<ProductExistence, Long> {

}
