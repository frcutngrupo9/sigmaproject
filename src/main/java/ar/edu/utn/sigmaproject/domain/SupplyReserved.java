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

	@ManyToOne
	SupplyRequirement supplyRequirement;

	BigDecimal stockReserved = BigDecimal.ZERO;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SupplyReserved that = (SupplyReserved) o;

		return id.equals(that.id);

	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	public static SupplyReserved clone(SupplyReserved supplyReserved){
		try {
			return (SupplyReserved)supplyReserved.clone();
		} catch (CloneNotSupportedException e) {
			//not possible
		}
		return null;
	}

}
