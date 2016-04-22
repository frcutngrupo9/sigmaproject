package ar.edu.utn.sigmaproject.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class Supply implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@ManyToOne
	Product product;

	@ManyToOne
	SupplyType supplyType;

	BigDecimal quantity = BigDecimal.ZERO;
	boolean isClone;

	public Supply() {

	}

	public Supply(Product product, SupplyType supplyType, BigDecimal quantity) {
		this.product = product;
		this.supplyType = supplyType;
		this.quantity = quantity;
		isClone = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
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

	public boolean isClone() {
		return isClone;
	}

	public void setClone(boolean isClone) {
		this.isClone = isClone;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Supply other = (Supply) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public static Supply clone(Supply supply) {
		try {
			return (Supply) supply.clone();
		} catch (CloneNotSupportedException e) {
			// not possible
		}
		return null;
	}
}
