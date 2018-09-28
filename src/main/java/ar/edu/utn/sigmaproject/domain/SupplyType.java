/*
 * The MIT License
 *
 * Copyright (C) 2017 SigmaProject.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.search.annotations.Indexed;

@Entity
@Indexed
public class SupplyType extends Item implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "item", targetEntity = MaterialReserved.class)
	private List<MaterialReserved> suppliesReserved = new ArrayList<>();

	private String code = "";
	private String description = "";
	private String details = "";
	private String brand = "";
	private String presentation = "";
	private String measure = "";
	private BigDecimal stock = BigDecimal.ZERO;
	private BigDecimal stockMin = BigDecimal.ZERO;
	private BigDecimal stockRepo = BigDecimal.ZERO;

	public SupplyType() {
	}

	public SupplyType(String code, String description, String details, String brand, String presentation, String measure, BigDecimal stock, BigDecimal stockMin, BigDecimal stockRepo, BigDecimal price) {
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
		this.price = price;
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

	public List<MaterialReserved> getSuppliesReserved() {
		return suppliesReserved;
	}

	public void setSuppliesReserved(List<MaterialReserved> suppliesReserved) {
		this.suppliesReserved = suppliesReserved;
	}

	public BigDecimal getStockReserved() {
		BigDecimal aux = BigDecimal.ZERO;
		for(MaterialReserved each : suppliesReserved) {
			aux = aux.add(each.getStockReserved());
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

	@Override
	public List<MaterialReserved> getMaterialReservedList() {
		return getSuppliesReserved();
	}
}