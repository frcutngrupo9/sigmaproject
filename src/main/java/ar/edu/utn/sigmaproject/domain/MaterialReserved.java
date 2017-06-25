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
import javax.persistence.OneToOne;

@Entity
public class MaterialReserved implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@ManyToOne(targetEntity = Item.class)
	private Item item = null;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, updatable = false)
	private MaterialType type;

	@OneToOne(targetEntity = MaterialRequirement.class)
	MaterialRequirement materialRequirement;

	BigDecimal stockReserved = BigDecimal.ZERO;

	public MaterialReserved() {

	}

	public MaterialReserved(MaterialRequirement materialRequirement, BigDecimal stockReserved) {
		this.item = materialRequirement.getItem();
		this.materialRequirement = materialRequirement;
		this.type = materialRequirement.getType();
		this.stockReserved = stockReserved;
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

	public MaterialRequirement getMaterialRequirement() {
		return materialRequirement;
	}

	public void setMaterialRequirement(MaterialRequirement materialRequirement) {
		this.materialRequirement = materialRequirement;
	}

	public BigDecimal getStockReserved() {
		return stockReserved;
	}

	public void setStockReserved(BigDecimal stockReserved) {
		this.stockReserved = stockReserved;
	}

	public MaterialType getType() {
		return type;
	}

	public void setType(MaterialType type) {
		this.type = type;
	}
}
