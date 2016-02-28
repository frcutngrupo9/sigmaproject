package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.util.Date;

public class ProductionPlanState implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	Integer idProductionPlan;
	Integer idProductionPlanStateType;
	Date date;

	public ProductionPlanState(Integer idProductionPlan, Integer idProductionPlanStateType, Date date) {
		this.idProductionPlan = idProductionPlan;
		this.idProductionPlanStateType = idProductionPlanStateType;
		this.date = date;
	}

	public Integer getIdProductionPlan() {
		return idProductionPlan;
	}

	public void setIdProductionPlan(Integer idProductionPlan) {
		this.idProductionPlan = idProductionPlan;
	}

	public Integer getIdProductionPlanStateType() {
		return idProductionPlanStateType;
	}

	public void setIdProductionPlanStateType(Integer idProductionPlanStateType) {
		this.idProductionPlanStateType = idProductionPlanStateType;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductionPlanState other = (ProductionPlanState) obj;
		if (idProductionPlan != null && idProductionPlanStateType != null) {
			if (other.idProductionPlan != null && other.idProductionPlanStateType != null) {
				if (other.idProductionPlan == idProductionPlan && other.idProductionPlanStateType == idProductionPlanStateType)
					return true;
			}
		}
		return false;
	}

	public static ProductionPlanState clone(ProductionPlanState other) {
		try {
			return (ProductionPlanState) other.clone();
		} catch (CloneNotSupportedException e) {
			// not possible
		}
		return null;
	}
}
