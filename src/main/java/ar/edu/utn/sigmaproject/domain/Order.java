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
    Long id;
    
    @ManyToOne
    Client client;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @OrderColumn(name = "state_index")
    List<OrderState> states = new ArrayList<OrderState>();
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @OrderColumn(name = "detail_index")
    List<OrderDetail> details = new ArrayList<OrderDetail>();
    
    @ManyToOne(optional = false)
	OrderStateType currentStateType;
    
    Integer number;
    Date date;
    Date needDate;

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
	
	public List<OrderState> getStates() {
		return states;
	}

	public void setStates(List<OrderState> states) {
		this.states = states;
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

	public void setCurrentStateType(OrderStateType currentStateType) {
		this.currentStateType = currentStateType;
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