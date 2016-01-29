package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class RawMaterial  implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    
    Integer id;
    Integer idProduct;
    Integer idRawMaterialType;
    Double quantity;
    boolean isClone;
    
    public RawMaterial(Integer id, Integer idProduct, Integer idRawMaterialType, Double quantity) {
    	this.id = id;
    	this.idProduct = idProduct;
        this.idRawMaterialType = idRawMaterialType;
        this.quantity = quantity;
        isClone = false;
    }
    
    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public boolean isClone() {
		return isClone;
	}

	public void setClone(boolean isClone) {
		this.isClone = isClone;
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
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
