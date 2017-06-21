package ar.edu.utn.sigmaproject.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;

import ar.edu.utn.sigmaproject.domain.Machine;
import ar.edu.utn.sigmaproject.domain.MachineType;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.ProcessState;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.service.MachineRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeRepository;
import ar.edu.utn.sigmaproject.util.ProductionDateTimeHelper;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProductionOrderListController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox productionPlanNameTextbox;
	@Wire
	Datebox productionPlanCreationDatebox;
	@Wire
	Grid productionOrderGrid;
	@Wire
	Textbox productionPlanStateTypeTextbox;
	@Wire
	Button returnButton;
	@Wire
	Datebox productionPlanStartDatebox;
	@Wire
	Datebox productionPlanFinishDatebox;
	@Wire
	Datebox productionPlanStartRealDatebox;
	@Wire
	Datebox productionPlanFinishRealDatebox;
	@Wire
	Button saveButton;
	@Wire
	Button resetButton;
	@Wire
	Timebox productionPlanStartTimebox;

	// services
	@WireVariable
	private MachineRepository machineRepository;
	@WireVariable
	private ProductionOrderRepository productionOrderRepository;
	@WireVariable
	private ProductionPlanRepository productionPlanRepository;
	@WireVariable
	private ProductionPlanStateRepository productionPlanStateRepository;
	@WireVariable
	private ProductionPlanStateTypeRepository productionPlanStateTypeRepository;
	@WireVariable
	private ProductionOrderStateRepository productionOrderStateRepository;
	@WireVariable
	private ProductionOrderStateTypeRepository productionOrderStateTypeRepository;

	// atributes
	private ProductionPlan currentProductionPlan;

	// list
	private List<ProductionOrder> productionOrderList;

	// list models
	private ListModelList<ProductionOrder> productionOrderListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentProductionPlan = (ProductionPlan) Executions.getCurrent().getAttribute("selected_production_plan");
		if(currentProductionPlan == null) {throw new RuntimeException("ProductionPlan not found");}
		productionOrderList = productionOrderRepository.findByProductionPlan(currentProductionPlan);

		productionPlanNameTextbox.setDisabled(true);
		productionPlanCreationDatebox.setDisabled(true);
		productionPlanStateTypeTextbox.setDisabled(true);
		productionPlanFinishDatebox.setDisabled(true);
		productionPlanStartRealDatebox.setDisabled(true);
		productionPlanFinishRealDatebox.setDisabled(true);

		refreshView();
	}

	private void refreshView() {
		if(currentProductionPlan != null) {
			productionOrderList = productionOrderRepository.findByProductionPlan(currentProductionPlan);
			sortProductionOrderListBySequence();
			productionOrderListModel = new ListModelList<ProductionOrder>(productionOrderList);
			productionOrderGrid.setModel(productionOrderListModel);

			productionPlanNameTextbox.setText(currentProductionPlan.getName());
			productionPlanCreationDatebox.setValue(currentProductionPlan.getDateCreation());
			ProductionPlanStateType lastProductionPlanState = currentProductionPlan.getCurrentStateType();
			if(lastProductionPlanState != null) {
				productionPlanStateTypeTextbox.setText(lastProductionPlanState.getName().toUpperCase());
			} else {
				productionPlanStateTypeTextbox.setText("[Sin Estado]");
			}
			if(currentProductionPlan.getDateStart() == null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				cal.set(Calendar.HOUR_OF_DAY, 9);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				productionPlanStartTimebox.setValue(cal.getTime());
			} else {
				productionPlanStartTimebox.setValue(currentProductionPlan.getDateStart());
			}
			productionPlanStartDatebox.setValue(currentProductionPlan.getDateStart());
			productionPlanFinishDatebox.setValue(getProductionPlanFinishDate());
			productionPlanStartRealDatebox.setValue(getProductionPlanStartRealDate());
			productionPlanFinishRealDatebox.setValue(getProductionPlanFinishRealDate());
		}
	}

	// busca la ultima fecha de las finalizaciones de ordenes de produccion
	public Date getProductionPlanFinishDate() {
		Date date = null;
		for(ProductionOrder each : productionOrderList) {
			if(each.getDateFinish() != null) {
				if(date == null) {
					date = each.getDateFinish();
				} else {
					if(each.getDateFinish().after(date)) {
						date = each.getDateFinish();
					}
				}
			}
		}
		return date;
	}

	// busca la primera fecha de inicio real de ordenes de produccion
	public Date getProductionPlanStartRealDate() {
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

	// busca la ultima fecha de finalizacion real de ordenes de produccion en caso de que esten todas finalizadas
	public Date getProductionPlanFinishRealDate() {
		ProductionPlanStateType currentProductionPlanStateType = currentProductionPlan.getCurrentStateType();
		Date date = null;
		if(productionPlanStateTypeRepository.findOne(currentProductionPlanStateType.getId()).equals(productionPlanStateTypeRepository.findFirstByName("Finalizado"))) {// si esta finalizado el plan
			for(ProductionOrder each : productionOrderList) {
				Date finishRealDate = each.getDateFinishReal();
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
		}
		return date;
	}

	@Listen("onEditProductionOrder = #productionOrderGrid")
	public void doEditProductionOrder(ForwardEvent evt) {
		ProductionOrder productionOrder = (ProductionOrder) evt.getData();
		Executions.getCurrent().setAttribute("selected_production_order", productionOrder);
		Executions.getCurrent().setAttribute("selected_production_plan", currentProductionPlan);
		Include include = (Include) Selectors.iterable(evt.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/production_order_creation.zul");
	}

	public ListModel<ProductionOrderDetail> getProductionOrderDetailList(Product product) {// buscar todos los procesos del producto
		List<ProductionOrderDetail> list = new ArrayList<>();
		ProductionOrder aux = productionOrderRepository.findByProductionPlanAndProduct(currentProductionPlan, product);
		if(aux != null) {
			list = aux.getDetails();
		}
		return new ListModelList<>(list);
	}

	public String getIsFinished(boolean value) {
		if(value == true) {
			return "si";
		} else {
			return "no";
		}
	}

	public String getPercentComplete(ProductionOrder aux) {
		if(aux != null) {
			List<ProductionOrderDetail> productionOrderDetailList = aux.getDetails();
			int quantityFinished = 0;
			for(ProductionOrderDetail productionOrderDetail : productionOrderDetailList) {
				if(productionOrderDetail.getState()==ProcessState.Realizado) {
					quantityFinished += 1;
				}
			}
			double percentComplete;
			if(productionOrderDetailList.size() == 0) {
				percentComplete = 0;
			} else {
				percentComplete = (quantityFinished * 100) / productionOrderDetailList.size();
			}
			return percentComplete + " %";
		} else {
			return "";
		}
	}

	public String getMachineTypeName(ProductionOrderDetail productionOrderDetail) {
		String name = "Ninguna";
		Process process = productionOrderDetail.getProcess();
		ProcessType processType = process.getType();
		MachineType machineType = processType.getMachineType();
		if(machineType != null) {
			name = machineType.getName();
		}
		return name;
	}

	public String getMachineName(ProductionOrderDetail productionOrderDetail) {
		String name = "Ninguna";
		Machine machine = productionOrderDetail.getMachine();
		if(machine != null) {
			name = machine.getName();
		}
		return name;
	}

	public boolean isProductionPlanStateCancel() {
		ProductionPlanStateType lastProductionPlanState = currentProductionPlan.getCurrentStateType();
		return lastProductionPlanState != null && lastProductionPlanState.getName().equals("Cancelado");
	}

	public boolean isProductionOrderStateCancel(ProductionOrder aux) {
		if(aux == null) {
			return false;
		} else {
			if(aux.getCurrentStateType()!=null && aux.getCurrentStateType().getName().equals("Cancelada")) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Listen("onClick = #returnButton")
	public void returnButtonClick() {
		Include include = (Include) Selectors.iterable(this.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/production_plan_list.zul");
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		currentProductionPlan.setDateStart(getTimeboxDate());
		productionOrderList = productionOrderRepository.save(productionOrderList);
		currentProductionPlan = productionPlanRepository.save(currentProductionPlan);
		Clients.showNotification("Plan y Ordenes de Produccion guardadas");
		refreshView();
	}

	@Listen("onChange = #productionPlanStartDatebox")
	public void productionPlanStartDateboxChange() {
		sortProductionOrderListBySequenceAndRefreshDates();
	}

	@Listen("onChange = #productionPlanStartTimebox")
	public void productionPlanStartTimeboxChange() {
		sortProductionOrderListBySequenceAndRefreshDates();
	}

	@Listen("onEditProductionOrderSequence = #productionOrderGrid")
	public void doEditProductionOrderSequence(ForwardEvent evt) {
		ProductionOrder data = (ProductionOrder) evt.getData();// obtiene el objeto pasado por parametro
		Spinner origin = (Spinner)evt.getOrigin().getTarget();
		InputEvent inputEvent = (InputEvent) evt.getOrigin();
		origin.setValue(Integer.valueOf(inputEvent.getValue()));
		Integer sequence = (Integer)origin.intValue();
		data.setSequence(sequence);// carga al objeto el valor actualizado del elemento web
		sortProductionOrderListBySequenceAndRefreshDates();
	}

	private void sortProductionOrderListBySequenceAndRefreshDates() {
		//se ordena la lista por secuencia
		sortProductionOrderListBySequence();
		// selecciona el primer valor de secuencia y le asigna como fecha de inicio el valor seleccionado, y calcula las demas fechas de los restantes ordenes y se las asigna
		Date productionPlanStartDate = getTimeboxDate();
		if(productionPlanStartDate != null) {
			int sequence = 0;
			Date previousStartDate = null;
			// calcula todas las fechas y las agrega a las ordenes de produccion
			Date productionOrderFinishDate = null;
			for(ProductionOrder productionOrder : productionOrderList) {
				// las ordenes el mismo nro de secuencia, inician al mismo tiempo
				if(sequence == 0) {
					// si es la primera vez que ingresa
					Date productionOrderStartDate;
					if(productionOrderFinishDate == null) {// si es la primera fecha en asignarse
						productionOrderStartDate = productionPlanStartDate;
					} else {
						productionOrderStartDate = productionOrderFinishDate;// el inicio es al finalizar la ultima
					}
					productionOrderFinishDate = ProductionDateTimeHelper.getFinishDate(productionOrderStartDate, productionOrder.getDurationTotal());
					productionOrder.setDateStart(productionOrderStartDate);
					productionOrder.setDateFinish(productionOrderFinishDate);
					previousStartDate = productionOrderStartDate;
				} else {
					// si el valor de secuencia anterior es igual al actual se inician al mismo tiempo y se guarda el que finalice mas tarde para usarlo como inicio del proximo
					if(sequence == productionOrder.getSequence()) {
						productionOrder.setDateStart(previousStartDate);
						Date currentFinishDate = ProductionDateTimeHelper.getFinishDate(previousStartDate, productionOrder.getDurationTotal());
						productionOrder.setDateFinish(currentFinishDate);
						if(currentFinishDate.after(productionOrderFinishDate)) {// si la actual orden finaliza despues q la que anterior, la reemplaza
							productionOrderFinishDate = currentFinishDate;
						}
					} else {
						Date productionOrderStartDate;
						if(productionOrderFinishDate == null) {// si es la primera fecha en asignarse
							productionOrderStartDate = productionPlanStartDate;
						} else {
							productionOrderStartDate = productionOrderFinishDate;// el inicio es al finalizar la ultima
						}
						productionOrderFinishDate = ProductionDateTimeHelper.getFinishDate(productionOrderStartDate, productionOrder.getDurationTotal());
						productionOrder.setDateStart(productionOrderStartDate);
						productionOrder.setDateFinish(productionOrderFinishDate);
						previousStartDate = productionOrderStartDate;
					}
				}
				sequence = productionOrder.getSequence();
			}
			// se usa la ultima fecha como el fin de plan de produccion
			productionPlanFinishDatebox.setValue(productionOrderFinishDate);
		} else {
			// se ponen en null los dates de las ordenes de prod
			for(ProductionOrder productionOrder : productionOrderList) {
				productionOrder.setDateStart(null);
				productionOrder.setDateFinish(null);
			}
		}
		productionOrderListModel = new ListModelList<ProductionOrder>(productionOrderList);
		productionOrderGrid.setModel(productionOrderListModel);
	}

	private void sortProductionOrderListBySequence() {
		Comparator<ProductionOrder> comp = new Comparator<ProductionOrder>() {
			@Override
			public int compare(ProductionOrder a, ProductionOrder b) {
				return a.getSequence().compareTo(b.getSequence());
			}
		};
		Collections.sort(productionOrderList, comp);
	}

	private Date getTimeboxDate() {
		// devuelve la union entre la fecha del datebox y la hora del timebox
		Date productionPlanStartDate = productionPlanStartDatebox.getValue();
		Date productionPlanStartTime = productionPlanStartTimebox.getValue();
		if(productionPlanStartTime!=null && productionPlanStartDate!=null) {
			Calendar calStartDate = Calendar.getInstance();
			calStartDate.setTime(productionPlanStartDate);
			Calendar calTimebox = Calendar.getInstance();
			calTimebox.setTime(productionPlanStartTimebox.getValue());
			calStartDate.set(Calendar.HOUR_OF_DAY, calTimebox.get(Calendar.HOUR_OF_DAY));
			calStartDate.set(Calendar.MINUTE, calTimebox.get(Calendar.MINUTE));
			return calStartDate.getTime();
		}
		return null;
	}
	
//	private boolean isProductionPlanReady() {
//		// recorre todas las ordenes y en caso de que todas esten en estado preparada, devuelve verdadero
//		List<ProductionOrder> productionOrderList = productionOrderRepository.findByProductionPlan(currentProductionPlan);
//		for(ProductionOrder each : productionOrderList) {
//			if(!productionOrderStateTypeRepository.findOne(each.getCurrentStateType().getId()).equals(productionOrderStateTypeRepository.findFirstByName("Preparada"))) {
//				return false;
//			}
//		}
//		return true;
//	}
	
//	@Listen("onClick = #productionPlanLaunchButton")
//	public void productionPlanLaunchButtonClick() {
//		ProductionPlanStateType currentPlanState = productionPlanStateTypeRepository.findOne(currentProductionPlan.getCurrentStateType().getId());
//		ProductionPlanStateType planStateLanzado = productionPlanStateTypeRepository.findFirstByName("Lanzado");
//		ProductionPlanStateType planStateFinalizado = productionPlanStateTypeRepository.findFirstByName("Finalizado");
//		ProductionPlanStateType planStateEnEjecucion = productionPlanStateTypeRepository.findFirstByName("En Ejecucion");
//		if(currentPlanState.equals(planStateLanzado) || currentPlanState.equals(planStateEnEjecucion) || currentPlanState.equals(planStateFinalizado)) {
//			alert("No se puede lanzar un plan que ya esta lanzado o en un estado posterior.");
//			return;
//		}
//		currentProductionPlan.setDateStart(getTimeboxDate());
//		if(!isProductionPlanReady()) {
//			alert("No se puede lanzar hasta que todas Ordenes esten Preparadas.");
//			return;
//		}
//		if(!productionPlanStateTypeRepository.findOne(currentProductionPlan.getCurrentStateType().getId()).equals(productionPlanStateTypeRepository.findFirstByName("Abastecido"))) {
//			alert("No se puede lanzar el Plan hasta estar Abastecido.");
//			return;
//		}
//		ProductionPlanState state = new ProductionPlanState(planStateLanzado, new Date());
//		productionPlanStateRepository.save(state);
//		currentProductionPlan.setState(state);
//		productionPlanRepository.save(currentProductionPlan);
//		Clients.showNotification("Plan de Produccion Lanzado");
//		refreshView();
//	}
	
	@Listen("onClick = #productionPlanRequirementButton")
	public void productionPlanRequirementButtonClick() {
		Executions.getCurrent().setAttribute("selected_production_plan", currentProductionPlan);
		Include include = (Include) Selectors.iterable(this.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/requirement_plan_creation.zul");
	}
}
