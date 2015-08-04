package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.util.Date;

public class ProductionPlan  implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	Integer id;
	String name;
	String details;
	Date date;

	public ProductionPlan(Integer id, String name, String details, Date date) {
		this.id = id;
		this.name = name;
		this.details = details;
		this.date = date;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getDetails() {
		return details;
	}
	
	public void setDetails(String details) {
		this.details = details;
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductionPlan other = (ProductionPlan) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public static ProductionPlan clone(ProductionPlan productionPlan){
		try {
			return (ProductionPlan)productionPlan.clone();
		} catch (CloneNotSupportedException e) {
			//not possible
		}
		return null;
	}
}