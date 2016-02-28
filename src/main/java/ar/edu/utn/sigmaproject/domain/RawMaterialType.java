package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class RawMaterialType implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	Integer id;
	String name;
	BigDecimal length;
	Integer lengthIdMeasureUnit;
	BigDecimal depth;
	Integer depthIdMeasureUnit;
	BigDecimal width;
	Integer widthIdMeasureUnit;

	public RawMaterialType(Integer id, String name, BigDecimal length, Integer lengthIdMeasureUnit, BigDecimal depth, Integer depthIdMeasureUnit, BigDecimal width, Integer widthIdMeasureUnit) {
		this.id = id;
		this.name = name;
		this.length = length;
		this.lengthIdMeasureUnit = lengthIdMeasureUnit;
		this.depth = depth;
		this.depthIdMeasureUnit = depthIdMeasureUnit;
		this.width = width;
		this.widthIdMeasureUnit = widthIdMeasureUnit;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getWidth() {
		return width;
	}

	public void setWidth(BigDecimal width) {
		this.width = width;
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

	public Integer getLengthIdMeasureUnit() {
		return lengthIdMeasureUnit;
	}

	public void setLengthIdMeasureUnit(Integer lengthIdMeasureUnit) {
		this.lengthIdMeasureUnit = lengthIdMeasureUnit;
	}

	public Integer getDepthIdMeasureUnit() {
		return depthIdMeasureUnit;
	}

	public void setDepthIdMeasureUnit(Integer depthIdMeasureUnit) {
		this.depthIdMeasureUnit = depthIdMeasureUnit;
	}

	public Integer getWidthIdMeasureUnit() {
		return widthIdMeasureUnit;
	}

	public void setWidthIdMeasureUnit(Integer widthIdMeasureUnit) {
		this.widthIdMeasureUnit = widthIdMeasureUnit;
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

	public static RawMaterialType clone(RawMaterialType rawMaterialType){
		try {
			return (RawMaterialType)rawMaterialType.clone();
		} catch (CloneNotSupportedException e) {
			//not possible
		}
		return null;
	}
}