package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

@Indexed
@Analyzer(definition = "edge_ngram")
@Entity
public class SupplyType extends Item implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

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

	@OneToMany(orphanRemoval = true)
	List<SupplyReserved> suppliesReserved = new ArrayList<>();

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

	public List<SupplyReserved> getSuppliesReserved() {
		return suppliesReserved;
	}

	public void setSuppliesReserved(List<SupplyReserved> suppliesReserved) {
		this.suppliesReserved = suppliesReserved;
	}

	public BigDecimal getStockReserved() {
		BigDecimal aux = BigDecimal.ZERO;
		for(SupplyReserved each : suppliesReserved) {
			if(!each.isWithdrawn()) {
				aux = aux.add(each.getStockReserved());
			}
		}
		return aux;
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
	
	public BigDecimal getStockAvailable() {
		// devuelve la diferencia entre el stock total y el total reservado
		BigDecimal stockTotal = getStock();
		BigDecimal stockReservedTotal = getStockReserved();
		return stockTotal.subtract(stockReservedTotal);
	}
}
