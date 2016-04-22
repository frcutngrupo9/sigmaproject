package ar.edu.utn.sigmaproject.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class RawMaterial  implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@ManyToOne
	Product product;

	@ManyToOne
	RawMaterialType rawMaterialType;

	BigDecimal quantity = BigDecimal.ZERO
			;
	boolean isClone;

	public RawMaterial() {

	}

	public RawMaterial(Product product, RawMaterialType rawMaterialType, BigDecimal quantity) {
		this.product = product;
		this.rawMaterialType = rawMaterialType;
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
		RawMaterial other = (RawMaterial) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public static RawMaterial clone(RawMaterial rawMaterial) {
		try {
			return (RawMaterial) rawMaterial.clone();
		} catch (CloneNotSupportedException e) {
			// not possible
		}
		return null;
	}
}
