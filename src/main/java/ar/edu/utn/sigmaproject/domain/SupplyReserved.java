package ar.edu.utn.sigmaproject.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class SupplyReserved implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@OneToOne
	SupplyRequirement supplyRequirement;

	BigDecimal stockReserved = BigDecimal.ZERO;

	boolean isWithdrawn = false;

	public SupplyReserved() {

	}

	public SupplyReserved(SupplyRequirement supplyRequirement, BigDecimal stockReserved) {
		this.supplyRequirement = supplyRequirement;
		this.stockReserved = stockReserved;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SupplyRequirement getSupplyRequirement() {
		return supplyRequirement;
	}

	public void setSupplyRequirement(SupplyRequirement supplyRequirement) {
		this.supplyRequirement = supplyRequirement;
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
