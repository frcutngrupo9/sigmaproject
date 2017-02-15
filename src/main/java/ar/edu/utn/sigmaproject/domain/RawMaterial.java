package ar.edu.utn.sigmaproject.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class RawMaterial implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@ManyToOne
	RawMaterialType rawMaterialType;

	BigDecimal quantity = BigDecimal.ZERO;
	boolean isClone;

	public RawMaterial() {

	}

	public RawMaterial(RawMaterialType rawMaterialType, BigDecimal quantity) {
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
}
