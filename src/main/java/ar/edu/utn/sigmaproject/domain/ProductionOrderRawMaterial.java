package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ProductionOrderRawMaterial implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	@ManyToOne
	RawMaterialType rawMaterialType;

	BigDecimal quantity = BigDecimal.ZERO;
	BigDecimal quantityUsed = BigDecimal.ZERO;
	
	public ProductionOrderRawMaterial() {

	}
	
	public ProductionOrderRawMaterial(RawMaterialType rawMaterialType, BigDecimal quantity) {
		this.rawMaterialType = rawMaterialType;
		this.quantity = quantity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public RawMaterialType getRawMaterialType() {
		return rawMaterialType;
	}

	public void setRawMaterialType(RawMaterialType rawMaterialType) {
		this.rawMaterialType = rawMaterialType;
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
