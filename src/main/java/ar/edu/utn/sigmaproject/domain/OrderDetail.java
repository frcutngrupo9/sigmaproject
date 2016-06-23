package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class OrderDetail  implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@ManyToOne
	Product product;
	Integer units = 0;
	BigDecimal price = BigDecimal.ZERO;

	public OrderDetail() {

	}

	public OrderDetail(Product product, Integer units, BigDecimal price) {
		this.product = product;
		this.units = units;
		this.price = price;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}


	public Integer getUnits() {
		return units;
	}


	public void setUnits(Integer units) {
		this.units = units;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderDetail other = (OrderDetail) obj;
		if (id != null && other.id != null) {
			return id.equals(other.id);
		}
		return false;
	}

	public static OrderDetail clone(OrderDetail orderDetail) {
		try {
			return (OrderDetail) orderDetail.clone();
		} catch (CloneNotSupportedException e) {
			// not possible
		}
		return null;
	}

}