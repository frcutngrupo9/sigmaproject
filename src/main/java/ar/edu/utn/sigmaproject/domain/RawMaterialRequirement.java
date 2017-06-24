package ar.edu.utn.sigmaproject.domain;

import javax.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class RawMaterialRequirement implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Wood wood;
	
	@ManyToOne(targetEntity = ProductionPlan.class)
	private ProductionPlan productionPlan = null;

	private BigDecimal quantity = BigDecimal.ZERO;
	private BigDecimal quantityWithdrawn = BigDecimal.ZERO;

	public RawMaterialRequirement() {

	}

	public RawMaterialRequirement(Wood wood, ProductionPlan productionPlan, BigDecimal quantity) {
		this.wood = wood;
		this.productionPlan = productionPlan;
		this.quantity = quantity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Wood getWood() {
		return wood;
	}

	public void setWood(Wood wood) {
		this.wood = wood;
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
