package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class RawMaterial implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	Integer id;
	Integer idMeasureUnit;
	String name;
	Long length;
	Long depth;
	Long height;

	public RawMaterial(Integer id, Integer idMeasureUnit, String name, Long length, Long depth, Long height) {
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

	public Long getHeight() {
		return height;
	}

	public void setHeight(Long height) {
		this.height = height;
	}

	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public Long getDepth() {
		return depth;
	}

	public void setDepth(Long depth) {
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
		RawMaterial other = (RawMaterial) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public static RawMaterial clone(RawMaterial rawMaterial){
		try {
			return (RawMaterial)rawMaterial.clone();
		} catch (CloneNotSupportedException e) {
			//not possible
		}
		return null;
	}
}