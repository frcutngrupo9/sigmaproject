package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.util.Date;

public class OrderState implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	Integer idOrder;
	Integer idOrderStateType;
	Date date;
	
	public OrderState(Integer idOrder, Integer idOrderStateType, Date date) {
		this.idOrder = idOrder;
		this.idOrderStateType = idOrderStateType;
		this.date = date;
	}

	public Integer getIdOrder() {
		return idOrder;
	}

	public void setIdOrder(Integer idOrder) {
		this.idOrder = idOrder;
	}
	
	public Integer getIdOrderStateType() {
		return idOrderStateType;
	}

	public void setIdOrderStateType(Integer idOrderType) {
		this.idOrderStateType = idOrderType;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderState other = (OrderState) obj;
		if (idOrder != null && idOrderStateType != null) {
			if (other.idOrder != null && other.idOrderStateType != null) {
				if (other.idOrder == idOrder && other.idOrderStateType == idOrderStateType)
					return true;
			}
		}
		return false;
	}

	public static OrderState clone(OrderState orderState) {
		try {
			return (OrderState) orderState.clone();
		} catch (CloneNotSupportedException e) {
			// not possible
		}
		return null;
	}
}