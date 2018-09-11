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

import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.xml.datatype.Duration;

@Entity
@Indexed
@Analyzer(definition = "edge_ngram")
public class Product extends Item implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "product", targetEntity = Piece.class)
	private List<Piece> pieces = new ArrayList<>();

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "product", targetEntity = ProductMaterial.class)
	private List<ProductMaterial> materials = new ArrayList<>();

	@Lob
	private byte[] imageData = new byte[0];

	@Field
	private String name = "";

	@Field
	private String details = "";

	@Field
	private String code = "";

	private Integer stock = 0;
	private Integer stockMin = 0;
	private Integer stockRepo = 0;
	private Integer stockMax = 0;

	@ManyToOne
	private ProductCategory category;

	private BigDecimal price = BigDecimal.ZERO;
	private boolean isClone;

	public Product() {

	}

	public Product(String code , String name, String details, ProductCategory category, BigDecimal price) {
		this.name = name;
		this.details = details;
		this.category = category;
		this.code = code;
		this.price = price;
	}

	public Duration getDurationTotal() {
		Duration durationTotal = null;
		for(Piece each : pieces) {
			if(durationTotal == null) {
				durationTotal = each.getDurationTotal();
			} else {
				durationTotal = durationTotal.add(each.getDurationTotal());
			}

		}
		return durationTotal;
	}

	@Override
	public String getDescription() {
		return getName();
	}

	public List<Piece> getPieces() {
		return pieces;
	}

	public void setPieces(List<Piece> pieces) {
		this.pieces = pieces;
	}

	public List<ProductMaterial> getSupplies() {
		List<ProductMaterial> supplies = new ArrayList<>();
		for(ProductMaterial each : materials) {
			if(each.getType() == MaterialType.Supply) {
				supplies.add(each);
			}
		}
		return supplies;
	}

	public List<ProductMaterial> getRawMaterials() {
		List<ProductMaterial> rawMaterials = new ArrayList<>();
		for(ProductMaterial each : materials) {
			if(each.getType() == MaterialType.Wood) {
				rawMaterials.add(each);
			}
		}
		return rawMaterials;
	}

	public List<ProductMaterial> getMaterials() {
		return materials;
	}

	public void setMaterials(List<ProductMaterial> materials) {
		this.materials = materials;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public Integer getStockMin() {
		return stockMin;
	}

	public void setStockMin(Integer stockMin) {
		this.stockMin = stockMin;
	}

	public Integer getStockRepo() {
		return stockRepo;
	}

	public void setStockRepo(Integer stockRepo) {
		this.stockRepo = stockRepo;
	}

	public Integer getStockMax() {
		return stockMax;
	}

	public void setStockMax(Integer stockMax) {
		this.stockMax = stockMax;
	}

	public byte[] getImageData() {
		return imageData;
	}

	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}

	public String getName() {
		return name;
	}

	public String getDetails() {
		return details;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getCode() {
		return code;
	}

	public ProductCategory getCategory() {
		return category;
	}

	public void setCategory(ProductCategory category) {
		this.category = category;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public boolean isClone() {
		return isClone;
	}

	public void setClone(boolean isClone) {
		this.isClone = isClone;
	}

	@Override
	public List<MaterialReserved> getMaterialReservedList() {
		// metodo no usado pero implementado porque hereda de item (se usa en materiales)
		return null;
	}

	public BigDecimal getCostMaterials() {
		//devuelve el valor de todos las materias primas e insumos
		BigDecimal materialsPrice = BigDecimal.ZERO;
		for(ProductMaterial each : materials) {
			materialsPrice = materialsPrice.add(each.getCost());
		}
		return materialsPrice;
	}

	public BigDecimal getCostSupplies() {
		//devuelve el valor de los insumos
		BigDecimal materialsPrice = BigDecimal.ZERO;
		for(ProductMaterial each : materials) {
			Item item = each.getItem();
			if(item instanceof SupplyType) {
				materialsPrice = materialsPrice.add(each.getCost());
			}
		}
		return materialsPrice;
	}

	public BigDecimal getCostWoods() {
		//devuelve el valor de los insumos
		BigDecimal materialsPrice = BigDecimal.ZERO;
		for(ProductMaterial each : materials) {
			Item item = each.getItem();
			if (item instanceof Wood) {
				materialsPrice = materialsPrice.add(each.getCost());
			}
		}
		return materialsPrice;
	}

	public BigDecimal getCostWork() {
		BigDecimal cost = BigDecimal.ZERO;
		for(Piece each : pieces) {
			cost = cost.add(each.getCost());
		}
		return cost;
	}

	public BigDecimal getCostTotal() {
		return getCostMaterials().add(getCostWork());
	}
}
