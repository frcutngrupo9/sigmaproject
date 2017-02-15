package ar.edu.utn.sigmaproject.domain;

import ar.edu.utn.sigmaproject.domain.indexbridge.RawMaterialTypeFieldsClassBridge;
import org.hibernate.search.annotations.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

@Indexed
@ClassBridge(
		name = "measure",
		store = Store.YES,
		analyzer = @Analyzer(definition = "edge_ngram"),
		impl = RawMaterialTypeFieldsClassBridge.class)
@Entity
@Analyzer(definition = "edge_ngram")
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
	MeasureUnit widthMeasureUnit;

	@ContainedIn
	@OneToMany(mappedBy = "rawMaterialType")
	Set<Wood> woods = new HashSet<>();

	@Field
	String name = "";

	BigDecimal length = BigDecimal.ZERO;
	BigDecimal depth = BigDecimal.ZERO;
	BigDecimal width = BigDecimal.ZERO;

	public RawMaterialType() {

	}

	public RawMaterialType(String name, BigDecimal length, MeasureUnit lengthMeasureUnit, BigDecimal depth, MeasureUnit depthMeasureUnit, BigDecimal width, MeasureUnit widthMeasureUnit) {
		this.name = name;
		this.length = length;
		this.lengthMeasureUnit = lengthMeasureUnit;
		this.depth = depth;
		this.depthMeasureUnit = depthMeasureUnit;
		this.width = width;
		this.widthMeasureUnit = widthMeasureUnit;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public MeasureUnit getWidthMeasureUnit() {
		return widthMeasureUnit;
	}

	public void setWidthMeasureUnit(MeasureUnit widthMeasureUnit) {
		this.widthMeasureUnit = widthMeasureUnit;
	}

	public Set<Wood> getWoods() {
		return woods;
	}

	public void setWoods(Set<Wood> woods) {
		this.woods = woods;
	}

	public String getFormattedMeasure() {
		String lengthText = "(L) " + length.doubleValue() + " " + lengthMeasureUnit.getShortName();
		String depthText = "(E) " + depth.doubleValue() + " " + depthMeasureUnit.getShortName();
		String widthText = "(A) " + width.doubleValue() + " " + widthMeasureUnit.getShortName();
		return lengthText + " x " + depthText + " x " + widthText;
	}
}