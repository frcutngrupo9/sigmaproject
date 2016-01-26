package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
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
	
	@ManyToOne
	Product product;
	
	@OneToMany(mappedBy = "piece", cascade = CascadeType.ALL)
	List<Process> processes = new ArrayList<Process>();
	
	@ManyToOne
	MeasureUnit measureUnit;
	
	String name = "";
	BigDecimal height = BigDecimal.ZERO;
	BigDecimal width = BigDecimal.ZERO;
	BigDecimal depth = BigDecimal.ZERO;
	BigDecimal size1 = BigDecimal.ZERO;
	BigDecimal size2 = BigDecimal.ZERO;
	boolean isGroup;
	Integer units = 0;
	
	public Piece() {
		
	}

	public Piece(Product product, String name, MeasureUnit measureUnit, BigDecimal height, BigDecimal width, BigDecimal depth, BigDecimal size1, BigDecimal size2, boolean isGroup, Integer units) {
		this.product = product;
		this.name = name;
		this.measureUnit = measureUnit;
		this.height = height;
		this.width = width;
		this.depth = depth;
		this.size1 = size1;
		this.size2 = size2;
		this.isGroup = isGroup;
		this.units = units;
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

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public MeasureUnit getMeasureUnit() {
		return measureUnit;
	}

	public void setMeasureUnit(MeasureUnit measureUnit) {
		this.measureUnit = measureUnit;
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

	public BigDecimal getWidth() {
		return width;
	}

	public void setWidth(BigDecimal width) {
		this.width = width;
	}

	public BigDecimal getDepth() {
		return depth;
	}

	public void setDepth(BigDecimal depth) {
		this.depth = depth;
	}

	public BigDecimal getSize1() {
		return size1;
	}

	public void setSize1(BigDecimal size1) {
		this.size1 = size1;
	}

	public BigDecimal getSize2() {
		return size2;
	}

	public void setSize2(BigDecimal size2) {
		this.size2 = size2;
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Piece)) {
			return false;
		}
		Piece other = (Piece) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
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