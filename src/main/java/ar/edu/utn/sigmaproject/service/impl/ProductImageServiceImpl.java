package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.ProductImage;
import ar.edu.utn.sigmaproject.service.ProductImageService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class ProductImageServiceImpl  implements ProductImageService {
	static List<ProductImage> productImageList = new ArrayList<ProductImage>();
	private SerializationService serializator = new SerializationService("product_image");

	public ProductImageServiceImpl() {
		@SuppressWarnings("unchecked")
		List<ProductImage> aux = serializator.obtenerLista();
		if(aux != null) {
			productImageList = aux;
		} else {
			serializator.grabarLista(productImageList);
		}
	}

	public List<ProductImage> findAll() {
		List<ProductImage> list = new ArrayList<ProductImage>();
		for(ProductImage each: productImageList) {
			list.add(ProductImage.clone(each));
		}
		return list;
	}
	
	public List<ProductImage> findbyProductId(Integer idProduct) {
		List<ProductImage> list = new ArrayList<ProductImage>();
		for(ProductImage each: productImageList) {
			if(each.getIdProduct().equals(idProduct)) {
				list.add(ProductImage.clone(each));
			}
		}
		return list;
	}

	public ProductImage getProductImage(Integer id) {
		for(ProductImage each: productImageList) {
			if(each.getId().equals(id)) {
				return ProductImage.clone(each);
			}
		}
		return null;
	}

	public ProductImage findFirstProductImage(Integer idProduct) {
		for(ProductImage each: productImageList) {
			if(each.getIdProduct().equals(idProduct)) {
				return ProductImage.clone(each);
			}
		}
		return null;
	}

	public ProductImage save(ProductImage productImage) {
		if(productImage.getId() == null) {
			productImage.setId(getNewId());
			productImage = ProductImage.clone(productImage);
			productImageList.add(productImage);
			serializator.grabarLista(productImageList);
			return productImage;
		} else {
			int size = productImageList.size();
			for(int i=0; i<size; i++) {
				ProductImage t = productImageList.get(i);
				if(t.getId().equals(productImage.getId())){
					productImageList.set(i, productImage);
					serializator.grabarLista(productImageList);
					return productImage;
				}
			}
			throw new RuntimeException("ProductImage not found " + productImage.getId());
		}
	}

	public void deleteProductImage(ProductImage productImage) {
		if(productImage.getId() != null) {
			int size = productImageList.size();
			for(int i=0; i<size; i++) {
				ProductImage t = productImageList.get(i);
				if(t.getId().equals(productImage.getId())){
					productImageList.remove(i);
					serializator.grabarLista(productImageList);
					return;
				}
			}
		}
	}
	
	public synchronized void deleteAll(Integer idProduct) {
		List<ProductImage> listDelete = findbyProductId(idProduct);
		for(ProductImage delete:listDelete) {
			deleteProductImage(delete);
		}
	}
	
	private synchronized Integer getNewId() {
		Integer lastId = 0;
		for(int i=0; i<productImageList.size(); i++) {
			ProductImage aux = productImageList.get(i);
			if(lastId < aux.getId()){
				lastId = aux.getId();
			}
		}
		return lastId + 1;
	}
}
