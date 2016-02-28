package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.util.Date;

public class Order implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	Integer id;
	Integer idClient;
	Integer number;
	Date date;
	Date needDate;

	public Order(Integer id, Integer idClient, Integer number, Date date, Date needDate) {
		this.id = id;
		this.idClient = idClient;
		this.number = number;
		this.date = date;
		this.needDate = needDate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdClient() {
		return idClient;
	}

	public void setIdClient(Integer idClient) {
		this.idClient = idClient;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getNeedDate() {
		return needDate;
	}

	public void setNeedDate(Date needDate) {
		this.needDate = needDate;
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
		Order other = (Order) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public static Order clone(Order order){
		try {
			return (Order)order.clone();
		} catch (CloneNotSupportedException e) {
			//not possible
		}
		return null;
	}
}