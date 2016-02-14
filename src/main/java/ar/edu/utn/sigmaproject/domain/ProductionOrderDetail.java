package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

import javax.xml.datatype.Duration;

public class ProductionOrderDetail implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	Integer idProductionOrder;
	Integer idProcess;
	Duration timeTotal;
	Integer quantityPiece;
	Double quantityFinished;
	boolean isFinished;

	public ProductionOrderDetail(Integer idProductionOrder, Integer idProcess, Duration timeTotal, Integer quantityPiece) {
		this.idProductionOrder = idProductionOrder;
		this.idProcess = idProcess;
		this.timeTotal = timeTotal;
		this.quantityPiece = quantityPiece;
		quantityFinished = 0.0; 
		isFinished = false;
	}
	
	public Integer getIdProductionOrder() {
		return idProductionOrder;
	}

	public void setIdProductionOrder(Integer idProductionOrder) {
		this.idProductionOrder = idProductionOrder;
	}

	public Integer getIdProcess() {
		return idProcess;
	}

	public void setIdProcess(Integer idProcess) {
		this.idProcess = idProcess;
	}

	public Duration getTimeTotal() {
		return timeTotal;
	}

	public void setTimeTotal(Duration timeTotal) {
		this.timeTotal = timeTotal;
	}

	public Integer getQuantityPiece() {
		return quantityPiece;
	}

	public void setQuantityPiece(Integer quantityPiece) {
		this.quantityPiece = quantityPiece;
	}

	public Double getQuantityFinished() {
		return quantityFinished;
	}

	public void setQuantityFinished(Double quantityFinished) {
		this.quantityFinished = quantityFinished;
	}

	public boolean isFinished() {
		return isFinished;
	}

	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductionOrderDetail other = (ProductionOrderDetail) obj;
		if (idProductionOrder != null && idProcess != null) {
			if (other.idProductionOrder != null  && other.idProcess != null) {
				if (other.idProductionOrder.equals(idProductionOrder) && other.idProcess.equals(idProcess))
					return true;
			}
		}
		return false;
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
