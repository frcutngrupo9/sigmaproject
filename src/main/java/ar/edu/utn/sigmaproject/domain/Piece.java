package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Piece implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@OneToMany(orphanRemoval = true)
	List<Process> processes = new ArrayList<>();

	String name = "";
	
	BigDecimal length = BigDecimal.ZERO;

	@ManyToOne
	MeasureUnit lengthMeasureUnit;

	BigDecimal depth = BigDecimal.ZERO;

	@ManyToOne
	MeasureUnit depthMeasureUnit;

	BigDecimal width = BigDecimal.ZERO;

	@ManyToOne
	MeasureUnit widthMeasureUnit;

	String size = "";
	boolean isGroup;
	Integer units = 0;

	boolean isClone;

	public Piece() {

	}

	public Piece(String name, BigDecimal length, MeasureUnit lengthMeasureUnit, BigDecimal depth, MeasureUnit depthMeasureUnit, BigDecimal width, MeasureUnit widthMeasureUnit, String size, boolean isGroup, Integer units) {
		this.name = name;
		this.length = length;
		this.lengthMeasureUnit = lengthMeasureUnit;
		this.width = width;
		this.widthMeasureUnit = widthMeasureUnit;
		this.depth = depth;
		this.depthMeasureUnit = depthMeasureUnit;
		this.size = size;
		this.isGroup = isGroup;
		this.units = units;
		isClone = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Process> getProcesses() {
		return processes;
	}

	public void setProcesses(List<Process> processes) {
		this.processes = processes;
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

	public MeasureUnit getLengthMeasureUnit() {
		return this.lengthMeasureUnit;
	}

	public void setLengthMeasureUnit(MeasureUnit lengthMeasureUnit) {
		this.lengthMeasureUnit = lengthMeasureUnit;
	}

	public BigDecimal getDepth() {
		return depth;
	}

	public void setDepth(BigDecimal depth) {
		this.depth = depth;
	}

	public MeasureUnit getDepthMeasureUnit() {
		return this.depthMeasureUnit;
	}

	public void setDepthMeasureUnit(MeasureUnit depthMeasureUnit) {
		this.depthMeasureUnit = depthMeasureUnit;
	}

	public BigDecimal getWidth() {
		return width;
	}

	public void setWidth(BigDecimal width) {
		this.width = width;
	}

	public MeasureUnit getWidthMeasureUnit() {
		return this.widthMeasureUnit;
	}

	public void setWidthMeasureUnit(MeasureUnit widthMeasureUnit) {
		this.widthMeasureUnit = widthMeasureUnit;
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

	public boolean isClone() {
		return isClone;
	}

	public void setClone(boolean isClone) {
		this.isClone = isClone;
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