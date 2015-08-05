package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class ProductionPlanDetail implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	Integer idProductionPlan;
	Integer idProduct;
	Integer units;

	public ProductionPlanDetail(Integer idProductionPlan, Integer idProduct, Integer units) {
		this.idProductionPlan = idProductionPlan;
		this.idProduct = idProduct;
		this.units = units;
	}
	
	public Integer getIdProductionPlan() {
		return idProductionPlan;
	}


	public void setIdProductionPlan(Integer idProductionPlan) {
		this.idProductionPlan = idProductionPlan;
	}


	public Integer getIdProduct() {
		return idProduct;
	}


	public void setIdProduct(Integer idProduct) {
		this.idProduct = idProduct;
	}


	public Integer getUnits() {
		return units;
	}


	public void setUnits(Integer units) {
		this.units = units;
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
		if (idProductionPlan != null && idProduct != null) {
			if (other.idProductionPlan != null && other.idProduct != null) {
				if (other.idProductionPlan.compareTo(idProductionPlan) ==  0 && other.idProduct.compareTo(idProduct) == 0)
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

