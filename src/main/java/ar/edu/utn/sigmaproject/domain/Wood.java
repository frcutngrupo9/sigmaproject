package ar.edu.utn.sigmaproject.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class Wood implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@ManyToOne
	RawMaterialType rawMaterialType;

	@ManyToOne
	WoodType woodType;

	String code = "";
	BigDecimal stock = BigDecimal.ZERO;
	BigDecimal stockMin = BigDecimal.ZERO;
	BigDecimal stockRepo = BigDecimal.ZERO;

	public Wood() {

	}

	public Wood(RawMaterialType rawMaterialType, WoodType woodType, String code, BigDecimal stock, BigDecimal stockMin, BigDecimal stockRepo) {
		this.rawMaterialType = rawMaterialType;
		this.woodType = woodType;
		this.code = code;
		this.stock = stock;
		this.stockMin = stockMin;
		this.stockRepo = stockRepo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public RawMaterialType getRawMaterialType() {
		return rawMaterialType;
	}

	public void setRawMaterialType(RawMaterialType rawMaterialType) {
		this.rawMaterialType = rawMaterialType;
	}

	public WoodType getWoodType() {
		return woodType;
	}

	public void setWoodType(WoodType woodType) {
		this.woodType = woodType;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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
		Wood other = (Wood) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public static Wood clone(Wood other) {
		try {
			return (Wood)other.clone();
		} catch (CloneNotSupportedException e) {
			//not possible
		}
		return null;
	}
}
