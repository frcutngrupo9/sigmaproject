package ar.edu.utn.sigmaproject.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductCategory;

@Repository
public interface ProductRepository extends SearchableRepository<Product, Long> {

	public Page<Product> findAllByCategory(ProductCategory productCategory, Pageable page);

}