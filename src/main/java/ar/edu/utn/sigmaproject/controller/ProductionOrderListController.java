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
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;

import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.domain.ProductionOrderState;
import ar.edu.utn.sigmaproject.domain.ProductionOrderStateType;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.service.ProductionOrderRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateTypeRepository;
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

	// services
	@WireVariable
	private ProductionOrderRepository productionOrderRepository;
	@WireVariable
	private ProductionPlanRepository productionPlanRepository;
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
		refreshView();
	}

	private void refreshView() {
		if(currentProductionPlan != null) {
			currentProductionPlan = productionPlanRepository.findOne(currentProductionPlan.getId());
			productionOrderList = sortProductionOrderListByNumber(currentProductionPlan.getProductionOrderList());
			//sortProductionOrderListBySequence();
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
			updatePlanDateboxes();
		}
	}
	
	public List<ProductionOrder> sortProductionOrderListByNumber(List<ProductionOrder> productionOrderList) {
		Comparator<ProductionOrder> comp = new Comparator<ProductionOrder>() {
			@Override
			public int compare(ProductionOrder a, ProductionOrder b) {
				return a.getNumber().compareTo(b.getNumber());
			}
		};
		Collections.sort(productionOrderList, comp);
		return productionOrderList;
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
		currentProductionPlan.setDateStart(productionPlanStartDatebox.getValue());
		productionOrderList = productionOrderRepository.save(productionOrderList);
		currentProductionPlan = productionPlanRepository.save(currentProductionPlan);
		Clients.showNotification("Plan y Ordenes de Produccion guardadas");
		refreshView();
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

	@Listen("onClick = #productionPlanRequirementButton")
	public void productionPlanRequirementButtonClick() {
		Executions.getCurrent().setAttribute("selected_production_plan", currentProductionPlan);
		Include include = (Include) Selectors.iterable(this.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/requirement_plan_creation.zul");
	}

	@Listen("onProductionOrderStartDateboxChange = #productionOrderGrid")
	public void doProductionOrderStartDateboxChange(ForwardEvent evt) {
		if(isEditionAllowed() == false) {
			alert("No se puede modificar, el plan esta " + currentProductionPlan.getCurrentStateType().getName() + ".");
			refreshView();
			return;
		}
		ProductionOrder data = (ProductionOrder) evt.getData();// obtiene el objeto pasado por parametro
		Datebox element = (Datebox)evt.getOrigin().getTarget();
		Date date = (Date)element.getValue();
		// se busca el elemento Timebox para realizar la union
		Cell cell = (Cell)element.getParent();
		changeStartDates(false, cell, date, data);
	}

	@Listen("onProductionOrderStartTimeboxChange = #productionOrderGrid")
	public void doProductionOrderStartTimeboxChange(ForwardEvent evt) {
		if(isEditionAllowed() == false) {
			alert("No se puede modificar, el plan esta " + currentProductionPlan.getCurrentStateType().getName() + ".");
			refreshView();
			return;
		}
		ProductionOrder data = (ProductionOrder) evt.getData();// obtiene el objeto pasado por parametro
		Timebox element = (Timebox)evt.getOrigin().getTarget();
		// si el datebox tiene valor null al cambiar el timebox, no se realiza ningun accion
		Datebox datebox = (Datebox)element.getParent().getChildren().get(0);
		if(datebox.getValue() ==  null) {
			return;
		}
		Date time = (Date)element.getValue();
		// se verifica que la hora este dentro del horario de trabajo
		if(time!=null && ProductionDateTimeHelper.isOutsideWorkingHours(element.getValue())) {
			alert("Error en la Hora. Debe seleccionar un horario entre las " + ProductionDateTimeHelper.getFormattedFirst() + " hs y las " + ProductionDateTimeHelper.getFormattedLast() + " hs");
			// se regresa al valor original si no es null
			if(data.getDateStart() != null) {
				element.setValue(data.getDateStart());
			} else {
				element.setValue(getFirstHourMinuteOfDay());
			}
			return;
		}
		// se busca el elemento Datebox para realizar la union
		Cell cell = (Cell)element.getParent();
		changeStartDates(true, cell, time, data);
	}

	private void changeStartDates(boolean isTimebox, Cell cell, Date date, ProductionOrder data) {
		Date dateStart = null;
		Timebox timebox = (Timebox)cell.getChildren().get(1);
		Datebox datebox = (Datebox)cell.getChildren().get(0);
		if(isTimebox) {
			dateStart = getTimeboxDate(datebox.getValue(), date);
		} else {
			dateStart = getTimeboxDate(date, timebox.getValue());
		}
		data.setDateStart(dateStart);
		data.sortDetailsByProcessTypeSequence();
		data.updateDetailDates(dateStart);// calcula las demas fechas
		changeFinishDatebox(cell, data.getDateFinish());
		// si es null el dateStart se reinicia el timebox
		if(dateStart == null) {
			timebox.setValue(getFirstHourMinuteOfDay());
			// se borra el valor del datebox
			datebox.setValue(null);
		}
		// se actualiza el estado de la orden
		updateProductionOrderState(data, cell);
		// se actualizan las fechas del plan
		updatePlanDateboxes();
	}

	private void changeFinishDatebox(Cell cell, Date date) {
		// setea la fecha en el datebox finish
		Row row = (Row)cell.getParent();
		Datebox dateboxFinish = (Datebox)row.getChildren().get(2);
		dateboxFinish.setValue(date);
	}

	private Date getTimeboxDate(Date startDate, Date startTime) {
		// devuelve la union entre la fecha del datebox y la hora del timebox
		if(startTime!=null && startDate!=null) {
			Calendar calStartDate = Calendar.getInstance();
			calStartDate.setTime(startDate);
			Calendar calTimebox = Calendar.getInstance();
			calTimebox.setTime(startTime);
			calStartDate.set(Calendar.HOUR_OF_DAY, calTimebox.get(Calendar.HOUR_OF_DAY));
			calStartDate.set(Calendar.MINUTE, calTimebox.get(Calendar.MINUTE));
			return calStartDate.getTime();
		}
		return null;
	}

	public Date getTimeboxValue(Date startDate) {
		if(startDate == null) {
			return getFirstHourMinuteOfDay();
		}
		return startDate;
	}

	private Date getFirstHourMinuteOfDay() {
		// devuelve el horario de inicio del dia
		int hour = ProductionDateTimeHelper.getFirstHourOfDay();
		int minute = ProductionDateTimeHelper.getFirstMinuteOfDay();
		int second = 0;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		return calendar.getTime();
	}

	private void updateProductionOrderState(ProductionOrder productionOrder, Cell cell) {
		ProductionOrderStateType stateType = getProductionOrderCurrentStateType(productionOrder);
		// solo cambia si es diferente del guardado
		if(!stateType.getName().equalsIgnoreCase(productionOrder.getCurrentStateType().getName())) {
			productionOrder.setState(productionOrderStateRepository.save(new ProductionOrderState(stateType, new Date())));
			Label labelState = (Label)cell.getParent().getChildren().get(7);
			labelState.setValue(stateType.getName());
		}
	}

	private void updatePlanDateboxes() {
		productionPlanStartDatebox.setValue(getDateFromList(true, false));
		productionPlanFinishDatebox.setValue(getDateFromList(false, false));
		productionPlanStartRealDatebox.setValue(getDateFromList(true, true));
		productionPlanFinishRealDatebox.setValue(getDateFromList(false, true));
	}

	public Date getDateFromList(boolean isStart, boolean isReal) {
		Date date = null;
		for(ProductionOrder each : productionOrderList) {
			Date eachDate = null;
			if(isStart) {
				if(isReal) {
					eachDate = each.getDateStartReal();
				} else {
					eachDate = each.getDateStart();
				}
			} else {
				if(isReal) {
					eachDate = each.getDateFinishReal();
				} else {
					eachDate = each.getDateFinish();
				}
			}
			if(eachDate != null) {
				if(date == null) {
					date = eachDate;
				} else {
					if(isStart) {
						if(eachDate.before(date)) {
							date = eachDate;
						}
					} else {
						if(eachDate.after(date)) {
							date = eachDate;
						}
					}
				}
			}
		}
		return date;
	}

	private ProductionOrderStateType getProductionOrderCurrentStateType(ProductionOrder productionOrder) {
		if(productionOrder.isReady()) {
			return productionOrderStateTypeRepository.findFirstByName("Preparada");
		} else {
			return productionOrderStateTypeRepository.findFirstByName("Registrada");
		}
	}

	private boolean isEditionAllowed() {
		// verifica que no se encuentre en los estados "En Ejecucion""Finalizado""Cancelado""Suspendido", sino devuelve falso
		ProductionPlanStateType currentStateType = currentProductionPlan.getCurrentStateType();
		String state = currentStateType.getName();
		if(state.equalsIgnoreCase("En Ejecucion") || state.equalsIgnoreCase("Finalizado") || state.equalsIgnoreCase("Cancelado")) {
			return false;
		}
		return true;
	}
}
