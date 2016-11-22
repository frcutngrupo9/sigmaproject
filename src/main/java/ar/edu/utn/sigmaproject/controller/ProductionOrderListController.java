package ar.edu.utn.sigmaproject.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.Duration;

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
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeRepository;

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

	public String getWorkerName(ProductionOrder productionOrder) {
		if(productionOrder == null) {
			return "";
		} else {
			if(productionOrder.getWorker() != null) {
				return productionOrder.getWorker().getName();
			} else {
				return "[no asignado]";
			}
		}
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
				if(productionOrderDetail.isFinished()) {
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
		sortProductionOrderListBySequenceAndUpdateDates();
	}

	@Listen("onChange = #productionPlanStartTimebox")
	public void productionPlanStartTimeboxChange() {
		sortProductionOrderListBySequenceAndUpdateDates();
	}

	@Listen("onEditProductionOrderSequence = #productionOrderGrid")
	public void doEditProductionOrderSequence(ForwardEvent evt) {
		ProductionOrder data = (ProductionOrder) evt.getData();// obtiene el objeto pasado por parametro
		Spinner origin = (Spinner)evt.getOrigin().getTarget();
		InputEvent inputEvent = (InputEvent) evt.getOrigin();
		origin.setValue(Integer.valueOf(inputEvent.getValue()));
		Integer sequence = (Integer)origin.intValue();
		data.setSequence(sequence);// carga al objeto el valor actualizado del elemento web
		sortProductionOrderListBySequenceAndUpdateDates();
	}

	private void sortProductionOrderListBySequenceAndUpdateDates() {
		//se ordena la lista por secuencia
		sortProductionOrderListBySequence();
		// selecciona el primer valor de secuencia y le asigna como fecha de inicio el valor seleccionado, y calcula las demas fechas de los restantes ordenes y se las asigna
		Date productionPlanStartDate = getTimeboxDate();
		if(productionPlanStartDate != null) {
			// calcula todas las fechas y las agrega a las ordenes de produccion
			Date productionOrderFinishDate = null;
			for(ProductionOrder productionOrder : productionOrderList) {
				Date productionOrderStartDate;
				if(productionOrderFinishDate == null) {// si es la primera fecha en asignarse
					productionOrderStartDate = productionPlanStartDate;
				} else {
					productionOrderStartDate = productionOrderFinishDate;
				}
				productionOrderFinishDate = getFinishDate(productionOrderStartDate, productionOrder.getDurationTotal());
				productionOrder.setDateStart(productionOrderStartDate);
				productionOrder.setDateFinish(productionOrderFinishDate);
			}
			// se guarda la ultima fecha como el fin de plan de produccion
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

	public Date getFinishDate(Date startDate, Duration time) {
		int firstHourOfDay = 9;// horario en el que se empieza a trabajar
		int firstMinuteOfDay = 0;
		int lastHourOfDay = 18;// horario en el que se termina de trabajar
		int lastMinuteOfDay = 0;
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		// primero se comprueba que la fecha de inicio se encuentre dentro del horario
		if(cal.get(Calendar.HOUR_OF_DAY) < firstHourOfDay ||
				cal.get(Calendar.HOUR_OF_DAY) >= lastHourOfDay) {
			return null;
		} else {// si esta dentro del horario en horas pero no en minutos
			if(firstMinuteOfDay!=0 || lastMinuteOfDay!=0) {//si estan configurados minutos en el horario
				//solo es valido si la hora es igual a la de comienzo o igual al previo al final
				if(cal.get(Calendar.HOUR_OF_DAY) == firstHourOfDay) {
					if(cal.get(Calendar.MINUTE) < firstMinuteOfDay) {
						return null;
					}
				}
				if(cal.get(Calendar.HOUR_OF_DAY) == lastHourOfDay-1) {
					if(cal.get(Calendar.MINUTE) >= lastMinuteOfDay) {
						return null;
					}
				}
			}
		}

		if(time != null) {
			int hours = time.getHours();
			int minutes = time.getMinutes();// puede ser que los minutos sean mayor que 60, ya que se realizaron multiplicaciones sobre time
			while(minutes >= 60) {// se vuelven los minutos a menos de 60
				minutes -= 60;
				hours += 1;
			}
			while(hours > 0) {
				hours -= 1;
				cal.add(Calendar.HOUR_OF_DAY, 1);// agrega horas
				// si luego de agregar 1 hora, el tiempo supera el horario de finalizacion
				// se resta hasta el horario de finalizacion y ese tiempo se suma al dia siguiente
				if(cal.get(Calendar.HOUR_OF_DAY) >= lastHourOfDay) {// caso supere el horario de finalizacion
					if(cal.get(Calendar.MINUTE) > 0) {
						// se agregan los minutos que superan al horario de finalizacion
						minutes += cal.get(Calendar.MINUTE);
						while(minutes >= 60) {
							minutes -= 60;
							hours += 1;
						}
					}
					// agrega 1 dia y resetea el horario
					cal.add(Calendar.DAY_OF_MONTH, 1);
					cal.set(Calendar.HOUR_OF_DAY, firstHourOfDay);
					cal.set(Calendar.MINUTE, firstMinuteOfDay);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
				}
			}
			// agrega los minutos
			if(minutes > 0) {
				cal.add(Calendar.MINUTE, minutes);
				// puede ser que al agregar los minutos se supere el horario de finalizacion
				if(cal.get(Calendar.HOUR_OF_DAY) >= lastHourOfDay) {// caso supere el horario de finalizacion
					cal.add(Calendar.DAY_OF_MONTH, 1);// agrega 1 dia
					cal.set(Calendar.HOUR_OF_DAY, firstHourOfDay);
					cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));// los minutos que superaron el horario de finalizacion se agregan al horario de inicio
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);

				}
			}
			return cal.getTime();
		}
		return startDate;// si el tiempo es null se devuelve la misma fecha de inicio
	}
}
