package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class Category implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    
    Integer idProduct;
    Integer idCategoryType;
    
    public Category(Integer idProduct, Integer idCategoryType) {
    	this.idProduct = idProduct;
        this.idCategoryType = idCategoryType;
    }
    
    public Integer getIdProduct() {
		return idProduct;
	}

	public void setIdProduct(Integer idProduct) {
		this.idProduct = idProduct;
	}

	public Integer getIdCategoryType() {
		return idCategoryType;
	}

	public void setIdCategoryType(Integer idCategoryType) {
		this.idCategoryType = idCategoryType;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
		if (idProduct != null && idCategoryType != null) {
			if (other.idProduct != null  && other.idCategoryType != null) {
				if (other.idProduct.equals(idProduct) && other.idCategoryType.equals(idCategoryType))
					return true;
			}
		}
		return false;
	}

	public static Category clone(Category other) {
		try {
			return (Category) other.clone();
		} catch (CloneNotSupportedException e) {
			// not possible
		}
		return null;
	}
}
