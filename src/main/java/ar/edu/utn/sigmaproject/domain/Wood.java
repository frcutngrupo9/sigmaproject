package ar.edu.utn.sigmaproject.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

@Indexed
@Analyzer(definition = "edge_ngram")
@Entity
public class Wood extends Item implements Cloneable {
	private static final long serialVersionUID = 1L;

	@IndexedEmbedded
	@ManyToOne
	RawMaterialType rawMaterialType;

	@IndexedEmbedded
	@ManyToOne
	WoodType woodType;

	@OneToMany(orphanRemoval = true)
	List<WoodReserved> woodsReserved = new ArrayList<>();

	BigDecimal stock = BigDecimal.ZERO;
	BigDecimal stockMin = BigDecimal.ZERO;
	BigDecimal stockRepo = BigDecimal.ZERO;

	public Wood() {

	}

	public Wood(RawMaterialType rawMaterialType, WoodType woodType, BigDecimal stock, BigDecimal stockMin, BigDecimal stockRepo) {
		this.rawMaterialType = rawMaterialType;
		this.woodType = woodType;
		this.stock = stock;
		this.stockMin = stockMin;
		this.stockRepo = stockRepo;
	}

	@Override
	public String getDescription() {
		return getRawMaterialType().getFormattedMeasure() + " (" + getWoodType().getName() + ")";
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

	public List<WoodReserved> getWoodsReserved() {
		return woodsReserved;
	}

	public void setWoodsReserved(List<WoodReserved> woodsReserved) {
		this.woodsReserved = woodsReserved;
	}

	public BigDecimal getStockReserved() {
		BigDecimal aux = BigDecimal.ZERO;
		for(WoodReserved each : woodsReserved) {
			if(!each.isWithdrawn()) {
				aux = aux.add(each.getStockReserved());
			}
		}
		return aux;
	}
	
	public BigDecimal getStockAvailable() {
		// devuelve la diferencia entre el stock total y el total reservado
		BigDecimal stockTotal = getStock();
		BigDecimal stockReservedTotal = getStockReserved();
		return stockTotal.subtract(stockReservedTotal);
	}
}
