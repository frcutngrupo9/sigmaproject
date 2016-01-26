package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class RawMaterialType implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	@ManyToOne
	MeasureUnit lengthMeasureUnit;
	
	@ManyToOne
	MeasureUnit depthMeasureUnit;
	
	@ManyToOne
	MeasureUnit heightMeasureUnit;
	
	String name;
	BigDecimal length = BigDecimal.ZERO;
	BigDecimal depth = BigDecimal.ZERO;
	BigDecimal height = BigDecimal.ZERO;
	

	public RawMaterialType(String name, BigDecimal length, MeasureUnit lengthMeasureUnit, BigDecimal depth, MeasureUnit depthMeasureUnit, BigDecimal height, MeasureUnit heightMeasureUnit) {
		this.name = name;
		this.length = length;
		this.lengthMeasureUnit = lengthMeasureUnit;
		this.depth = depth;
		this.depthMeasureUnit = depthMeasureUnit;
		this.height = height;
		this.heightMeasureUnit = heightMeasureUnit;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public MeasureUnit getLengthMeasureUnit() {
		return lengthMeasureUnit;
	}

	public void setLengthMeasureUnit(MeasureUnit lengthMeasureUnit) {
		this.lengthMeasureUnit = lengthMeasureUnit;
	}

	public MeasureUnit getDepthMeasureUnit() {
		return depthMeasureUnit;
	}

	public void setDepthMeasureUnit(MeasureUnit depthMeasureUnit) {
		this.depthMeasureUnit = depthMeasureUnit;
	}

	public MeasureUnit getHeightMeasureUnit() {
		return heightMeasureUnit;
	}

	public void setHeightMeasureUnit(MeasureUnit heightMeasureUnit) {
		this.heightMeasureUnit = heightMeasureUnit;
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
	
	public static RawMaterialType clone(RawMaterialType rawMaterialType){
		try {
			return (RawMaterialType)rawMaterialType.clone();
		} catch (CloneNotSupportedException e) {
			//not possible
		}
		return null;
	}
}