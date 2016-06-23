package ar.edu.utn.sigmaproject.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.*;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

@Entity
public class ProductionOrderDetail implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Transient
	final Logger logger = LoggerFactory.getLogger(ProductionOrderDetail.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@ManyToOne
	Process process;

	@ManyToOne
	Machine machine;

	@Transient
	Duration timeTotal;

	@Column
	String timeTotalInternal;

	Integer quantityPiece = 0;

	BigDecimal quantityFinished = BigDecimal.ZERO;

	boolean isFinished;

	public ProductionOrderDetail() {

	}

	public ProductionOrderDetail(Process process, Machine machine, Duration timeTotal, Integer quantityPiece) {
		this.process = process;
		this.machine = machine;
		this.timeTotal = timeTotal;
		this.quantityPiece = quantityPiece;
		isFinished = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public Machine getMachine() {
		return machine;
	}

	public void setMachine(Machine machine) {
		this.machine = machine;
	}

	public Duration getTimeTotal() {
		if (this.timeTotal == null && this.timeTotalInternal != null) {
			try {
				this.timeTotal = DatatypeFactory.newInstance().newDuration(this.timeTotalInternal);
			} catch (Exception e) {
				logger.error("Error while trying to deserialize Duration representation(" + this.timeTotalInternal + "): " + e.toString());
			}
		}
		return timeTotal;
	}

	public void setTimeTotal(Duration timeTotal) {
		this.timeTotal = timeTotal;
		if (timeTotal != null) {
			this.timeTotalInternal = timeTotal.toString();
		} else {
			this.timeTotalInternal = null;
		}
	}

	public Integer getQuantityPiece() {
		return quantityPiece;
	}

	public void setQuantityPiece(Integer quantityPiece) {
		this.quantityPiece = quantityPiece;
	}

	public BigDecimal getQuantityFinished() {
		return quantityFinished;
	}

	public void setQuantityFinished(BigDecimal quantityFinished) {
		this.quantityFinished = quantityFinished;
	}

	public boolean isFinished() {
		return isFinished;
	}

	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ProductionOrderDetail that = (ProductionOrderDetail) o;

		return id != null ? id.equals(that.id) : that.id == null;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	public static ProductionOrderDetail clone(ProductionOrderDetail other) {
		try {
			return (ProductionOrderDetail) other.clone();
		} catch (CloneNotSupportedException e) {
			// not possible
		}
		return null;
	}
}
