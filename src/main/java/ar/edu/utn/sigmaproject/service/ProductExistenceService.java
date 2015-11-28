package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.ProductExistence;

public interface ProductExistenceService {
	
	List<ProductExistence> getProductExistenceList();
    
	ProductExistence getProductExistence(Integer idProduct);

	ProductExistence saveProductExistence(ProductExistence productExistence);

	ProductExistence updateProductExistence(ProductExistence productExistence);

    void deleteProductExistence(ProductExistence productExistence);

}
