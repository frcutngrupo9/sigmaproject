package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.Category;
import ar.edu.utn.sigmaproject.service.CategoryService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class CategoryServiceImpl implements CategoryService {
	static List<Category> categoryList = new ArrayList<Category>();
	private SerializationService serializator = new SerializationService("category");

	public CategoryServiceImpl() {
		@SuppressWarnings("unchecked")
		List<Category> aux = serializator.obtenerLista();
		if(aux != null) {
			categoryList = aux;
		} else {
			serializator.grabarLista(categoryList);
		}
	}

	public synchronized List<Category> getCategoryList() {
		List<Category> list = new ArrayList<Category>();
		for(Category each:categoryList) {
			list.add(Category.clone(each));
		}
		return list;
	}

	public synchronized List<Category> getCategoryList(Integer idProduct) {
		List<Category> list = new ArrayList<Category>();
		for(Category each:categoryList) {
			if(each.getIdProduct().equals(idProduct)) {
				list.add(Category.clone(each));
			}
		}
		return list;
	}

	public synchronized Category getCategory(Integer idProduct, Integer idCategoryType) {
		int size = categoryList.size();
		for(int i = 0; i < size; i++) {
			Category aux = categoryList.get(i);
			if(aux.getIdProduct().equals(idProduct) && aux.getIdCategoryType().equals(idCategoryType)) {
				return Category.clone(aux);
			}
		}
		return null;
	}

	public synchronized Category saveCategory(Category category) {
		category = Category.clone(category);
		categoryList.add(category);
		serializator.grabarLista(categoryList);
		return category;
	}

	public synchronized Category updateCategory(Category category) {
		if(category.getIdProduct()==null || category.getIdCategoryType()==null) {
			throw new IllegalArgumentException("can't update a null-id Category, save it first");
		} else {
			category = Category.clone(category);
			int size = categoryList.size();
			for(int i = 0; i < size; i++) {
				Category aux = categoryList.get(i);
				if(aux.getIdProduct().equals(category.getIdProduct()) && aux.getIdCategoryType().equals(category.getIdCategoryType())) {
					categoryList.set(i, category);
					serializator.grabarLista(categoryList);
					return category;
				}
			}
			throw new RuntimeException("Category not found "+category.getIdProduct()+" "+category.getIdCategoryType());
		}
	}

	public synchronized void deleteCategory(Category category) {
		if(category.getIdProduct()!=null && category.getIdCategoryType()!=null) {
			int size = categoryList.size();
			for(int i = 0; i < size; i++) {
				Category aux = categoryList.get(i);
				if(aux.getIdProduct().equals(category.getIdProduct()) && aux.getIdCategoryType().equals(category.getIdCategoryType())) {
					categoryList.remove(i);
					serializator.grabarLista(categoryList);
					return;
				}
			}
		}
	}

	public synchronized void deleteAll(Integer idProduct) {
		List<Category> listDelete = getCategoryList(idProduct);
		for(Category delete:listDelete) {
			deleteCategory(delete);
		}
	}
}
