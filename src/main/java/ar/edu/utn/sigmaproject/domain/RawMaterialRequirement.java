package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class RawMaterialRequirement implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	Integer id;
	Integer idProductionPlan;
	Integer idRawMaterialType;
	Double quantity;
	boolean isFulfilled;

	public RawMaterialRequirement(Integer id, Integer idProductionPlan, Integer idRawMaterialType, Double quantity) {
		this.id = id;
		this.idProductionPlan = idProductionPlan;
		this.idRawMaterialType = idRawMaterialType;
		this.quantity = quantity;
		isFulfilled = false;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdProductionPlan() {
		return idProductionPlan;
	}

	public void setIdProductionPlan(Integer idProductionPlan) {
		this.idProductionPlan = idProductionPlan;
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

	public boolean isFulfilled() {
		return isFulfilled;
	}

	public void setFulfilled(boolean isFulfilled) {
		this.isFulfilled = isFulfilled;
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
		RawMaterialRequirement other = (RawMaterialRequirement) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public static RawMaterialRequirement clone(RawMaterialRequirement obj){
		try {
			return (RawMaterialRequirement)obj.clone();
		} catch (CloneNotSupportedException e) {
			//not possible
		}
		return null;
	}
}