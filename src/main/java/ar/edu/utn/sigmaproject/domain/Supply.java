package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class Supply implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    
    Integer idProduct;
    Integer idSupplyType;
    Double quantity;
    
    public Supply(Integer idProduct, Integer idSupplyType, Double quantity) {
    	this.idProduct = idProduct;
        this.idSupplyType = idSupplyType;
        this.quantity = quantity;
    }
    
    public Integer getIdProduct() {
		return idProduct;
	}

	public void setIdProduct(Integer idProduct) {
		this.idProduct = idProduct;
	}

	public Integer getIdSupplyType() {
		return idSupplyType;
	}

	public void setIdSupplyType(Integer idSupplyType) {
		this.idSupplyType = idSupplyType;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Supply other = (Supply) obj;
		if (idProduct != null && idSupplyType != null) {
			if (other.idProduct != null  && other.idSupplyType != null) {
				if (other.idProduct.equals(idProduct) && other.idSupplyType.equals(idSupplyType))
					return true;
			}
		}
		return false;
	}

	public static Supply clone(Supply supply) {
		try {
			return (Supply) supply.clone();
		} catch (CloneNotSupportedException e) {
			// not possible
		}
		return null;
	}
}
