package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ProductionOrderSupply implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	@ManyToOne
	SupplyType supplyType;

	BigDecimal quantity = BigDecimal.ZERO;
	BigDecimal quantityUsed = BigDecimal.ZERO;
	
	public ProductionOrderSupply() {

	}
	
	public ProductionOrderSupply(SupplyType supplyType, BigDecimal quantity) {
		this.supplyType = supplyType;
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

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getQuantityUsed() {
		return quantityUsed;
	}

	public void setQuantityUsed(BigDecimal quantityUsed) {
		this.quantityUsed = quantityUsed;
	}
}
