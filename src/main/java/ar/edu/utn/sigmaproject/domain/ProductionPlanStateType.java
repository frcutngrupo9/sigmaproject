package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class ProductionPlanStateType implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	Integer id;
	String name;
	String details;

	public ProductionPlanStateType(Integer id, String name, String details) {
		this.id = id;
		this.name = name;
		this.details = details;
	}
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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
		ProductionPlanStateType other = (ProductionPlanStateType) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public static ProductionPlanStateType clone(ProductionPlanStateType other) {
		try {
			return (ProductionPlanStateType) other.clone();
		} catch (CloneNotSupportedException e) {
			// not possible
		}
		return null;
	}
}
