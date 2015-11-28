package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class RawMaterial  implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    
    Integer idProduct;
    Integer idRawMaterialType;
    Double quantity;
    
    public RawMaterial(Integer idProduct, Integer idRawMaterialType, Double quantity) {
    	this.idProduct = idProduct;
        this.idRawMaterialType = idRawMaterialType;
        this.quantity = quantity;
    }
    
    public Integer getIdProduct() {
		return idProduct;
	}

	public void setIdProduct(Integer idProduct) {
		this.idProduct = idProduct;
	}

	public Integer getIdRawMaterialType() {
		return idRawMaterialType;
	}

	public void setIdRawMaterialType(Integer idRawMaterialType) {
		this.idRawMaterialType = idRawMaterialType;
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
		RawMaterial other = (RawMaterial) obj;
		if (idProduct != null && idRawMaterialType != null) {
			if (other.idProduct != null  && other.idRawMaterialType != null) {
				if (other.idProduct.equals(idProduct) && other.idRawMaterialType.equals(idRawMaterialType))
					return true;
			}
		}
		return false;
	}

	public static RawMaterial clone(RawMaterial rawMaterial) {
		try {
			return (RawMaterial) rawMaterial.clone();
		} catch (CloneNotSupportedException e) {
			// not possible
		}
		return null;
	}
}
