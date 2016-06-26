package ar.edu.utn.sigmaproject.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class RawMaterialRequirement implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@ManyToOne
	RawMaterialType rawMaterialType;

	BigDecimal quantity = BigDecimal.ZERO;

	boolean isFulfilled;

	public RawMaterialRequirement() {

	}

	public RawMaterialRequirement(RawMaterialType rawMaterialType, BigDecimal quantity) {
		this.rawMaterialType = rawMaterialType;
		this.quantity = quantity;
		isFulfilled = false;
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

	public boolean isFulfilled() {
		return isFulfilled;
	}

	public void setFulfilled(boolean isFulfilled) {
		this.isFulfilled = isFulfilled;
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
		RawMaterialRequirement other = (RawMaterialRequirement) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public static RawMaterialRequirement clone(RawMaterialRequirement obj){
		try {
			return (RawMaterialRequirement)obj.clone();
		} catch (CloneNotSupportedException e) {
			//not possible
		}
		return null;
	}
}
