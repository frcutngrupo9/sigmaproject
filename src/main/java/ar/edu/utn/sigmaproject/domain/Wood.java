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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.search.annotations.Indexed;

@Entity
@Indexed
public class Wood extends Item implements Cloneable {
	private static final long serialVersionUID = 1L;

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "item", targetEntity = MaterialReserved.class)
	private List<MaterialReserved> woodsReserved = new ArrayList<>();

	@ManyToOne
	private WoodType woodType;

	@ManyToOne
	private MeasureUnit lengthMeasureUnit;

	@ManyToOne
	private MeasureUnit depthMeasureUnit;

	@ManyToOne
	private MeasureUnit widthMeasureUnit;

	private String name = "";
	private BigDecimal length = BigDecimal.ZERO;
	private BigDecimal depth = BigDecimal.ZERO;
	private BigDecimal width = BigDecimal.ZERO;
	private BigDecimal stock = BigDecimal.ZERO;
	private BigDecimal stockMin = BigDecimal.ZERO;
	private BigDecimal stockRepo = BigDecimal.ZERO;

	public Wood() {
	}

	public Wood(String name, BigDecimal length, MeasureUnit lengthMeasureUnit, BigDecimal depth, MeasureUnit depthMeasureUnit, BigDecimal width, MeasureUnit widthMeasureUnit, WoodType woodType, BigDecimal stock, BigDecimal stockMin, BigDecimal stockRepo, BigDecimal price) {
		this.name = name;
		this.length = length;
		this.lengthMeasureUnit = lengthMeasureUnit;
		this.depth = depth;
		this.depthMeasureUnit = depthMeasureUnit;
		this.width = width;
		this.widthMeasureUnit = widthMeasureUnit;
		this.woodType = woodType;
		this.stock = stock;
		this.stockMin = stockMin;
		this.stockRepo = stockRepo;
		this.price = price;
	}

	@Override
	public String getDescription() {
		return getFormattedMeasure() + " (" + getWoodType().getName() + ")";
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

	public String getFormattedMeasure() {
		String depthText = "(E) " + depth.doubleValue() + " " + depthMeasureUnit.getShortName();
		String widthText = "(A) " + width.doubleValue() + " " + widthMeasureUnit.getShortName();
		String lengthText = "(L) " + length.doubleValue() + " " + lengthMeasureUnit.getShortName();
		return depthText + " x " + widthText + " x " + lengthText;
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

	public List<MaterialReserved> getWoodsReserved() {
		return woodsReserved;
	}

	public void setWoodsReserved(List<MaterialReserved> woodsReserved) {
		this.woodsReserved = woodsReserved;
	}

	public BigDecimal getStockReserved() {
		BigDecimal aux = BigDecimal.ZERO;
		for(MaterialReserved each : woodsReserved) {
			aux = aux.add(each.getStockReserved());
		}
		return aux;
	}

	public BigDecimal getStockAvailable() {
		// devuelve la diferencia entre el stock total y el total reservado
		BigDecimal stockTotal = getStock();
		BigDecimal stockReservedTotal = getStockReserved();
		return stockTotal.subtract(stockReservedTotal);
	}

	@Override
	public List<MaterialReserved> getMaterialReservedList() {
		return getWoodsReserved();
	}
}