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
}
