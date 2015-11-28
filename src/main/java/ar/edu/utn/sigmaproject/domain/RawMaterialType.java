package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class RawMaterialType implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	Integer id;
	Integer idMeasureUnit;
	String name;
	BigDecimal length;
	BigDecimal depth;
	BigDecimal height;

	public RawMaterialType(Integer id, Integer idMeasureUnit, String name, BigDecimal length, BigDecimal depth, BigDecimal height) {
		this.id = id;
		this.idMeasureUnit = idMeasureUnit;
		this.name = name;
		this.length = length;
		this.depth = depth;
		this.height = height;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getIdMeasureUnit() {
		return idMeasureUnit;
	}

	public void setIdMeasureUnit(Integer idMeasureUnit) {
		this.idMeasureUnit = idMeasureUnit;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getHeight() {
		return height;
	}

	public void setHeight(BigDecimal height) {
		this.height = height;
	}

	public BigDecimal getLength() {
		return length;
	}

	public void setLength(BigDecimal length) {
		this.length = length;
	}

	public BigDecimal getDepth() {
		return depth;
	}

	public void setDepth(BigDecimal depth) {
		this.depth = depth;
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
		RawMaterialType other = (RawMaterialType) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public static RawMaterialType clone(RawMaterialType rawMaterial){
		try {
			return (RawMaterialType)rawMaterial.clone();
		} catch (CloneNotSupportedException e) {
			//not possible
		}
		return null;
	}
}