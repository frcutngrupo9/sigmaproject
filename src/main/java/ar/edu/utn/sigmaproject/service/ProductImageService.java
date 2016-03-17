package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.ProductImage;

public interface ProductImageService {
	
	List<ProductImage> findAll();

	ProductImage getProductImage(Integer id);
	
	ProductImage findFirstProductImage(Integer idProduct);
	
	ProductImage save(ProductImage productImage);

	void deleteProductImage(ProductImage productImage);

}
