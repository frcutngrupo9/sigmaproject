package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
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
import javax.xml.datatype.Duration;

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

	Integer sequence = 0;
	Integer number = 0;
	Integer units = 0;
	Date dateStart = null;
	Date dateFinish = null;
	Date dateStartReal = null;
	Date dateFinishReal = null;

	@OneToMany(orphanRemoval = true)
	List<ProductionOrderSupply> productionOrderSupplies = new ArrayList<>();

	@OneToMany(orphanRemoval = true)
	List<ProductionOrderRawMaterial> productionOrderRawMaterials = new ArrayList<>();

	public ProductionOrder() {

	}

	public ProductionOrder(Integer sequence, ProductionPlan productionPlan, Product product, Integer units, ProductionOrderState state) {
		this.sequence = sequence;
		this.productionPlan = productionPlan;
		this.product = product;
		this.units = units;
		this.states.add(state);
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

	public Date getDateStart() {
		return dateStart;
	}

	public void setDateStart(Date dateStart) {
		this.dateStart = dateStart;
	}

	public Date getDateFinish() {
		return dateFinish;
	}

	public void setDateFinish(Date dateFinish) {
		this.dateFinish = dateFinish;
	}

	public Date getDateStartReal() {
		return dateStartReal;
	}

	public void setDateStartReal(Date dateStartReal) {
		this.dateStartReal = dateStartReal;
	}

	public Date getDateFinishReal() {
		return dateFinishReal;
	}

	public void setDateFinishReal(Date dateFinishReal) {
		this.dateFinishReal = dateFinishReal;
	}

	public List<ProductionOrderSupply> getProductionOrderSupplies() {
		return productionOrderSupplies;
	}

	public void setProductionOrderSupplies(List<ProductionOrderSupply> productionOrderSupplyList) {
		this.productionOrderSupplies = productionOrderSupplyList;
	}

	public List<ProductionOrderRawMaterial> getProductionOrderRawMaterials() {
		return productionOrderRawMaterials;
	}

	public void setProductionOrderRawMaterials(List<ProductionOrderRawMaterial> productionOrderRawMaterialList) {
		this.productionOrderRawMaterials = productionOrderRawMaterialList;
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

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Duration getDurationTotal() {
		if(product.getDurationTotal() != null) {
			return product.getDurationTotal().multiply(units);
		}
		return null;
	}
}
