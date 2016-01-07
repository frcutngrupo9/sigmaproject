package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class Piece implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	Integer id;
	Integer idProduct;
	String name;
	BigDecimal length;
	Integer lengthIdMeasureUnit;
	BigDecimal depth;
	Integer depthIdMeasureUnit;
	BigDecimal width;
	Integer widthIdMeasureUnit;
	String size;
	boolean isGroup;
	Integer units;

	public Piece(Integer id, Integer idProduct, String name, BigDecimal length, Integer lengthIdMeasureUnit, BigDecimal depth, Integer depthIdMeasureUnit, BigDecimal width, Integer widthIdMeasureUnit, String size, boolean isGroup, Integer units) {
		this.id = id;
		this.idProduct = idProduct;
		this.name = name;
		this.length = length;
		this.lengthIdMeasureUnit = lengthIdMeasureUnit;
		this.depth = depth;
		this.depthIdMeasureUnit = depthIdMeasureUnit;
		this.width = width;
		this.widthIdMeasureUnit = widthIdMeasureUnit;
		this.size = size;
		this.isGroup = isGroup;
		this.units = units;
	}

	public Integer getId() {
		return id;
	}
	
	public Integer getIdProduct() {
		return idProduct;
	}

	public void setIdProduct(Integer idProduct) {
		this.idProduct = idProduct;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public BigDecimal getWidth() {
		return width;
	}

	public void setWidth(BigDecimal width) {
		this.width = width;
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

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public boolean isGroup() {
		return isGroup;
	}

	public void setGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}

	public Integer getUnits() {
		return units;
	}

	public void setUnits(Integer units) {
		this.units = units;
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
		Piece other = (Piece) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public static Piece clone(Piece piece){
		try {
			return (Piece)piece.clone();
		} catch (CloneNotSupportedException e) {
			//not possible
		}
		return null;
	}
}