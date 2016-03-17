package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

import org.zkoss.image.Image;

public class ProductImage implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	Integer id;
	Integer idProduct;
	Image image;

	public ProductImage(Integer id, Integer idProduct, Image image) {
		this.id = id;
		this.idProduct = idProduct;
		this.image = image;
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

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
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
		ProductImage other = (ProductImage) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public static ProductImage clone(ProductImage productImage){
		try {
			return (ProductImage)productImage.clone();
		} catch (CloneNotSupportedException e) {
			//not possible
		}
		return null;
	}
}
