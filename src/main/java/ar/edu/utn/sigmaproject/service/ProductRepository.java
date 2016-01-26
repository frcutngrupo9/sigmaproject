package ar.edu.utn.sigmaproject.service;

import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.Product;

@Repository
public interface ProductRepository extends SearchableRepository<Product, Long> {

}
