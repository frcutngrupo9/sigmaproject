package ar.edu.utn.sigmaproject.service;

import ar.edu.utn.sigmaproject.domain.ProductCategory;

import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoryRepository extends SearchableRepository<ProductCategory, Long> {

	public ProductCategory findFirstByName(String string);

}