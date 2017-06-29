package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
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
	ProductionPlanStateType productionPlanStateType;

	@Column(nullable = false)
	Date date = new Date();

	public ProductionPlanState() {

	}

	public ProductionPlanState(ProductionPlanStateType productionPlanStateType, Date date) {
		this.productionPlanStateType = productionPlanStateType;
		this.date = date;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
}
