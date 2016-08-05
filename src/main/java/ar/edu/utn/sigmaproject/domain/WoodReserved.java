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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WoodReserved other = (WoodReserved) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public static WoodReserved clone(WoodReserved other){
		try {
			return (WoodReserved)other.clone();
		} catch (CloneNotSupportedException e) {
			//not possible
		}
		return null;
	}
}
