package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class ProductionPlanDetail implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	Integer idProductionPlan;
	Integer idOrder;

	public ProductionPlanDetail(Integer idProductionPlan, Integer idOrder) {
		this.idProductionPlan = idProductionPlan;
		this.idOrder = idOrder;
	}
	
	public Integer getIdProductionPlan() {
		return idProductionPlan;
	}


	public void setIdProductionPlan(Integer idProductionPlan) {
		this.idProductionPlan = idProductionPlan;
	}

	public Integer getIdOrder() {
		return idOrder;
	}


	public void setIdOrder(Integer idOrder) {
		this.idOrder = idOrder;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductionPlanDetail other = (ProductionPlanDetail) obj;
		if (idProductionPlan != null && idOrder != null) {
			if (other.idProductionPlan != null  && other.idOrder != null) {
				if (other.idProductionPlan.equals(idProductionPlan) && other.idOrder.equals(idOrder))
					return true;
			}
		}
		return false;
	}

	public static ProductionPlanDetail clone(ProductionPlanDetail productionPlanDetail) {
		try {
			return (ProductionPlanDetail) productionPlanDetail.clone();
		} catch (CloneNotSupportedException e) {
			// not possible
		}
		return null;
	}
	
}

