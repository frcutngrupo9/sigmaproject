package ar.edu.utn.sigmaproject.domain;

import javax.persistence.*;
import javax.xml.datatype.Duration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class ProductionOrder implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@ManyToOne
	ProductionPlan productionPlan;

	@ManyToOne
	Product product;

	@ManyToOne
	Worker worker;

	@OneToMany(orphanRemoval = true)
	List<ProductionOrderState> states = new ArrayList<>();

	@OneToMany(orphanRemoval = true)
	@OrderColumn(name = "detail_index")
	List<ProductionOrderDetail> details = new ArrayList<>();

	Integer number = 0;
	Integer units = 0;
	Date date = new Date();
	Date dateFinished = new Date();

	public ProductionOrder() {

	}

	public ProductionOrder(ProductionPlan productionPlan, Product product, Worker worker, Integer number, Integer units, Date date, Date dateFinished, ProductionOrderState state) {
		this.productionPlan = productionPlan;
		this.product = product;
		this.worker = worker;
		this.number = number;
		this.units = units;
		this.date = date;
		this.dateFinished = dateFinished;
		this.states.add(state);
		// se crean los detalles
		//		for(Piece piece : this.product.getPieces()) {
		//			List<Process> auxProcessList = piece.getProcesses();
		//			for(Process process : auxProcessList) {
		//				// por cada proceso hay que crear un detalle de orden de produccion
		//				Integer quantityPiece = this.units * piece.getUnits();// cantidad total de la pieza
		//				Duration timeTotal = process.getTime().multiply(quantityPiece);// cantidad total de tiempo del proceso
		//				this.details.add(new ProductionOrderDetail(process, null, timeTotal, quantityPiece));
		//			}
		//		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ProductionPlan getProductionPlan() {
		return productionPlan;
	}

	public void setProductionPlan(ProductionPlan productionPlan) {
		this.productionPlan = productionPlan;
	}

	public Product getProduct() {
		return product;
	}

	public Worker getWorker() {
		return worker;
	}

	public void setWorker(Worker worker) {
		this.worker = worker;
	}

	public List<ProductionOrderDetail> getDetails() {
		return details;
	}

	public void setDetails(List<ProductionOrderDetail> details) {
		this.details = details;
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
	public ProductionOrderStateType getCurrentStateType() {
		ProductionOrderState result = getCurrentState();
		if(result != null) {
			return result.getProductionOrderStateType();
		}
		return null;
	}

	public ProductionOrderState getCurrentState() {
		ProductionOrderState result = null;
		for(ProductionOrderState each : states) {// busca el objeto con la fecha mas reciente
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

	public void setState(ProductionOrderState state) {
		states.add(state);
	}

	public List<ProductionOrderState> getStates() {
		return states;
	}

	public void setStates(List<ProductionOrderState> states) {
		this.states = states;
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ProductionOrder)) {
			return false;
		}
		ProductionOrder other = (ProductionOrder) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
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
