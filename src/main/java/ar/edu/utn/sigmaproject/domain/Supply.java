package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class Supply implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	Integer id;
	Integer idProduct;
	Integer idSupplyType;
	Double quantity;
	boolean isClone;

	public Supply(Integer id, Integer idProduct, Integer idSupplyType, Double quantity) {
		this.id = id;
		this.idProduct = idProduct;
		this.idSupplyType = idSupplyType;
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
		Supply other = (Supply) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
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
