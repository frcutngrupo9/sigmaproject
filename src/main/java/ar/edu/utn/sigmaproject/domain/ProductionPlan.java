/*
 * The MIT License
 *
 * Copyright (C) 2017 SigmaProject.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

		return updateDateStart();
	}

	private Date updateDateStart() {
		// busca la fecha de inicio de la primer orden de produccion
		ProductionOrder result = null;
		for(ProductionOrder each : productionOrderList) {
			if(result != null) {
				Date date = result.getStartDateFromDetails();
				if(date!=null && date.after(each.getStartDateFromDetails())) {
					result = each;
				}
			} else {
				result = each;
			}
		}
		if(result != null) {
			// actualiza la fecha del plan
			dateStart = result.getStartDateFromDetails();
			return dateStart;
		}
		dateStart = null;// si ninguna orden tiene fecha
		return dateStart;
	}

	public Date getDateFinish() {
		// busca la fecha de fin de la ultima orden de produccion
		ProductionOrder result = null;
		for(ProductionOrder each : productionOrderList) {
			if(result != null) {
				Date date = result.getFinishDateFromDetails();
				if(date!=null && date.before(each.getFinishDateFromDetails())) {
					result = each;
				}
			} else {
				result = each;
			}
		}
		if(result != null) {
			// actualiza la fecha del plan
			dateStart = result.getStartDateFromDetails();
			return dateStart;
		}
		dateStart = null;// si ninguna orden tiene fecha
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
	
	public String getDeviationText() {
		Date dateStart = getDateStart();
		if(dateStart == null) {
			return "No hay Fecha Inicio";
		}
		Date dateStartReal = getDateStartReal();
		if(dateStartReal == null) {
			return "No hay Fecha Inicio Real";
		}
		if(dateStart.before(dateStartReal)) {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			String dateStartFormatted = dateFormat.format(dateStart);
			String dateStartRealFormatted = dateFormat.format(dateStartReal);
			if(dateStartFormatted.equals(dateStartRealFormatted)) {
				return "Esta Puntual";
			}
			return "Esta Retrasado";
		} else {
			return "Esta Adelantado";
		}
	}
	
	public Date getDateStartReal() {
		// busca la primera fecha de inicio real de ordenes de produccion
		Date date = null;
		for(ProductionOrder each : productionOrderList) {
			Date startRealDate = each.getDateStartReal();
			if(startRealDate != null) {
				if(date == null) {
					date = startRealDate;
				} else {
					if(startRealDate.before(date)) {
						date = startRealDate;
					}
				}
			}
		}
		return date;
	}
	
	public List<Product> getProductList() {
		List<Product> productList = new ArrayList<Product>();
		for(ProductionOrder each : productionOrderList) {
			productList.add(each.getProduct());
		}
		return productList;
	}
}