package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ProductionPlanState implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	@ManyToOne
	ProductionPlan productionPlan;
	
	@ManyToOne
	ProductionPlanStateType productionPlanStateType;
	
	Date date;
	
	public ProductionPlanState() {
		
	}
	
	public ProductionPlanState(ProductionPlan productionPlan, ProductionPlanStateType productionPlanStateType, Date date) {
		this.productionPlan = productionPlan;
		this.productionPlanStateType = productionPlanStateType;
		this.date = date;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ProductionPlan getProductionPlan() {
		return productionPlan;
	}

	public void setProductionPlan(ProductionPlan productionPlan) {
		this.productionPlan = productionPlan;
	}

	public ProductionPlanStateType getProductionPlanStateType() {
		return productionPlanStateType;
	}

	public void setProductionPlanStateType(ProductionPlanStateType productionPlanStateType) {
		this.productionPlanStateType = productionPlanStateType;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ProductionPlanState)) {
			return false;
		}
		ProductionPlanState other = (ProductionPlanState) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
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
