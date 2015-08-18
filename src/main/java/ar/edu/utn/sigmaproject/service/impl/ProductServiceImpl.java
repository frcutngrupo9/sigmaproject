package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.RawMaterial;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class ProductServiceImpl implements ProductService {
	
	static List<Product> productList = new ArrayList<Product>();
	private SerializationService serializator = new SerializationService("product");
	
	public ProductServiceImpl() {
		List<Product> aux = serializator.obtenerLista();
		if(aux != null) {
			productList = aux;
		} else {
			serializator.grabarLista(productList);
		}
	}
	
	public synchronized List<Product> getProductList() {
		List<Product> list = new ArrayList<Product>();
		for(Product product:productList){
			list.add(Product.clone(product));
		}
		return list;
	}
	
	public synchronized Product getProduct(Integer id) {
		for(Product product:productList) {
			if(product.getId().equals(id)) {
				return Product.clone(product);
			}
		}
		return null;
	}
	
	public synchronized Product saveProduct(Product product) {
		if(existId(product.getId())){
			throw new IllegalArgumentException("can't save product, id already used");
		}
		product = Product.clone(product);
		productList.add(product);
		serializator.grabarLista(productList);
		return product;
	}
	
	public synchronized Product updateProduct(Product product) {
		if(product.getId() == null) {
			throw new IllegalArgumentException("can't update a null-id product, save it first");
		}else {
			product = Product.clone(product);
			int size = productList.size();
			for(int i = 0; i < size; i++) {
				Product t = productList.get(i);
				if(t.getId().equals(product.getId())) {
					productList.set(i, product);
					serializator.grabarLista(productList);
					return product;
				}
			} throw new RuntimeException("Product not found "+product.getId());
		}
	}
	
	public synchronized void deleteProduct(Product product) {
	    // se debe eliminar tambien todas las piezas relacionadas al producto, asi como los procesos relacionados a las piezas
		if(product.getId() != null) {
			int size = productList.size();
			for(int i=0;i<size;i++) {
				Product t = productList.get(i);
				if(t.getId().equals(product.getId())) {
					productList.remove(i);
					serializator.grabarLista(productList);
					return;
				}
			}
		}
	}
	
	public synchronized Integer getNewId() {
		Integer lastId = 0;
		for(int i = 0; i < productList.size(); i++) {
			Product aux = productList.get(i);
			if(lastId < aux.getId()) {
				lastId = aux.getId();
			}
		}
		return lastId + 1;
	}
	
	private boolean existId(Integer id) {
		boolean value = false;
		for(int i =0; i < productList.size(); i++) {
			Product aux = productList.get(i);
			if(aux.getId().equals(id)) {
				value = true;
			}
		}
		return value;
	}

}
