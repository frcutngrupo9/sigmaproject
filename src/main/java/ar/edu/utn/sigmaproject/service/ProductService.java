package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.Product;

public interface ProductService {
	
	List<Product> getProductList();
	
	Product getProduct(Integer idProduct);

	Product saveProduct(Product product);
	
	Product saveProduct(Product product, List<Piece> pieceList, List<Process> processList);

	Product updateProduct(Product product);
	
	Product updateProduct(Product product, List<Piece> pieceList, List<Process> processList);

	void deleteProduct(Product product);

	Integer getNewId();

}
