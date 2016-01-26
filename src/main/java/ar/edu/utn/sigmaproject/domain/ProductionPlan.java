package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
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

@Entity
public class ProductionPlan  implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	@OneToMany(mappedBy = "productionPlan", cascade = CascadeType.ALL)
	@OrderColumn(name = "detail_index")
	List<ProductionPlanDetail> details;
	
	@OneToMany(mappedBy = "productionPlan", cascade = CascadeType.ALL)
	@OrderColumn(name = "state_index")
	List<ProductionPlanState> states;
	
	@ManyToOne
	ProductionPlanStateType currentStateType;
	
	String name = "";
	String info = "";
	Date date = new Date();

	public ProductionPlan(String name, String details, Date date) {
		this.name = name;
		this.info = details;
		this.date = date;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public List<ProductionPlanDetail> getDetails() {
		return details;
	}

	public void setDetails(List<ProductionPlanDetail> details) {
		this.details = details;
	}

	public List<ProductionPlanState> getStates() {
		return states;
	}

	public void setStates(List<ProductionPlanState> states) {
		this.states = states;
	}

	public ProductionPlanStateType getCurrentStateType() {
		return currentStateType;
	}

	public void setCurrentStateType(ProductionPlanStateType currentStateType) {
		this.currentStateType = currentStateType;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getInfo() {
		return info;
	}
	
	public void setInfo(String info) {
		this.info = info;
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