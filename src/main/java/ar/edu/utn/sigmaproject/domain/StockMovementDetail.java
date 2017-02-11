package ar.edu.utn.sigmaproject.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class StockMovementDetail<T extends Item> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;

	@ManyToOne(targetEntity = StockMovement.class)
	private StockMovement<T> stockMovement = null;

	private String description = "";

	private BigDecimal quantity = BigDecimal.ZERO;

	@ManyToOne(targetEntity = Item.class, optional = false)
	private T item;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public StockMovement<T> getStockMovement() {
		return stockMovement;
	}

	public void setStockMovement(StockMovement<T> stockMovement) {
		this.stockMovement = stockMovement;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public T getItem() {
		return item;
	}

	public void setItem(T item) {
		this.item = item;
	}
}
