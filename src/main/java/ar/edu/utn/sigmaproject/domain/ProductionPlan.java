package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
public class ProductionPlan  implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@OneToMany(mappedBy = "productionPlan", cascade = CascadeType.ALL)
	@OrderColumn(name = "detail_index")
	List<ProductionPlanDetail> planDetails = new ArrayList<>();

	@OneToMany(mappedBy = "productionPlan", cascade = CascadeType.ALL)
	@OrderBy("date")
	List<ProductionPlanState> states = new ArrayList<>();

	@ManyToOne
	ProductionPlanStateType currentStateType;

	@OneToMany(mappedBy = "productionPlan", cascade = CascadeType.ALL)
	List<RawMaterialRequirement> rawMaterialRequirements = new ArrayList<>();

	@OneToMany(mappedBy = "productionPlan")
	List<ProductionOrder> orders = new ArrayList<>();

	@OneToMany(mappedBy = "productionPlan")
	List<SupplyRequirement> supplyRequirements = new ArrayList<>();

	String name = "";
	String details = "";
	Date date;

	public ProductionPlan(String name, String details, Date date) {
		this.name = name;
		this.details = details;
		this.date = date;
	}

	public List<ProductTotal> getProductTotalList() {
		List<ProductTotal> productTotalList = new ArrayList<>();// se empieza con una lista vacia
		for (ProductionPlanDetail auxProductionPlanDetail : getPlanDetails()) {
			for (OrderDetail auxOrderDetail : auxProductionPlanDetail.getOrder().getDetails()) {// por cada detalle del pedido, observamos si el producto ya esta en la lista, si lo esta sumamos su cantidad y, si no esta lo agregamos
				Boolean is_in_list = false;
				Integer order_detail_units = auxOrderDetail.getUnits();
				for (ProductTotal productTotal : productTotalList) {
					if (productTotal.getId().equals(auxOrderDetail.getProduct().getId())) {// si esta
						is_in_list = true;
						productTotal.setTotalUnits(productTotal.getTotalUnits() + order_detail_units);// sumamos su cantidad con la existente
						break;
					}
				}
				if (!is_in_list) {// no esta, por lo tanto agregamos el producto a la lista total
					ProductTotal productTotal = new ProductTotal(auxOrderDetail.getProduct());
					productTotal.setTotalUnits(order_detail_units);// el primer valor son el total de unidades del detalle de pedido
					productTotalList.add(productTotal);
				}
			}
			// si es el primer loop del productionPlanDetailList entonces la lista productTotalList deberia estar llena solo con los productos
			// del primer pedido, en el siguiente loop se sumaran los que ya estan y agregaran los nuevos
		}
		return productTotalList;// devuelve el productTotalList lleno con todos los productos sin repetir y con el total, que conforman el plan de produccion
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

	public List<RawMaterialRequirement> getRawMaterialRequirements() {
		return rawMaterialRequirements;
	}

	public void setRawMaterialRequirements(List<RawMaterialRequirement> rawMaterialRequirements) {
		this.rawMaterialRequirements = rawMaterialRequirements;
	}

	public List<ProductionOrder> getOrders() {
		return orders;
	}

	public void setOrders(List<ProductionOrder> orders) {
		this.orders = orders;
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