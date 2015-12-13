package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.Category;

public interface CategoryService {

	List<Category> getCategoryList();
	
	List<Category> getCategoryList(Integer idProduct);
	
	Category getCategory(Integer idProduct, Integer idCategoryType);

	Category saveCategory(Category category);

	Category updateCategory(Category category);

	void deleteCategory(Category category);
	
	void deleteAll(Integer idProduct);

}
