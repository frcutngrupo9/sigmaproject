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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.xml.datatype.Duration;

@Entity
public class Piece implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(targetEntity = Product.class)
	private Product product = null;

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "piece", targetEntity = Process.class)
	private List<Process> processes = new ArrayList<>();

	private String name = "";

	private BigDecimal length = BigDecimal.ZERO;

	@ManyToOne
	private MeasureUnit lengthMeasureUnit;

	private BigDecimal depth = BigDecimal.ZERO;

	@ManyToOne
	private MeasureUnit depthMeasureUnit;

	private BigDecimal width = BigDecimal.ZERO;

	@ManyToOne
	private MeasureUnit widthMeasureUnit;

	private String size = "";
	private boolean isGroup;
	private Integer units = 0;

	@ManyToOne(targetEntity = Piece.class)
	private Piece pieceParent = null;

	@OneToMany(orphanRemoval = false, cascade = CascadeType.PERSIST, mappedBy = "pieceParent", targetEntity = Piece.class)
	private List<Piece> pieceGroupList = null;

	@Lob
	private byte[] imageData = null;

	private boolean isClone;

	public Piece() {

	}

	public Piece(Product product, String name, BigDecimal length, MeasureUnit lengthMeasureUnit, BigDecimal depth, MeasureUnit depthMeasureUnit, BigDecimal width, MeasureUnit widthMeasureUnit, String size, boolean isGroup, Integer units) {
		this.product = product;
		this.name = name;
		this.length = length;
		this.lengthMeasureUnit = lengthMeasureUnit;
		this.width = width;
		this.widthMeasureUnit = widthMeasureUnit;
		this.depth = depth;
		this.depthMeasureUnit = depthMeasureUnit;
		this.size = size;
		this.isGroup = isGroup;
		this.units = units;
		isClone = false;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Duration getDurationTotal() {
		Duration durationTotal = null;
		for(Process each : processes) {
			Duration timePerProduct = each.getTime().multiply(new BigDecimal(getUnits()));
			if(durationTotal == null) {
				durationTotal = timePerProduct;
			} else {
				durationTotal = durationTotal.add(timePerProduct);
			}

		}
		return durationTotal;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Process> getProcesses() {
		return processes;
	}

	public void setProcesses(List<Process> processes) {
		this.processes = processes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getLength() {
		return length;
	}

	public void setLength(BigDecimal length) {
		this.length = length;
	}

	public MeasureUnit getLengthMeasureUnit() {
		return this.lengthMeasureUnit;
	}

	public void setLengthMeasureUnit(MeasureUnit lengthMeasureUnit) {
		this.lengthMeasureUnit = lengthMeasureUnit;
	}

	public BigDecimal getDepth() {
		return depth;
	}

	public void setDepth(BigDecimal depth) {
		this.depth = depth;
	}

	public MeasureUnit getDepthMeasureUnit() {
		return this.depthMeasureUnit;
	}

	public void setDepthMeasureUnit(MeasureUnit depthMeasureUnit) {
		this.depthMeasureUnit = depthMeasureUnit;
	}

	public BigDecimal getWidth() {
		return width;
	}

	public void setWidth(BigDecimal width) {
		this.width = width;
	}

	public MeasureUnit getWidthMeasureUnit() {
		return this.widthMeasureUnit;
	}

	public void setWidthMeasureUnit(MeasureUnit widthMeasureUnit) {
		this.widthMeasureUnit = widthMeasureUnit;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public boolean isGroup() {
		return isGroup;
	}

	public void setGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}

	public Integer getUnits() {
		return units;
	}

	public void setUnits(Integer units) {
		this.units = units;
	}

	public Piece getPieceParent() {
		return pieceParent;
	}

	public void setPieceParent(Piece pieceParent) {
		this.pieceParent = pieceParent;
	}

	public List<Piece> getPieceGroupList() {
		/*
		// si no existe, crea uno vacio
		if(pieceGroupList == null) {
			pieceGroupList = new ArrayList<>();
		}*/
		return pieceGroupList;// debe ser nulo si isGroup es false
	}

	public void setPieceGroupList(List<Piece> pieceGroupList) {
		this.pieceGroupList = pieceGroupList;
	}

	public byte[] getImageData() {
		return imageData;
	}

	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}

	public BigDecimal getCost() {
		// calcula el costo de todos los procesos
		BigDecimal cost = BigDecimal.ZERO;
		for(Process each : processes) {
			cost = cost.add(each.getCost().multiply(new BigDecimal(units)));
		}
		return cost;
	}

	public boolean isClone() {
		return isClone;
	}

	public void setClone(boolean isClone) {
		this.isClone = isClone;
	}

	/**
	 * Returns a copy of the object, or null if the object cannot
	 * be serialized.
	 */
	public static Object copy(Object orig) {
		// crea una copia profunda de la pieza para que se copien tambien los procesos
		Object obj = null;
		try {
			// Write the object out to a byte array
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(orig);
			out.flush();
			out.close();

			// Make an input stream from the byte array and read
			// a copy of the object back in.
			ObjectInputStream in = new ObjectInputStream(
					new ByteArrayInputStream(bos.toByteArray()));
			obj = in.readObject();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		catch(ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
		return obj;
	}

}