package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.CategoryType;

public interface CategoryTypeService {

	List<CategoryType> getCategoryList();

	CategoryType getCategory(Integer idCategoryType);

	CategoryType getCategory(String nameCategoryType);

	CategoryType saveCategory(CategoryType categoryType);

	CategoryType updateCategory(CategoryType categoryType);

	void deleteCategory(CategoryType categoryType);

}
