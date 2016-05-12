package ar.edu.utn.sigmaproject.domain;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.*;

@Entity
@Indexed
public class SupplyType implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Field
	String code = "";

	@Field
	String description = "";

	@Field
	String details = "";

	@Field
	String brand = "";

	@Field
	String presentation = "";

	@Field
	String measure = "";

	BigDecimal stock = BigDecimal.ZERO;
	BigDecimal stockMin = BigDecimal.ZERO;
	BigDecimal stockRepo = BigDecimal.ZERO;

	public SupplyType() {
		
	}
	
	public SupplyType(String code, String description, String details, String brand, String presentation, String measure, BigDecimal stock, BigDecimal stockMin, BigDecimal stockRepo) {
		this.code = code;
		this.description = description;
		this.details = details;
		this.brand = brand;
		this.presentation = presentation;
		this.measure = measure;
		this.stock = stock;
		this.stockMin = stockMin;
		this.stockRepo = stockRepo;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getPresentation() {
		return presentation;
	}

	public void setPresentation(String presentation) {
		this.presentation = presentation;
	}

	public String getMeasure() {
		return measure;
	}

	public void setMeasure(String measure) {
		this.measure = measure;
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
		SupplyType other = (SupplyType) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public static SupplyType clone(SupplyType supplyType){
		try {
			return (SupplyType)supplyType.clone();
		} catch (CloneNotSupportedException e) {
			//not possible
		}
		return null;
	}
}
