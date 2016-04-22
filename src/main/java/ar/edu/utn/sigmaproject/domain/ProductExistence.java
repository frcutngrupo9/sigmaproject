package ar.edu.utn.sigmaproject.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class ProductExistence implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@OneToOne
	Product product;

	Integer stock = 0;
	Integer stockMin = 0;
	Integer stockRepo = 0;

	public ProductExistence() {

	}

	public ProductExistence(Product product, Integer stock, Integer stockMin, Integer stockRepo) {
		this.product = product;
		this.stock = stock;
		this.stockMin = stockMin;
		this.stockRepo = stockRepo;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ProductExistence that = (ProductExistence) o;

		return product.equals(that.product);

	}

	@Override
	public int hashCode() {
		return product.hashCode();
	}

	public static ProductExistence clone(ProductExistence productExistence){
		try {
			return (ProductExistence)productExistence.clone();
		} catch (CloneNotSupportedException e) {
			//not possible
		}
		return null;
	}

}
