package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductExistence;
import ar.edu.utn.sigmaproject.service.ProductExistenceService;
import ar.edu.utn.sigmaproject.service.impl.ProductServiceImpl;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class ProductExistenceServiceImpl implements ProductExistenceService {

	static List<ProductExistence> productExistenceList = new ArrayList<ProductExistence>();
	private SerializationService serializator = new SerializationService("product_existence");

	public ProductExistenceServiceImpl() {
		List<ProductExistence> aux = serializator.obtenerLista();
		if(aux != null) {
			productExistenceList = aux;
		} else {
			serializator.grabarLista(productExistenceList);
		}
	}

	public synchronized List<ProductExistence> getProductExistenceList() {
		List<ProductExistence> list = new ArrayList<ProductExistence>();
		for(ProductExistence productExistence : productExistenceList) {
			list.add(ProductExistence.clone(productExistence));
		}
		return list;
	}

	public synchronized ProductExistence getProductExistence(Integer idProduct) {
		int size = productExistenceList.size();
		for(int i = 0; i < size; i++) {
			ProductExistence t = productExistenceList.get(i);
			if(t.getIdProduct().equals(idProduct)) {
				return ProductExistence.clone(t);
			}
		}
		return null;
	}

	public synchronized ProductExistence saveProductExistence(ProductExistence productExistence) {
		if(productExistence.getIdProduct() == null) {
			throw new IllegalArgumentException("can't save a null-id ProductExistence");
		} else {
			Product aux = (new ProductServiceImpl()).getProduct(productExistence.getIdProduct());
			if(aux == null) {
				throw new IllegalArgumentException("Product referenced by ProductExistence not found");
			} else {
				productExistence = ProductExistence.clone(productExistence);
				productExistenceList.add(productExistence);
				serializator.grabarLista(productExistenceList);
			}
		}
		return productExistence;
	}

	public synchronized ProductExistence updateProductExistence(ProductExistence productExistence) {
		if(productExistence.getIdProduct() == null) {
			throw new IllegalArgumentException("can't update a null-id ProductExistence, save it first");
		} else {
			productExistence = ProductExistence.clone(productExistence);
			int size = productExistenceList.size();
			for(int i = 0; i < size; i++) {
				ProductExistence t = productExistenceList.get(i);
				if(t.getIdProduct().equals(productExistence.getIdProduct())) {
					productExistenceList.set(i, productExistence);
					serializator.grabarLista(productExistenceList);
					return productExistence;
				}
			}
			throw new RuntimeException("ProductExistence not found " + productExistence.getIdProduct());
		}
	}

	public synchronized void deleteProductExistence(ProductExistence productExistence) {
		if(productExistence.getIdProduct() != null) {
			int size = productExistenceList.size();
			for(int i = 0; i < size; i++) {
				ProductExistence t = productExistenceList.get(i);
				if(t.getIdProduct().equals(productExistence.getIdProduct())) {
					productExistenceList.remove(i);
					serializator.grabarLista(productExistenceList);
					return;
				}
			}
		}
	}

}
