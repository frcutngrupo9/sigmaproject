package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
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
	private Long id;

	@ManyToOne
	private Client client;

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "order", targetEntity = OrderDetail.class)
	@OrderColumn(name = "detail_index")
	private List<OrderDetail> details = new ArrayList<>();

	@OneToMany(orphanRemoval = true)
	private List<OrderState> states = new ArrayList<>();

	private Integer number = 0;
	private Date date = new Date();
	private Date needDate = new Date();
	private OrderStateType currentStateType = null;

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
		return currentStateType;
	}

	public OrderState getCurrentState() {
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
			return result;
		}
		return null;
	}

	public void setState(OrderState state) {
		currentStateType = state.getType();
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
}