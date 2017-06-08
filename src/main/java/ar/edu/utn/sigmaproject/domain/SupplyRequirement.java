package ar.edu.utn.sigmaproject.domain;

import javax.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class SupplyRequirement implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private SupplyType supplyType;
	
	@ManyToOne(targetEntity = ProductionPlan.class)
	private ProductionPlan productionPlan = null;

	private BigDecimal quantity = BigDecimal.ZERO;
	private BigDecimal quantityWithdrawn = BigDecimal.ZERO;

	public SupplyRequirement() {

	}

	public SupplyRequirement(SupplyType supplyType, ProductionPlan productionPlan, BigDecimal quantity) {
		this.supplyType = supplyType;
		this.productionPlan = productionPlan;
		this.quantity = quantity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SupplyType getSupplyType() {
		return supplyType;
	}

	public void setSupplyType(SupplyType supplyType) {
		this.supplyType = supplyType;
	}

	public ProductionPlan getProductionPlan() {
		return productionPlan;
	}

	public void setProductionPlan(ProductionPlan productionPlan) {
		this.productionPlan = productionPlan;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getQuantityWithdrawn() {
		return quantityWithdrawn;
	}

	public void setQuantityWithdrawn(BigDecimal quantityWithdrawn) {
		this.quantityWithdrawn = quantityWithdrawn;
	}
	
	public BigDecimal getQuantityNotWithdrawn() {
		// es la cantidad que aun no ha sido retirada
		return quantity.subtract(quantityWithdrawn);
	}
}
