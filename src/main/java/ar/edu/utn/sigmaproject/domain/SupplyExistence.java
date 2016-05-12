package ar.edu.utn.sigmaproject.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class SupplyExistence implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;

	@ManyToOne
	SupplyType supplyType;

	BigDecimal stock = BigDecimal.ZERO;
	BigDecimal stockMin = BigDecimal.ZERO;
	BigDecimal stockRepo = BigDecimal.ZERO;

	public SupplyExistence() {
		
	}

	public SupplyExistence(SupplyType supplyType, BigDecimal stock, BigDecimal stockMin, BigDecimal stockRepo) {
		this.supplyType = supplyType;
		this.stock = stock;
		this.stockMin = stockMin;
		this.stockRepo = stockRepo;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public SupplyType getSupplyType() {
		return supplyType;
	}

	public void setSupplyType(SupplyType supplyType) {
		this.supplyType = supplyType;
	}

	public BigDecimal getStock() {
		return stock;
	}

	public void setStock(BigDecimal stock) {
		this.stock = stock;
	}

	public BigDecimal getStockMin() {
		return stockMin;
	}

	public void setStockMin(BigDecimal stockMin) {
		this.stockMin = stockMin;
	}

	public BigDecimal getStockRepo() {
		return stockRepo;
	}

	public void setStockRepo(BigDecimal stockRepo) {
		this.stockRepo = stockRepo;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SupplyExistence that = (SupplyExistence) o;

		return id != null ? id.equals(that.id) : that.id == null;

	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	public static SupplyExistence clone(SupplyExistence supplyExistence){
		try {
			return (SupplyExistence)supplyExistence.clone();
		} catch (CloneNotSupportedException e) {
			//not possible
		}
		return null;
	}

}
