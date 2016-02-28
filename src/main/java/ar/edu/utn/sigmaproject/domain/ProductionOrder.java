package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.util.Date;

public class ProductionOrder implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	Integer id;
	Integer idProductionPlan;
	Integer idProduct;
	Integer idWorker;
	Integer number;
	Integer units;
	Date date;
	Date dateFinished;

	public ProductionOrder(Integer id, Integer idProductionPlan, Integer idProduct, Integer idWorker, Integer number, Integer units, Date date, Date dateFinished) {
		this.id = id;
		this.idProductionPlan = idProductionPlan;
		this.idProduct = idProduct;
		this.idWorker = idWorker;
		this.number = number;
		this.units = units;
		this.date = date;
		this.dateFinished = dateFinished;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdProductionPlan() {
		return idProductionPlan;
	}

	public void setIdProductionPlan(Integer idProductionPlan) {
		this.idProductionPlan = idProductionPlan;
	}

	public Integer getIdProduct() {
		return idProduct;
	}

	public void setIdProduct(Integer idProduct) {
		this.idProduct = idProduct;
	}

	public Integer getIdWorker() {
		return idWorker;
	}

	public void setIdWorker(Integer idWorker) {
		this.idWorker = idWorker;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getUnits() {
		return units;
	}

	public void setUnits(Integer units) {
		this.units = units;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDateFinished() {
		return dateFinished;
	}

	public void setDateFinished(Date dateFinished) {
		this.dateFinished = dateFinished;
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
		ProductionOrder other = (ProductionOrder) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public static ProductionOrder clone(ProductionOrder order){
		try {
			return (ProductionOrder)order.clone();
		} catch (CloneNotSupportedException e) {
			//not possible
		}
		return null;
	}
}
