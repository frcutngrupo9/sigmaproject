package ar.edu.utn.sigmaproject.service;

import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.ProductCategory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.Product;

@Repository
public interface ProductRepository extends SearchableRepository<Product, Long> {

	Page<Product> findAllByCategory(ProductCategory productCategory, Pageable page);

	Product findByPieces(Piece piece);

}
