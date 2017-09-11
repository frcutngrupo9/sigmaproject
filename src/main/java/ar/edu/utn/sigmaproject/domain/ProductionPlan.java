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
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

@Entity
public class ProductionPlan  implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "productionPlan", targetEntity = ProductionOrder.class)
	private List<ProductionOrder> productionOrderList = new ArrayList<>();

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "productionPlan", targetEntity = ProductionPlanDetail.class)
	@OrderColumn(name = "detail_index")
	private List<ProductionPlanDetail> planDetails = new ArrayList<>();

	@OneToMany(orphanRemoval = true)
	private List<ProductionPlanState> states = new ArrayList<>();

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "productionPlan", targetEntity = MaterialRequirement.class)
	private List<MaterialRequirement> materialRequirements = new ArrayList<>();

	private String name = "";
	private Date dateCreation = null;
	private Date dateStart = null;
	private ProductionPlanStateType currentStateType = null;

	public ProductionPlan() {

	}

	public ProductionPlan(String name) {
		this.name = name;
		this.dateCreation = new Date();
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

	public List<MaterialRequirement> getRawMaterialRequirements() {
		List<MaterialRequirement> rawMaterialRequirements = new ArrayList<>();
		for(MaterialRequirement each : materialRequirements) {
			if(each.getType() == MaterialType.Wood) {
				rawMaterialRequirements.add(each);
			}
		}
		return rawMaterialRequirements;
	}

	public List<MaterialRequirement> getSupplyRequirements() {
		List<MaterialRequirement> supplyRequirements = new ArrayList<>();
		for(MaterialRequirement each : materialRequirements) {
			if(each.getType() == MaterialType.Supply) {
				supplyRequirements.add(each);
			}
		}
		return supplyRequirements;
	}

	public List<MaterialRequirement> getMaterialRequirements() {
		return materialRequirements;
	}

	public void setMaterialRequirements(
			List<MaterialRequirement> materialRequirements) {
		this.materialRequirements = materialRequirements;
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

	public List<ProductionOrder> getProductionOrderList() {
		return productionOrderList;
	}

	public void setProductionOrderList(List<ProductionOrder> productionOrderList) {
		this.productionOrderList = productionOrderList;
	}
}