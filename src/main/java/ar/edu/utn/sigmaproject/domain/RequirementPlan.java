package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.util.Date;

public class RequirementPlan implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	Integer id;
	Integer idProductionPlan;
	Date fulfilledDate;

	public RequirementPlan(Integer id, Integer idProductionPlan, Date fulfilledDate) {
		this.id = id;
		this.idProductionPlan = idProductionPlan;
		this.fulfilledDate = fulfilledDate;
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

	public Date getFulfilledDate() {
		return fulfilledDate;
	}

	public void setFulfilledDate(Date fulfilledDate) {
		this.fulfilledDate = fulfilledDate;
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
		RequirementPlan other = (RequirementPlan) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public static RequirementPlan clone(RequirementPlan other){
		try {
			return (RequirementPlan)other.clone();
		} catch (CloneNotSupportedException e) {
			//not possible
		}
		return null;
	}
}
