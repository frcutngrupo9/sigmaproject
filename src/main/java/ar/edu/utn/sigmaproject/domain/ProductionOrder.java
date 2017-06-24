package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.xml.datatype.Duration;

import ar.edu.utn.sigmaproject.util.ProductionDateTimeHelper;

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

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "productionOrder", targetEntity = ProductionOrderDetail.class)
	@OrderColumn(name = "detail_index")
	List<ProductionOrderDetail> details = new ArrayList<>();

	Integer sequence = 0;
	Integer number = 0;
	Integer units = 0;
	Date dateStart = null;
	Date dateFinish = null;
	Date dateStartReal = null;
	Date dateFinishReal = null;
	Date dateMaterialsWithdrawal = null;
	ProductionOrderStateType currentStateType = null;

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
		this.currentStateType = state.getProductionOrderStateType();
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
		updateDetailDates(dateStart);// se calculan las fechas de los detalles automaticamente
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
		return currentStateType;
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
		currentStateType = state.getProductionOrderStateType();
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
		// este metodo no tiene en cuenta el tiempo que se debe restar a causa de los procesos cancelados
		//		if(product.getDurationTotal() != null) {
		//			return product.getDurationTotal().multiply(units);
		//		}
		return getDurationTotalFromDetails();
	}

	private Duration getDurationTotalFromDetails() {
		// este metodo si tiene en cuenta el tiempo que se debe restar a causa de los procesos cancelados
		Duration duration = null;
		for(ProductionOrderDetail each : getDetails()) {
			if(each.getState() != ProcessState.Cancelado) {// no suma si esta cancelado
				if(duration == null) {// primera vez
					duration = each.getTimeTotal();
				} else {
					duration = duration.add(each.getTimeTotal());
				}
			}
		}
		return duration;
	}

	public Date getDateMaterialsWithdrawal() {
		return dateMaterialsWithdrawal;
	}

	public void setDateMaterialsWithdrawal(Date dateMaterialsWithdrawal) {
		this.dateMaterialsWithdrawal = dateMaterialsWithdrawal;
	}

	public Map<ProcessType, List<ProductionOrderDetail>> getProcessTypeMap() {
		// devuelve un map en que la llave es el ProcessType y el valor son los ProductionOrderDetail que referencian a un Process que referencia a ese ProcessType
		Map<ProcessType, List<ProductionOrderDetail>> processTypeMap = new HashMap<ProcessType, List<ProductionOrderDetail>>();
		for(ProductionOrderDetail each : getDetails()) {
			ProcessType processType = each.getProcess().getType();
			List<ProductionOrderDetail> list = processTypeMap.get(processType);
			if(list == null) {
				list = new ArrayList<ProductionOrderDetail>();
			} else {
				list.add(each);
			}
			processTypeMap.put(processType, list);
		}
		return processTypeMap;
	}

	public List<ProcessType> getProcessTypeList() {
		Set<ProcessType> processTypeSet = new HashSet<ProcessType>();
		for(ProductionOrderDetail eachProductionOrderDetail : getDetails()) {
			processTypeSet.add(eachProductionOrderDetail.getProcess().getType());// garantiza que los tipo de procesos no se repitan
		}
		List<ProcessType> list = new ArrayList<ProcessType>();
		for (ProcessType eachProcessType : processTypeSet) {
			list.add(eachProcessType);
		}
		return list;
	}

	private void updateDetailDates(Date startDate) {
		Date finishDate = null;
		if(startDate != null) {
			for(ProductionOrderDetail each : getDetails()) {
				if(each.getState() != ProcessState.Cancelado) {// solo se calcula para los procesos que no esten cancelados
					if(finishDate == null) {// si es la primera vez que ingresa
						finishDate = ProductionDateTimeHelper.getFinishDate(startDate, each.getTimeTotal());
					} else {
						// el inicio de la actual es al finalizar la ultima
						startDate = finishDate;
						finishDate = ProductionDateTimeHelper.getFinishDate(startDate, each.getTimeTotal());
					}
					each.setDateStart(startDate);
					each.setDateFinish(finishDate);
				} else {
					each.setDateStart(null);
					each.setDateFinish(null);
					// por las dudas borramos tambien las fechas reales
					each.setDateStartReal(null);
					each.setDateFinishReal(null);
				}
			}
		} else {
			for(ProductionOrderDetail each : getDetails()) {
				each.setDateStart(null);
				each.setDateFinish(null);
			}
		}
		setDateFinish(finishDate);// se usa la ultima fecha como el fin de la orden de produccion
	}


	public Date getStartRealDateFromDetails() {
		Date date = null;
		for(ProductionOrderDetail each : getDetails()) {
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

	public Date getFinishRealDateFromDetails() {
		// solo si todos tienen fecha fin e inicio real
		Date date = null;
		for(ProductionOrderDetail each : getDetails()) {
			Date finishRealDate = each.getDateFinishReal();
			Date startRealDate = each.getDateStartReal();
			if(finishRealDate == null || startRealDate == null) {
				return null;
			}
			if(finishRealDate != null) {
				if(date == null) {
					date = finishRealDate;
				} else {
					if(finishRealDate.after(date)) {
						date = finishRealDate;
					}
				}
			}
		}
		return date;
	}

	public Date getStartDateFromDetails() {
		Date date = null;
		for(ProductionOrderDetail each : getDetails()) {
			Date startDate = each.getDateStart();
			if(startDate != null) {
				if(date == null) {
					date = startDate;
				} else {
					if(startDate.before(date)) {
						date = startDate;
					}
				}
			}
		}
		return date;
	}

	public Date getFinishDateFromDetails() {
		// solo si todos tienen fecha fin e inicio
		Date date = null;
		for(ProductionOrderDetail each : getDetails()) {
			Date finishDate = each.getDateFinish();
			Date startDate = each.getDateStart();
			if(finishDate == null || startDate == null) {
				return null;
			}
			if(date == null) {
				date = finishDate;
			} else {
				if(finishDate.after(date)) {
					date = finishDate;
				}
			}
		}
		return date;
	}
}
