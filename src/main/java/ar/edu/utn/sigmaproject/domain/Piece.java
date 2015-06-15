package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class Piece implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	Integer id;
	Integer idProduct;
	Long height;
	Long width;
	Long depth;
	Long size1;
	Long size2;
	boolean isGroup;
	Integer units;

	public Piece(Integer id, Integer idProduct, Long height, Long width, Long depth, Long size1, Long size2, boolean isGroup, Integer units) {
		this.id = id;
		this.idProduct = idProduct;
		this.height = height;
		this.width = width;
		this.depth = depth;
		this.size1 = size1;
		this.size2 = size2;
		this.isGroup = isGroup;
		this.units = units;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getIdProduct() {
		return idProduct;
	}

	public void setIdProduct(Integer idProduct) {
		this.idProduct = idProduct;
	}

	public Long getHeight() {
		return height;
	}

	public void setHeight(Long height) {
		this.height = height;
	}

	public Long getWidth() {
		return width;
	}

	public void setWidth(Long width) {
		this.width = width;
	}

	public Long getDepth() {
		return depth;
	}

	public void setDepth(Long depth) {
		this.depth = depth;
	}

	public Long getSize1() {
		return size1;
	}

	public void setSize1(Long size1) {
		this.size1 = size1;
	}

	public Long getSize2() {
		return size2;
	}

	public void setSize2(Long size2) {
		this.size2 = size2;
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
		Piece other = (Piece) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public static Piece clone(Piece piece){
		try {
			return (Piece)piece.clone();
		} catch (CloneNotSupportedException e) {
			//not possible
		}
		return null;
	}
}