package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.Product;

public interface ProductService {
	
	List<Product> getProductList();
	
	Product getProduct(Integer idProduct);

	Product saveProduct(Product product);

	Product updateProduct(Product product);

	void deleteProduct(Product product);

	Integer getNewId();

}
