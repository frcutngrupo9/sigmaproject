package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class Product implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	Integer id;
	String name;
	String details;
	String code;
	ProductCategory category;
	BigDecimal price;
	boolean isClone;

	public Product(Integer id, String code , String name, String details, ProductCategory category, BigDecimal price) {
		this.id = id;
		this.name = name;
		this.details = details;
		this.code = code;
		this.category = category;
		this.price = price;
		isClone = false;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
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
		Product other = (Product) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public static Product clone(Product product){
		try {
			return (Product)product.clone();
		} catch (CloneNotSupportedException e) {
			//not possible
		}
		return null;
	}
}
