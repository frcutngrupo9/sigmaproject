package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ProductionOrderMaterial implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	@ManyToOne(targetEntity = ProductionOrder.class)
	private ProductionOrder productionOrder = null;
	
	@ManyToOne(optional = false)
	private Item item;

	BigDecimal quantity = BigDecimal.ZERO;
	BigDecimal quantityUsed = BigDecimal.ZERO;
	String observation = "";
	
	public ProductionOrderMaterial() {

	}
	
	public ProductionOrderMaterial(ProductionOrder productionOrder, Item item, BigDecimal quantity) {
		this.productionOrder = productionOrder;
		this.item = item;
		this.quantity = quantity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ProductionOrder getProductionOrder() {
		return productionOrder;
	}

	public void setProductionOrder(ProductionOrder productionOrder) {
		this.productionOrder = productionOrder;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
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
