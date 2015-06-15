package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.service.ProductListService;

public class ProductListServiceImpl implements ProductListService {
	
	static List<Product> productList = new ArrayList<Product>();
	
	static{
		productList.add(new Product(1, "Silla", "Confortable"));
	}
	
	public synchronized List<Product> getProductList() {
		List<Product> list = new ArrayList<Product>();
		for(Product product:productList){
			list.add(Product.clone(product));
		}
		return list;
	}
	
	public synchronized Product getProduct(Integer id) {
		int size = productList.size();
		for(int i=0;i<size;i++){
			Product t = productList.get(i);
			if(t.getId().equals(id)){
				return Product.clone(t);
			}
		}
		return null;
	}
	
	public synchronized Product saveProduct(Product product) {
		product = Product.clone(product);
		productList.add(product);
		return product;
	}
	
	public synchronized Product updateProduct(Product product) {
		if(product.getId()==null){
			throw new IllegalArgumentException("can't update a null-id product, save it first");
		}else{
			product = Product.clone(product);
			int size = productList.size();
			for(int i=0;i<size;i++){
				Product t = productList.get(i);
				if(t.getId().equals(product.getId())){
					productList.set(i, product);
					return product;
				}
			}
			throw new RuntimeException("Product not found "+product.getId());
		}
	}
	
	public synchronized void deleteProduct(Product product) {
		if(product.getId()!=null){
			int size = productList.size();
			for(int i=0;i<size;i++){
				Product t = productList.get(i);
				if(t.getId().equals(product.getId())){
					productList.remove(i);
					return;
				}
			}
		}
	}

}
