package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.RawMaterial;
import ar.edu.utn.sigmaproject.domain.Supply;

public interface ProductService {

	List<Product> getProductList();

	Product getProduct(Integer idProduct);

	Product saveProduct(Product product);

	Product saveProduct(Product product, List<Piece> pieceList, List<Process> processList);

	Product saveProduct(Product product, List<Piece> pieceList, List<Process> processList, List<Supply> supplyList, List<RawMaterial> rawMaterialList);

	Product updateProduct(Product product);

	Product updateProduct(Product product, List<Piece> pieceList, List<Process> processList);

	Product updateProduct(Product product, List<Piece> pieceList, List<Process> processList, List<Supply> supplyList, List<RawMaterial> rawMaterialList);

	void deleteProduct(Product product);

	Integer getNewId();

}
