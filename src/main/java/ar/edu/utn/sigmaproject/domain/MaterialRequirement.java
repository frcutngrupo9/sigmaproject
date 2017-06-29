package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class MaterialRequirement implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	private Item item;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, updatable = false)
	private MaterialType type;

	@ManyToOne(targetEntity = ProductionPlan.class)
	private ProductionPlan productionPlan = null;

	private BigDecimal quantity = BigDecimal.ZERO;
	private BigDecimal quantityWithdrawn = BigDecimal.ZERO;

	public MaterialRequirement() {

	}

	public MaterialRequirement(Item item, MaterialType type, ProductionPlan productionPlan, BigDecimal quantity) {
		this.item = item;
		this.type = type;
		this.productionPlan = productionPlan;
		this.quantity = quantity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public MaterialType getType() {
		return type;
	}

	public void setType(MaterialType type) {
		this.type = type;
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
