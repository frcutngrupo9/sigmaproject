package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class MeasureUnit implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	String name = "";
	String shortName = "";

	@ManyToOne
	MeasureUnitType type;

	public MeasureUnit() {

	}

	public MeasureUnit(String name, String shortName, MeasureUnitType measureUnitType) {
		this.name = name;
		this.shortName = shortName;
		this.type = measureUnitType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MeasureUnitType getType() {
		return type;
	}

	public void setType(MeasureUnitType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
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
		MeasureUnit other = (MeasureUnit) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public static MeasureUnit clone(MeasureUnit measureUnit){
		try {
			return (MeasureUnit)measureUnit.clone();
		} catch (CloneNotSupportedException e) {
			//not possible
		}
		return null;
	}
}
