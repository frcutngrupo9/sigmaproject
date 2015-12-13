package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.CategoryType;
import ar.edu.utn.sigmaproject.service.CategoryTypeService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class CategoryTypeServiceImpl implements CategoryTypeService {
    
    static List<CategoryType> categoryTypeList = new ArrayList<CategoryType>();
    private SerializationService serializator = new SerializationService("category_type");
    static int categoryTypeId = 1;
    static {
        categoryTypeList.add(new CategoryType(categoryTypeId++,"Mesa"));
        categoryTypeList.add(new CategoryType(categoryTypeId++,"Silla"));
        categoryTypeList.add(new CategoryType(categoryTypeId++,"Cama"));
        categoryTypeList.add(new CategoryType(categoryTypeId++,"Escritorio"));
        categoryTypeList.add(new CategoryType(categoryTypeId++,"Armario"));
        categoryTypeList.add(new CategoryType(categoryTypeId++,"Biblioteca"));
    }
    
    public CategoryTypeServiceImpl() {
        List<CategoryType> aux = serializator.obtenerLista();
        if(aux != null) {
            categoryTypeList = aux;
        } else {
            serializator.grabarLista(categoryTypeList);
        }
    }
    
    public synchronized List<CategoryType> getCategoryList() {
        List<CategoryType> list = new ArrayList<CategoryType>();
        for(CategoryType aux:categoryTypeList) {
            list.add(CategoryType.clone(aux));
        }
        return list;
    }
    
    public synchronized CategoryType getCategory(Integer id) {
        for(CategoryType aux:categoryTypeList) {
            if(aux.getId().equals(id)) {
                return CategoryType.clone(aux);
            }
        }
        return null;
    }
    
    public synchronized CategoryType getCategory(String name) {
        for(CategoryType aux:categoryTypeList) {
            if(aux.getName().compareToIgnoreCase(name) == 0) {
                return CategoryType.clone(aux);
            }
        }
        return null;
    }
    
    public synchronized CategoryType saveCategory(CategoryType aux) {
        aux = CategoryType.clone(aux);
        categoryTypeList.add(aux);
        serializator.grabarLista(categoryTypeList);
        return aux;
    }
    
    public synchronized CategoryType updateCategory(CategoryType aux) {
        if(aux.getId() == null) {
            throw new IllegalArgumentException("can't update a null-id Category, save it first");
        }else {
            aux = CategoryType.clone(aux);
            int size = categoryTypeList.size();
            for(int i = 0; i < size; i++) {
            	CategoryType t = categoryTypeList.get(i);
                if(t.getId().equals(aux.getId())) {
                    categoryTypeList.set(i, aux);
                    serializator.grabarLista(categoryTypeList);
                    return aux;
                }
            }
            throw new RuntimeException("Category not found " + aux.getId());
        }
    }
    
    public synchronized void deleteCategory(CategoryType aux) {
        if(aux.getId()!=null) {
            int size = categoryTypeList.size();
            for(int i=0;i<size;i++) {
            	CategoryType t = categoryTypeList.get(i);
                if(t.getId().equals(aux.getId())) {
                    categoryTypeList.remove(i);
                    serializator.grabarLista(categoryTypeList);
                    return;
                }
            }
        }
    }
    
    public synchronized Integer getNewId() {
        Integer lastId = 0;
        for(int i=0;i<categoryTypeList.size();i++){
        	CategoryType aux = categoryTypeList.get(i);
            if(lastId < aux.getId()) {
                lastId = aux.getId();
            }
        }
        return lastId + 1;
    }
   
}
