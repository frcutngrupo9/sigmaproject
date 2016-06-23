package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class OrderState implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@ManyToOne(optional = false)
	OrderStateType type;

	@Column(nullable = false)
	Date date = new Date();

	public OrderState() {

	}

	public OrderState(OrderStateType orderStateType, Date date) {
		this.type = orderStateType;
		this.date = date;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public OrderStateType getType() {
		return type;
	}

	public void setType(OrderStateType type) {
		this.type = type;
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
		if (id != null && other.id != null) {
			return id.equals(other.id);
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