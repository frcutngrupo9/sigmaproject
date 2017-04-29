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
	Wood wood;

	BigDecimal quantity = BigDecimal.ZERO;
	BigDecimal quantityUsed = BigDecimal.ZERO;
	String observation = "";
	
	public ProductionOrderRawMaterial() {

	}
	
	public ProductionOrderRawMaterial(Wood wood, BigDecimal quantity) {
		this.wood = wood;
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

	public String getObservation() {
		return observation;
	}

	public void setObservation(String observation) {
		this.observation = observation;
	}
}
