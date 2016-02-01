package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class RequirementPlanDetailSupply implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	Integer idRequirementPlan;
	Integer idSupplyType;
	Double quantity;
    boolean isFulfilled;

	public RequirementPlanDetailSupply(Integer idRequirementPlan, Integer idSupplyType, Double quantity) {
		this.idRequirementPlan = idRequirementPlan;
		this.idSupplyType = idSupplyType;
		this.quantity = quantity;
		isFulfilled = false;
	}
	
	public Integer getIdRequirementPlan() {
		return idRequirementPlan;
	}

	public void setIdRequirementPlan(Integer idRequirementPlan) {
		this.idRequirementPlan = idRequirementPlan;
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
		RequirementPlanDetailSupply other = (RequirementPlanDetailSupply) obj;
		if (idRequirementPlan != null && idSupplyType != null) {
			if (other.idRequirementPlan != null  && other.idSupplyType != null) {
				if (other.idRequirementPlan.equals(idRequirementPlan) && other.idSupplyType.equals(idSupplyType))
					return true;
			}
		}
		return false;
	}

	public static RequirementPlanDetailSupply clone(RequirementPlanDetailSupply other) {
		try {
			return (RequirementPlanDetailSupply) other.clone();
		} catch (CloneNotSupportedException e) {
			// not possible
		}
		return null;
	}
	
}
