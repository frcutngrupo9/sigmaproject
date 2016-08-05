package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

@Entity
public class ProductionOrderState implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	@ManyToOne
	ProductionOrderStateType productionOrderStateType;

	@Column(nullable = false)
	Date date = new Date();

	public ProductionOrderState() {

	}

	public ProductionOrderState(ProductionOrderStateType productionOrderStateType, Date date) {
		this.productionOrderStateType = productionOrderStateType;
		this.date = date;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ProductionOrderStateType getProductionOrderStateType() {
		return productionOrderStateType;
	}

	public void setProductionOrderStateType(ProductionOrderStateType productionOrderStateType) {
		this.productionOrderStateType = productionOrderStateType;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ProductionOrderState)) {
			return false;
		}
		ProductionOrderState other = (ProductionOrderState) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
}
