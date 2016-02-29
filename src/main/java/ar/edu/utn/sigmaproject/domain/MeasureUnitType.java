package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class MeasureUnitType  implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	Integer id;
	String name;

	public MeasureUnitType(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		MeasureUnitType other = (MeasureUnitType) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public static MeasureUnitType clone(MeasureUnitType measureUnitType){
		try {
			return (MeasureUnitType)measureUnitType.clone();
		} catch (CloneNotSupportedException e) {
			//not possible
		}
		return null;
	}
}
