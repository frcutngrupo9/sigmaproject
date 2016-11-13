package ar.edu.utn.sigmaproject.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class WoodReserved implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@OneToOne
	RawMaterialRequirement rawMaterialRequirement;

	BigDecimal stockReserved = BigDecimal.ZERO;
	
	boolean isWithdrawn = false;

	public WoodReserved() {

	}

	public WoodReserved(RawMaterialRequirement rawMaterialRequirement, BigDecimal stockReserved) {
		this.rawMaterialRequirement = rawMaterialRequirement;
		this.stockReserved = stockReserved;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public RawMaterialRequirement getRawMaterialRequirement() {
		return rawMaterialRequirement;
	}

	public void setRawMaterialRequirement(RawMaterialRequirement rawMaterialRequirement) {
		this.rawMaterialRequirement = rawMaterialRequirement;
	}

	public BigDecimal getStockReserved() {
		return stockReserved;
	}

	public void setStockReserved(BigDecimal stockReserved) {
		this.stockReserved = stockReserved;
	}

	public boolean isWithdrawn() {
		return isWithdrawn;
	}

	public void setWithdrawn(boolean isWithdrawn) {
		this.isWithdrawn = isWithdrawn;
	}
}
