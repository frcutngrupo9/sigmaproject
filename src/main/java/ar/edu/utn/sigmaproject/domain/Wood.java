package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class Wood implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	Integer id;
	Integer idRawMaterialType;
	Integer idWoodType;
	Double stock;
	Double stockMin;
	Double stockRepo;

	public Wood(Integer id, Integer idRawMaterialType, Integer idWoodType, Double stock, Double stockMin, Double stockRepo) {
		this.id = id;
		this.idRawMaterialType = idRawMaterialType;
		this.idWoodType = idWoodType;
		this.stock = stock;
		this.stockMin = stockMin;
		this.stockRepo = stockRepo;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdRawMaterialType() {
		return idRawMaterialType;
	}

	public void setIdRawMaterialType(Integer idRawMaterialType) {
		this.idRawMaterialType = idRawMaterialType;
	}

	public Integer getIdWoodType() {
		return idWoodType;
	}

	public void setIdWoodType(Integer idWoodType) {
		this.idWoodType = idWoodType;
	}

	public Double getStock() {
		return stock;
	}

	public void setStock(Double stock) {
		this.stock = stock;
	}

	public Double getStockMin() {
		return stockMin;
	}

	public void setStockMin(Double stockMin) {
		this.stockMin = stockMin;
	}

	public Double getStockRepo() {
		return stockRepo;
	}

	public void setStockRepo(Double stockRepo) {
		this.stockRepo = stockRepo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Wood other = (Wood) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public static Wood clone(Wood other) {
		try {
			return (Wood)other.clone();
		} catch (CloneNotSupportedException e) {
			//not possible
		}
		return null;
	}
}
