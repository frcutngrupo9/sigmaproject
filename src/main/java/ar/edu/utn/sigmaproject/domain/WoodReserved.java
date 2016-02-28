package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class WoodReserved  implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	Integer idWood;
	Double stock;

	public WoodReserved(Integer idWood, Double stock) {
		this.idWood = idWood;
		this.stock = stock;
	}

	public Integer getIdWood() {
		return idWood;
	}

	public void setIdWood(Integer idWood) {
		this.idWood = idWood;
	}

	public Double getStock() {
		return stock;
	}

	public void setStock(Double stock) {
		this.stock = stock;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idWood == null) ? 0 : idWood.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WoodReserved other = (WoodReserved) obj;
		if (idWood == null) {
			if (other.idWood != null)
				return false;
		} else if (!idWood.equals(other.idWood))
			return false;
		return true;
	}

	public static WoodReserved clone(WoodReserved other){
		try {
			return (WoodReserved)other.clone();
		} catch (CloneNotSupportedException e) {
			//not possible
		}
		return null;
	}
}
