package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

@Entity(name = "Orders")
public class Order implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@ManyToOne
	Client client;

	@OneToMany(orphanRemoval = true)
	@OrderColumn(name = "detail_index")
	List<OrderDetail> details = new ArrayList<>();

	@OneToMany(orphanRemoval = true)
	List<OrderState> states = new ArrayList<>();

	Integer number = 0;
	Date date = new Date();
	Date needDate = new Date();

	public Order() {

	}

	public Order(Client client, Integer number, Date date, Date needDate) {
		this.client = client;
		this.number = number;
		this.date = date;
		this.needDate = needDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public List<OrderDetail> getDetails() {
		return details;
	}

	public void setDetails(List<OrderDetail> details) {
		this.details = details;
	}

	public OrderStateType getCurrentStateType() {
		OrderState result = null;
		for(OrderState each : states) {// busca el objeto con la fecha mas reciente
			if(result != null) {
				if(result.getDate().before(each.getDate())) {
					result = each;
				}
			} else {
				result = each;
			}
		}
		if(result != null) {
			return result.getType();
		}
		return null;
	}
	
	public void setState(OrderState state) {
		states.add(state);
	}

	public List<OrderState> getStates() {
		return states;
	}

	public void setStates(List<OrderState> states) {
		this.states = states;
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
	
	public String getFormattedNeedDate() {
		if(needDate != null) {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			return dateFormat.format(needDate);
		}
		return "";
	}
}