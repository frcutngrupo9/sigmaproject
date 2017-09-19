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

import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.service.ProductionOrderRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;
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
	private ProductionOrderRepository productionOrderRepository;
	@WireVariable
	private ProductionPlanRepository productionPlanRepository;
	@WireVariable
	private ProductionPlanStateTypeRepository productionPlanStateTypeRepository;

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

			// se verifica si el calculo de las fechas de los detalles es igual al atributo fechas
			Date startDate = getProductionPlanStartDate();
			if(currentProductionPlan.getDateStart() == null) {
				currentProductionPlan.setDateStart(startDate);
			} else {
				if(startDate!=null && startDate.compareTo(currentProductionPlan.getDateStart())!=0) {
					// si no es el mismo el calculo se lo reemplaza y se guarda
					currentProductionPlan.setDateStart(startDate);
					currentProductionPlan = productionPlanRepository.save(currentProductionPlan);
				}
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
	private Date getProductionPlanFinishDate() {
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

	// busca el  primero de los inicios de ordenes de produccion
	private Date getProductionPlanStartDate() {
		Date date = null;
		for(ProductionOrder each : productionOrderList) {
			if(each.getDateStart() != null) {
				if(date == null) {
					date = each.getDateStart();
				} else {
					if(each.getDateStart().before(date)) {
						date = each.getDateStart();
					}
				}
			}
		}
		return date;
	}

	// busca la primera fecha de inicio real de ordenes de produccion
	private Date getProductionPlanStartRealDate() {
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
	private Date getProductionPlanFinishRealDate() {
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

	public boolean isProductionPlanStateCancel() {
		ProductionPlanStateType lastProductionPlanState = currentProductionPlan.getCurrentStateType();
		return lastProductionPlanState != null && lastProductionPlanState.getName().equals("Cancelado");
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
		// verifica que el valor del Timebox este entre la hora de inicio y fin del dia
		Date productionPlanStartTime = productionPlanStartTimebox.getValue();
		if(productionPlanStartTime!=null) {
			Calendar calTimebox = Calendar.getInstance();
			calTimebox.setTime(productionPlanStartTimebox.getValue());
			int hour = calTimebox.get(Calendar.HOUR_OF_DAY);
			int minute = calTimebox.get(Calendar.MINUTE);
			if(hour<ProductionDateTimeHelper.getFirstHourOfDay() || hour>=ProductionDateTimeHelper.getLastHourOfDay()) {
				// si la hora se sale del rango se reinicia a la primer hora
				hour = ProductionDateTimeHelper.getFirstHourOfDay();
				minute = ProductionDateTimeHelper.getFirstMinuteOfDay();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(productionPlanStartTime);
				calendar.set(Calendar.HOUR_OF_DAY, hour);
				calendar.set(Calendar.MINUTE, minute);
				productionPlanStartTimebox.setValue(calendar.getTime());
			}

		}

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
					productionOrder.sortDetailsByProcessTypeSequence();
					productionOrder.setDateStart(productionOrderStartDate);// la fecha fin se calcula al asignar fecha inicio
					productionOrder.updateDetailDates(productionOrderStartDate);
					// se extrae la fecha calculada
					productionOrderFinishDate = productionOrder.getDateFinish();
					previousStartDate = productionOrderStartDate;
				} else {
					// si el valor de secuencia anterior es igual al actual se inician al mismo tiempo y se guarda el que finalice mas tarde para usarlo como inicio del proximo
					if(sequence == productionOrder.getSequence()) {
						productionOrder.setDateStart(previousStartDate);
						productionOrder.updateDetailDates(previousStartDate);
						Date currentFinishDate = productionOrder.getDateFinish();// se extrae la fecha calculada
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
						productionOrder.setDateStart(productionOrderStartDate);
						productionOrder.updateDetailDates(productionOrderStartDate);
						productionOrderFinishDate = productionOrder.getDateFinish();// se extrae la fecha calculada
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

	@Listen("onClick = #productionPlanRequirementButton")
	public void productionPlanRequirementButtonClick() {
		Executions.getCurrent().setAttribute("selected_production_plan", currentProductionPlan);
		Include include = (Include) Selectors.iterable(this.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/requirement_plan_creation.zul");
	}
}
