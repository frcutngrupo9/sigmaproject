package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class RequirementPlanDetailRawMaterial implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	Integer idRequirementPlan;
	Integer idRawMaterialType;
	Double quantity;
    boolean isFulfilled;

	public RequirementPlanDetailRawMaterial(Integer idRequirementPlan, Integer idRawMaterialType, Double quantity) {
		this.idRequirementPlan = idRequirementPlan;
		this.idRawMaterialType = idRawMaterialType;
		this.quantity = quantity;
		isFulfilled = false;
	}
	
	public Integer getIdRequirementPlan() {
		return idRequirementPlan;
	}

	public void setIdRequirementPlan(Integer idRequirementPlan) {
		this.idRequirementPlan = idRequirementPlan;
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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RequirementPlanDetailRawMaterial other = (RequirementPlanDetailRawMaterial) obj;
		if (idRequirementPlan != null && idRawMaterialType != null) {
			if (other.idRequirementPlan != null  && other.idRawMaterialType != null) {
				if (other.idRequirementPlan.equals(idRequirementPlan) && other.idRawMaterialType.equals(idRawMaterialType))
					return true;
			}
		}
		return false;
	}

	public static RequirementPlanDetailRawMaterial clone(RequirementPlanDetailRawMaterial other) {
		try {
			return (RequirementPlanDetailRawMaterial) other.clone();
		} catch (CloneNotSupportedException e) {
			// not possible
		}
		return null;
	}
}
