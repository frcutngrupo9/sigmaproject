package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.*;

@Entity
public class ProductionPlan  implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@OneToMany(orphanRemoval = true)
	@OrderColumn(name = "detail_index")
	List<ProductionPlanDetail> planDetails = new ArrayList<>();

	@ManyToOne
	ProductionPlanStateType currentStateType;

	@OneToMany(orphanRemoval = true)
	List<RawMaterialRequirement> rawMaterialRequirements = new ArrayList<>();

	@OneToMany(orphanRemoval = true)
	List<SupplyRequirement> supplyRequirements = new ArrayList<>();

	String name = "";
	String details = "";
	Date date;

	public ProductionPlan() {

	}

	public ProductionPlan(String name, String details, Date date) {
		this.name = name;
		this.details = details;
		this.date = date;
	}

	public List<ProductTotal> getProductTotalList() {
		Map<Product, Integer> productTotalMap = new HashMap<Product, Integer>();
		for(ProductionPlanDetail auxProductionPlanDetail : getPlanDetails()) {
			for(OrderDetail auxOrderDetail : auxProductionPlanDetail.getOrder().getDetails()) {
				Integer totalUnits = productTotalMap.get(auxOrderDetail.getProduct());
				productTotalMap.put(auxOrderDetail.getProduct(), (totalUnits == null) ? auxOrderDetail.getUnits() : totalUnits + auxOrderDetail.getUnits());
			}
		}
		List<ProductTotal> productTotalList = new ArrayList<ProductTotal>();
		for (Map.Entry<Product, Integer> entry : productTotalMap.entrySet()) {
			Product product = entry.getKey();
			Integer totalUnits = entry.getValue();
			ProductTotal productTotal = new ProductTotal(product, totalUnits);
			productTotalList.add(productTotal);
		}
		return productTotalList;// devuelve el productTotalList lleno con todos los productos sin repetir y con el total, que conforman el plan de produccion
	}

	public ProductTotal getProductTotal(Product product) {
		for(ProductTotal productTotal : getProductTotalList()) {
			if(productTotal.getProduct().equals(product)) {
				return productTotal;
			}
		}
		return null;
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

	public void setCurrentStateType(ProductionPlanStateType currentStateType) {
		this.currentStateType = currentStateType;
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

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
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