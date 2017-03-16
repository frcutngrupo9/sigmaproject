package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

@Entity
public class ProductionPlan  implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@OneToMany(orphanRemoval = true)
	@OrderColumn(name = "detail_index")
	List<ProductionPlanDetail> planDetails = new ArrayList<>();

	@OneToMany(orphanRemoval = true)
	List<ProductionPlanState> states = new ArrayList<>();

	@OneToMany(orphanRemoval = true)
	List<RawMaterialRequirement> rawMaterialRequirements = new ArrayList<>();

	@OneToMany(orphanRemoval = true)
	List<SupplyRequirement> supplyRequirements = new ArrayList<>();

	String name = "";
	Date dateCreation = null;
	Date dateStart = null;
	ProductionPlanStateType currentStateType = null;

	public ProductionPlan() {

	}

	public ProductionPlan(String name, List<ProductionPlanDetail> planDetails) {
		this.name = name;
		this.dateCreation = new Date();
		this.planDetails.addAll(planDetails);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<ProductionPlanDetail> getPlanDetails() {
		return planDetails;
	}

	public void setPlanDetails(List<ProductionPlanDetail> planDetails) {
		this.planDetails = planDetails;
	}

	public ProductionPlanStateType getCurrentStateType() {
		return currentStateType;
	}

	public ProductionPlanState getCurrentState() {
		ProductionPlanState result = null;
		for(ProductionPlanState each : states) {// busca el objeto con la fecha mas reciente
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

	public void setState(ProductionPlanState state) {
		currentStateType = state.getProductionPlanStateType();
		states.add(state);
	}

	public List<ProductionPlanState> getStates() {
		return states;
	}

	public void setStates(List<ProductionPlanState> states) {
		this.states = states;
	}

	public List<RawMaterialRequirement> getRawMaterialRequirements() {
		return rawMaterialRequirements;
	}

	public void setRawMaterialRequirements(List<RawMaterialRequirement> rawMaterialRequirements) {
		this.rawMaterialRequirements = rawMaterialRequirements;
	}

	public List<SupplyRequirement> getSupplyRequirements() {
		return supplyRequirements;
	}

	public void setSupplyRequirements(List<SupplyRequirement> supplyRequirements) {
		this.supplyRequirements = supplyRequirements;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public Date getDateStart() {
		return dateStart;
	}

	public void setDateStart(Date dateStart) {
		this.dateStart = dateStart;
	}
}