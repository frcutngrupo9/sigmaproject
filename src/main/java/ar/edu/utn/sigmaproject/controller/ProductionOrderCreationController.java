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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.Duration;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.springframework.transaction.annotation.Transactional;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkex.zul.Jasperreport;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Machine;
import ar.edu.utn.sigmaproject.domain.MachineType;
import ar.edu.utn.sigmaproject.domain.ProcessState;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.domain.ProductionOrderMaterial;
import ar.edu.utn.sigmaproject.domain.ProductionOrderState;
import ar.edu.utn.sigmaproject.domain.ProductionOrderStateType;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.domain.ReportType;
import ar.edu.utn.sigmaproject.domain.Worker;
import ar.edu.utn.sigmaproject.service.MachineRepository;
import ar.edu.utn.sigmaproject.service.MachineTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderDetailRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.WorkerRepository;
import ar.edu.utn.sigmaproject.util.ProductionDateTimeHelper;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProductionOrderCreationController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox productionPlanNameTextbox;
	@Wire
	Textbox productionPlanStateTypeTextbox;
	@Wire
	Textbox productionOrderStateTypeTextbox;
	@Wire
	Textbox productCodeTextbox;
	@Wire
	Textbox productNameTextbox;
	@Wire
	Grid productionOrderDetailGrid;
	@Wire
	Intbox productUnitsIntbox;
	@Wire
	Datebox productionOrderStartDatebox;
	@Wire
	Datebox productionOrderFinishDatebox;
	@Wire
	Button saveButton;
	@Wire
	Button cancelButton;
	@Wire
	Button resetButton;
	@Wire
	Button generateDetailsButton;
	@Wire
	Listbox productionOrderSupplyListbox;
	@Wire
	Listbox productionOrderRawMaterialListbox;
	@Wire
	Button autoAssignButton;
	@Wire
	Image productImage;
	@Wire
	Listbox reportTypeListbox;
	@Wire
	Button createReportButton;
	@Wire
	Jasperreport reportBlockJasperreport;

	// services
	@WireVariable
	private ProductionOrderRepository productionOrderRepository;
	@WireVariable
	private ProductionOrderDetailRepository productionOrderDetailRepository;
	@WireVariable
	private ProductionOrderStateRepository productionOrderStateRepository;
	@WireVariable
	private ProductionOrderStateTypeRepository productionOrderStateTypeRepository;
	@WireVariable
	private MachineRepository machineRepository;
	@WireVariable
	private MachineTypeRepository machineTypeRepository;
	@WireVariable
	private WorkerRepository workerRepository;

	// atributes
	private ProductionOrder currentProductionOrder;
	private ProductionPlan currentProductionPlan;

	// list
	private List<ProductionOrderDetail> productionOrderDetailList;
	private List<Machine> machineList;

	// list models
	private ListModelList<ProductionOrderDetail> productionOrderDetailListModel;
	private ListModelList<ProductionOrderMaterial> productionOrderSupplyListModel;
	private ListModelList<ProductionOrderMaterial> productionOrderRawMaterialListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentProductionOrder = (ProductionOrder) Executions.getCurrent().getAttribute("selected_production_order");
		if(currentProductionOrder == null) {throw new RuntimeException("ProductionOrder not found");}
		currentProductionPlan = (ProductionPlan) Executions.getCurrent().getAttribute("selected_production_plan");
		if(currentProductionPlan == null) {throw new RuntimeException("ProductionPlan not found");}
		productionOrderDetailList = currentProductionOrder.getDetails();
		machineList = machineRepository.findAll();
		refreshView();
	}

	private void refreshView() {
		org.zkoss.image.Image img = null;
		try {
			img = new AImage("", currentProductionOrder.getProduct().getImageData());
		} catch (IOException exception) { }
		if(img != null) {
			productImage.setHeight("75px");
			productImage.setWidth("75px");
			productImage.setStyle("margin: 8px");
		} else {
			productImage.setHeight("0px");
			productImage.setWidth("0px");
			productImage.setStyle("margin: 0px");
		}
		productImage.setContent(img);
		sortProductionOrderDetailListByProcessTypeSequence();
		productionPlanNameTextbox.setText(currentProductionPlan.getName());
		ProductionPlanStateType lastProductionPlanStateType = currentProductionPlan.getCurrentStateType();
		if(lastProductionPlanStateType != null) {
			productionPlanStateTypeTextbox.setText(lastProductionPlanStateType.getName());
		} else {
			productionPlanStateTypeTextbox.setText("[Sin Estado]");
		}
		productionOrderStateTypeTextbox.setText(currentProductionOrder.getCurrentStateType().getName());
		productNameTextbox.setText(currentProductionOrder.getProduct().getName());
		productCodeTextbox.setText(currentProductionOrder.getProduct().getCode());
		productUnitsIntbox.setValue(currentProductionOrder.getUnits());
		productionOrderStartDatebox.setValue(currentProductionOrder.getDateStart());
		productionOrderFinishDatebox.setValue(currentProductionOrder.getDateFinish());
		refreshProductionOrderDetailGridView();
		refreshProductionOrderOrderSupplyAndRawMaterialListbox();
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		resetButton.setDisabled(false);
	}

	private void refreshProductionOrderDetailGridView() {
		productionOrderDetailListModel = new ListModelList<ProductionOrderDetail>(productionOrderDetailList);
		productionOrderDetailGrid.setModel(productionOrderDetailListModel);
	}

	private void refreshProductionOrderOrderSupplyAndRawMaterialListbox() {
		List<ProductionOrderMaterial> productionOrderSupplyList = currentProductionOrder.getProductionOrderSupplies();
		List<ProductionOrderMaterial> productionOrderRawMaterialList = currentProductionOrder.getProductionOrderRawMaterials();
		productionOrderSupplyListModel = new ListModelList<ProductionOrderMaterial>(productionOrderSupplyList);
		productionOrderRawMaterialListModel = new ListModelList<ProductionOrderMaterial>(productionOrderRawMaterialList);
		productionOrderSupplyListbox.setModel(productionOrderSupplyListModel);
		productionOrderRawMaterialListbox.setModel(productionOrderRawMaterialListModel);
	}

	private Integer getNewProductionOrderNumber() {
		Integer lastValue = 0;
		List<ProductionOrder> list = productionOrderRepository.findByProductionPlan(currentProductionPlan);
		for(ProductionOrder each : list) {
			if(each.getNumber()!=null && each.getNumber()>lastValue) {
				lastValue = each.getNumber();
			}
		}
		return lastValue + 1;
	}

	private boolean isEditionAllowed() {
		// no se puede modificar si el plan esta Cancelado, o Finalizado. Si esta Suspendido se puede modificar para solucionar problemas de maquinas en reparacion o empreados ausentes.
		ProductionPlanStateType currentStateType = currentProductionPlan.getCurrentStateType();
		if(currentStateType.getName().equalsIgnoreCase("Finalizado") || currentStateType.getName().equalsIgnoreCase("Cancelado")) {
			return false;
		}
		return true;
	}

	@Transactional
	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(!isEditionAllowed()) {
			alert("No se puede modificar porque el Plan de Produccion esta Cancelado, Lanzado, En Ejecucion o Finalizado.");
			return;
		}
		String stateType = currentProductionOrder.getCurrentStateType().getName();
		if(stateType.equalsIgnoreCase("Iniciada") || stateType.equalsIgnoreCase("Finalizada") || stateType.equalsIgnoreCase("Cancelada")) {
			alert("No se puede modificar una Orden de Produccion " + stateType + ".");
			return;
		}
		/*
		for (ProductionOrderDetail productionOrderDetail : productionOrderDetailList) {
			if (productionOrderDetail.getState() != ProcessState.Cancelado) {
				if (productionOrderDetail.getWorker() == null) {
					Clients.showNotification("Existen Procesos sin Trabajador Asignado", productionOrderDetailGrid);
					return;
				}
				MachineType machineType = productionOrderDetail.getProcess().getType().getMachineType();
				if (machineType != null) {
					if (productionOrderDetail.getMachine() == null) {
						Clients.showNotification("Existen Procesos sin Maquina Asignada", productionOrderDetailGrid);
						return;
					}
				}
				if (productionOrderDetail.getDateStart() == null) {
					Clients.showNotification("Existen Procesos sin Fecha Seleccionada", productionOrderDetailGrid);
					return;
				}
			}
		}
		 */
		if(currentProductionOrder.getNumber() == 0) {
			currentProductionOrder.setNumber(getNewProductionOrderNumber());
		}
		// la fecha inicio y fin de la orden se actualiza
		currentProductionOrder.setDateStart(currentProductionOrder.getStartDateFromDetails());
		currentProductionOrder.setDateFinish(currentProductionOrder.getFinishDateFromDetails());
		// el estado de la orden debe cambiar automaticamente 
		ProductionOrderStateType productionOrderStateType = null;
		if(currentProductionOrder.isReady()) {
			productionOrderStateType = productionOrderStateTypeRepository.findFirstByName("Preparada");
		} else {
			productionOrderStateType = productionOrderStateTypeRepository.findFirstByName("Registrada");
		}
		if(!productionOrderStateType.getName().equalsIgnoreCase(currentProductionOrder.getCurrentStateType().getName())) { // no se vuelve a grabar si es el mismo estado
			ProductionOrderState productionOrderState = new ProductionOrderState(productionOrderStateType, new Date());
			productionOrderState = productionOrderStateRepository.save(productionOrderState);
			currentProductionOrder.setState(productionOrderState);
		}
		currentProductionOrder = productionOrderRepository.save(currentProductionOrder);
		productionOrderDetailList = currentProductionOrder.getDetails();
		refreshView();
		alert("Orden de Produccion Guardada.");
	}

	// Estados Orden de Produccion: "Registrada" "Preparada" "Iniciada" "Finalizada" "Cancelada"
	// Estados Plan de Produccion: "Registrado""Abastecido""Lanzado""En Ejecucion""Finalizado""Cancelado""Suspendido"

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		Executions.getCurrent().setAttribute("selected_production_plan", currentProductionPlan);
		Include include = (Include) Selectors.iterable(productionOrderDetailGrid.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/production_order_list.zul");
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		currentProductionOrder = productionOrderRepository.findOne(currentProductionOrder.getId());// obtiene la misma orden sin cambios en los detalles
		productionOrderDetailList = currentProductionOrder.getDetails();
		refreshView();
	}

	public boolean isMachineNecessary(ProcessType processType) {
		if(processType.getMachineType() != null) {
			return true;
		}
		return false;
	}

	public ListModelList<Machine> getMachineListModel(ProductionOrderDetail productionOrderDetail) {
		if (productionOrderDetail.getProcess().getType().getMachineType() == null || isProductionOrderDetailNotValid(productionOrderDetail)) {
			return new ListModelList<>(new ArrayList<Machine>());
		}
		// devuelve las maquinas que no estan asignadas en el horario del proceso
		return new ListModelList<>(getAvailableMachineList(productionOrderDetail));
	}

	private List<Machine> getAvailableMachineList(ProductionOrderDetail productionOrderDetail) {
		// buscan solo las maquinas del mismo tipo
		List<Machine> list = getMachineListByProcessType(productionOrderDetail.getProcess().getType());;
		List<ProductionOrderDetail> overlappingDetails = getOverlappingDetails(productionOrderDetail);
		// se remueven los detalles que no requieren maquina
		List<ProductionOrderDetail> nullMachineTypeDetails = new ArrayList<ProductionOrderDetail>();
		for(ProductionOrderDetail each : overlappingDetails) {
			if(each.getProcess().getType().getMachineType() == null) {
				nullMachineTypeDetails.add(each);
			}
		}
		for(ProductionOrderDetail each : nullMachineTypeDetails) {
			overlappingDetails.remove(each);
		}
		for(Machine each : getUnavailableMachineSet(overlappingDetails, productionOrderDetail.getProcess().getType())) {
			list.remove(machineRepository.findOne(each.getId()));
		}
		return list;
	}

	private Set<Machine> getUnavailableMachineSet(List<ProductionOrderDetail> productionOrderDetailList, ProcessType processType) {
		Set<Machine> machineSet = new HashSet<Machine>();
		if(processType.getMachineType() != null) {
			for(ProductionOrderDetail each : productionOrderDetailList) {
				// se agregan solo las del mismo tipo
				if(each.getProcess().getType().getMachineType().equals(machineTypeRepository.findOne(processType.getMachineType().getId()))) {
					machineSet.add(each.getMachine());
				}
			}
		}
		return machineSet;
	}

	private boolean isProductionOrderDetailNotValid(ProductionOrderDetail productionOrderDetail) {
		// si esta cancelado o si tiene fechas en null no es valido
		return productionOrderDetail.getState() == ProcessState.Cancelado || productionOrderDetail.getDateStart() == null || productionOrderDetail.getDateFinish() == null;
	}

	public ListModelList<Worker> getWorkerListModel(ProductionOrderDetail productionOrderDetail) {
		// si esta cancelado o si tiene fechas en null, se devuelve una lista vacia
		if(isProductionOrderDetailNotValid(productionOrderDetail)) {
			return new ListModelList<>(new ArrayList<Worker>());
		}
		// devuelve los empleados disponibles en el horario del proceso
		return new ListModelList<>(getAvailableWorkerList(productionOrderDetail));
	}

	private List<Worker> getAvailableWorkerList(ProductionOrderDetail productionOrderDetail) {
		List<Worker> list = workerRepository.findAll();
		// se eliminan de la lista los empleados que esten asignado a otros procesos en los mismos intervalos de tiempo del proceso actual
		for(Worker each : getUnavailableWorkerSet(getOverlappingDetails(productionOrderDetail))) {
			list.remove(workerRepository.findOne(each.getId()));
		}
		return list;
	}

	private Set<Worker> getUnavailableWorkerSet(List<ProductionOrderDetail> productionOrderDetailList) {
		Set<Worker> workerSet = new HashSet<Worker>();
		for(ProductionOrderDetail each : productionOrderDetailList) {
			workerSet.add(each.getWorker());
		}
		return workerSet;
	}

	private List<ProductionOrderDetail> getOverlappingDetails(ProductionOrderDetail productionOrderDetail) {
		Date start = productionOrderDetail.getDateStart();
		Date finish = productionOrderDetail.getDateFinish();
		List<ProductionOrderDetail> overlappingDetails = productionOrderDetailRepository.findByDateFinishAfterAndDateStartBefore(start, finish);
		// se elimina de la lista el proceso actual
		overlappingDetails.remove(productionOrderDetailRepository.findOne(productionOrderDetail.getId()));
		// se crea una lista con los detalles de ordenes canceladas o finalizadas o sin worker para luego removerlos de la lista
		List<ProductionOrderDetail> invalidDetails = new ArrayList<ProductionOrderDetail>();
		for(ProductionOrderDetail each : overlappingDetails) {
			if(each.getState() == ProcessState.Cancelado || each.getState() == ProcessState.Realizado || each.getWorker() == null) {
				invalidDetails.add(each);
			}
		}
		// se remueven los detalles invalidos de la lista
		for(ProductionOrderDetail each : invalidDetails) {
			overlappingDetails.remove(each);
		}
		return overlappingDetails;
	}

	@Listen("onCreateWorkerCombobox = #productionOrderDetailGrid")
	public void doCreateWorkerCombobox(ForwardEvent evt) {// metodo utilizado para seleccionar el item del combobox luego de crearlo
		ProductionOrderDetail data = (ProductionOrderDetail) evt.getData();// obtenemos el objeto pasado por parametro
		Combobox element = (Combobox) evt.getOrigin().getTarget();// obtenemos el elemento web
		int value = -1;
		if(data.getWorker() != null) {
			for (int i = 0; i < element.getItems().size(); i++) {
				Comboitem item = element.getItems().get(i);
				if (item != null) {
					Worker worker = (Worker) item.getValue();
					worker = workerRepository.findOne(worker.getId());// actualiza en base a la BD para poder hacer la comparacion
					if (worker.equals(workerRepository.findOne(data.getWorker().getId()))) {
						value = i;
					}
				}
			}
		}
		element.setSelectedIndex(value);
	}

	@Listen("onCreateMachineCombobox = #productionOrderDetailGrid")
	public void doCreateMachineCombobox(ForwardEvent evt) {// metodo utilizado para seleccionar el item del combobox luego de crearlo
		ProductionOrderDetail data = (ProductionOrderDetail) evt.getData();// obtenemos el objeto pasado por parametro
		Combobox element = (Combobox) evt.getOrigin().getTarget();// obtenemos el elemento web
		int value = -1;
		if(data.getMachine() != null) {
			for (int i = 0; i < element.getItems().size(); i++) {
				Comboitem item = element.getItems().get(i);
				if (item != null) {
					Machine machine = (Machine) item.getValue();
					machine = machineRepository.findOne(machine.getId());// actualiza en base a la BD para poder hacer la comparacion
					if (machine.equals(machineRepository.findOne(data.getMachine().getId()))) {
						value = i;
					}
				}
			}
		}
		element.setSelectedIndex(value);
	}

	@Listen("onEditProductionOrderDetailWorker = #productionOrderDetailGrid")
	public void doEditProductionOrderDetailWorker(ForwardEvent evt) {
		ProductionOrderDetail data = (ProductionOrderDetail) evt.getData();// obtenemos el objeto pasado por parametro
		Combobox element = (Combobox) evt.getOrigin().getTarget();// obtenemos el elemento web
		Worker workerSelected = (Worker)element.getSelectedItem().getValue();
		data.setWorker(workerSelected);// cargamos al objeto el valor actualizado del elemento web
		refreshProductionOrderDetailGridView();
	}

	@Listen("onEditProductionOrderDetailMachine = #productionOrderDetailGrid")
	public void doEditProductionOrderDetailMachine(ForwardEvent evt) {
		ProductionOrderDetail data = (ProductionOrderDetail) evt.getData();// obtenemos el objeto pasado por parametro
		Combobox element = (Combobox) evt.getOrigin().getTarget();// obtenemos el elemento web
		Machine machineSelected = (Machine)element.getSelectedItem().getValue();
		data.setMachine(machineSelected);// cargamos al objeto el valor actualizado del elemento web
		// asigna la misma maquina a todos los detalles que necesitan ese tipo de maquina
		for(ProductionOrderDetail each : productionOrderDetailList) {
			if(!data.equals(each)) {// no modifica el mismo detalle
				if(each.getProcess().getType().getMachineType() != null) {// comprueba si se necesita una maquina para el detalle
					// si el detalle ya posee una maquina asignada, se la deja igual
					if(each.getMachine() == null) {
						MachineType machineTypeSelected = machineTypeRepository.findOne(machineSelected.getMachineType().getId());
						MachineType machineTypeEach = machineTypeRepository.findOne(each.getProcess().getType().getMachineType().getId());
						if(machineTypeEach.equals(machineTypeSelected)) {
							each.setMachine(machineSelected);
						}
					}
				}
			}
		}
		refreshProductionOrderDetailGridView();
	}

	private List<Machine> getMachineListByProcessType(ProcessType processType) {
		List<Machine> list = new ArrayList<Machine>();
		if(processType.getMachineType() != null) {
			MachineType machineType = machineTypeRepository.findOne(processType.getMachineType().getId());
			if (machineType != null) {
				for (Machine machine : machineList) {
					if (machineType.equals(machineTypeRepository.findOne(machine.getMachineType().getId()))) {
						list.add(machine);
					}
				}
			}
		}
		return list;
	}

	/*
	private void sortProcessTypeListBySequence() {
		Comparator<ProcessType> comp = new Comparator<ProcessType>() {
			@Override
			public int compare(ProcessType a, ProcessType b) {
				return a.getSequence().compareTo(b.getSequence());
			}
		};
		Collections.sort(processTypeList, comp);
	}
	 */

	@Listen("onClick = #autoAssignButton")
	public void autoAssignButtonClick() {
		// si no estan asignadas las fechas de los procesos se informa
		if(productionOrderStartDatebox.getValue()==null || productionOrderFinishDatebox.getValue()==null) {
			alert("Debe seleccionar las fechas de los procesos antes de asignar recursos.");
			return;
		}
		// asigna la primer maquina o trabajador disponible a cada detalle
		Worker worker = null;
		Machine machine = null;
		for(ProductionOrderDetail each : productionOrderDetailList) {
			if(each.getState() == ProcessState.Cancelado) {
				each.setWorker(null);
				each.setMachine(null);
			} else {// si el detalle no es cancelado
				List<Worker> availableWorkerList = getAvailableWorkerList(each);
				List<Machine> availableMachineList = getAvailableMachineList(each);
				//getMachineListByProcessType(each.getProcess().getType());
				if(availableMachineList.size() > 0) {
					machine = availableMachineList.get(0);
				}
				if(availableWorkerList.size() > 0) {
					worker = availableWorkerList.get(0);
				}
				each.setWorker(worker);
				each.setMachine(machine);
			}
		}
		refreshProductionOrderDetailGridView();
	}

	@Listen("onChange = #productionOrderStartDatebox")
	public void doChangeProductionOrderStartDatebox() {
		refreshProcessDates();
	}

	private void refreshProcessDates() {
		Date startDate = getDateTimeStartWork(productionOrderStartDatebox.getValue());
		// asigna el valor a la clase y hace que calcule todos los tiempos de los detalles
		currentProductionOrder.setDateStart(startDate);
		currentProductionOrder.updateDetailDates(startDate);
		productionOrderDetailList = currentProductionOrder.getDetails();
		sortProductionOrderDetailListByProcessTypeSequence();
		productionOrderStartDatebox.setValue(currentProductionOrder.getDateStart());// modifica el valor del datebox para que contenga la hora de inicio
		productionOrderFinishDatebox.setValue(currentProductionOrder.getDateFinish());
		refreshProductionOrderDetailGridView();
	}

	private Date getDateTimeStartWork(Date startDate) {
		// devuelve la union entre la fecha del datebox y la hora de inicio del dia
		if(startDate != null) {
			Calendar calStartDate = Calendar.getInstance();
			calStartDate.setTime(startDate);
			calStartDate.set(Calendar.HOUR_OF_DAY, ProductionDateTimeHelper.getFirstHourOfDay());
			calStartDate.set(Calendar.MINUTE, ProductionDateTimeHelper.getFirstMinuteOfDay());
			return calStartDate.getTime();
		}
		return null;
	}

	private void sortProductionOrderDetailListByProcessTypeSequence() {
		Comparator<ProductionOrderDetail> comp = new Comparator<ProductionOrderDetail>() {
			@Override
			public int compare(ProductionOrderDetail a, ProductionOrderDetail b) {
				return a.getProcess().getType().getSequence().compareTo(b.getProcess().getType().getSequence());
			}
		};
		Collections.sort(productionOrderDetailList, comp);
	}

	public boolean isStateCancel(ProcessState processState) {
		return processState == ProcessState.Cancelado;
	}

	@Listen("onEditProductionOrderDetailIsCanceled = #productionOrderDetailGrid")
	public void doEditProductionOrderDetailIsCanceled(ForwardEvent evt) {
		ProductionOrderDetail data = (ProductionOrderDetail) evt.getData();// obtenemos el objeto pasado por parametro
		Checkbox element = (Checkbox) evt.getOrigin().getTarget();// obtenemos el elemento web
		if(element.isChecked()) {
			data.setState(ProcessState.Cancelado);
		} else {
			data.setState(ProcessState.Pendiente);
		}
		refreshProcessDates();
		autoAssignButtonClick();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onChangeProductionOrderDetailStartDate = #productionOrderDetailGrid")
	public void doChangeProductionOrderDetailStartDate(ForwardEvent evt) {
		final ProductionOrderDetail data = (ProductionOrderDetail) evt.getData();// obtenemos el objeto pasado por parametro
		final Datebox element = (Datebox) evt.getOrigin().getTarget();// obtenemos el elemento web
		// se verifica que la hora este dentro del horario de trabajo
		if(ProductionDateTimeHelper.isOutsideWorkingHours(element.getValue())) {
			alert("Error en la Hora. Debe seleccionar un horario entre las " + ProductionDateTimeHelper.getFormattedFirst() + " hs y las " + ProductionDateTimeHelper.getFormattedLast() + " hs");
			// se regresa al valor original
			element.setValue(data.getDateStart());
			return;
		}
		// se envia el mensaje: se modificaran tiempos de los procesos posteriores para que no se superpongan
		Messagebox.show("Los tiempos posteriores se modificaran, y los recursos asignados se removeran, desea continuar?", "Confirmar Modificacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
			public void onEvent(Event evt) throws InterruptedException {
				if (evt.getName().equals("onOK")) {
					changeProductionOrderDetailDates(data, element);
				} else {
					// si se cancela se regresa al valor original
					element.setValue(data.getDateStart());
				}
			}
		});
	}

	private void changeProductionOrderDetailDates(ProductionOrderDetail productionOrderDetail, Datebox datebox) {
		changeDetailStartDate(productionOrderDetail, datebox);
		// recalcula todos los tiempos de los procesos posteriores para que inicien en el final
		changeRemainDetailsDate(productionOrderDetail);
		// se hace refresh de la lista para q aparezcan los cambios
		refreshProductionOrderDetailGridView();
		// se actualizan los datebox de inicio y fin
		productionOrderStartDatebox.setValue(productionOrderDetail.getProductionOrder().getStartDateFromDetails());
		productionOrderFinishDatebox.setValue(productionOrderDetail.getProductionOrder().getFinishDateFromDetails());
	}

	private void changeDetailStartDate(ProductionOrderDetail detail, Datebox dateboxStart) {
		detail.setDateStart(dateboxStart.getValue());
		// cambia la fecha de fin tambien
		Date finish = ProductionDateTimeHelper.getFinishDate(dateboxStart.getValue(), detail.getTimeTotal());
		detail.setDateFinish(finish);
		// se remueven las maquinas y empleados asignados, ya que puede ser que esten no disponibles para el nuevo horario
		detail.setWorker(null);
		detail.setMachine(null);
		// cambia el valor del elemento con fecha fin
		//dateboxFinishSetValue(dateboxStart, finish); en lugar de cambiar el elemento se hace refresh de la lista
	}

	private void changeRemainDetailsDate(ProductionOrderDetail detail) {
		ProductionOrder productionOrder = detail.getProductionOrder();
		productionOrder.updateRemainDetailDates(detail);
	}
	
	@Listen("onClick = #createReportButton")
	public void reportButtonClick() {
		if(reportTypeListbox.getSelectedItem() != null) {
			ReportType reportType = (ReportType)(reportTypeListbox.getSelectedItem().getValue());
			String selectedReportType = reportType.getValue();
			loadJasperreport(selectedReportType);
		} else {
			Clients.showNotification("Tipo de reporte no seleccionado");
		}
	}

	private void loadJasperreport(String type) {
		reportBlockJasperreport.setSrc("/jasperreport/production_order.jasper");
		reportBlockJasperreport.setType(type);
		reportBlockJasperreport.setDatasource(new ProductionOrderReportDataSource(productionOrderDetailList));
	}
}

class ProductionOrderReportDataSource implements JRDataSource {

	private List<ProductionOrderDetail> productionOrderDetailList = new ArrayList<ProductionOrderDetail>();
	private int index = -1;

	public ProductionOrderReportDataSource(List<ProductionOrderDetail> productionOrderDetailList) {
		// agrega todos menos los detalles cancelados
		for(ProductionOrderDetail each : productionOrderDetailList) {
			if(each.getState() != ProcessState.Cancelado) {
				this.productionOrderDetailList.add(each);
			}
		}
	}

	public boolean next() throws JRException {
		index++;
		return (index < productionOrderDetailList.size());
	}

	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		if ("process_name".equals(fieldName)) {
			value = productionOrderDetailList.get(index).getProcess().getType().getName();
		} else if ("machine_name".equals(fieldName)) {
			if(productionOrderDetailList.get(index).getProcess().getType().getMachineType() != null) {
				if(productionOrderDetailList.get(index).getMachine() != null) {
					value = productionOrderDetailList.get(index).getMachine().getName();
				} else {
					value = "No seleccionado";
				}
			} else {
				value = "No requiere";
			}
		} else if ("worker_name".equals(fieldName)) {
			if(productionOrderDetailList.get(index).getWorker() != null) {
				value = productionOrderDetailList.get(index).getWorker().getName();
			} else {
				value = "No seleccionado";
			}
		} else if ("date_start".equals(fieldName)) {
			Date date = productionOrderDetailList.get(index).getDateStart();
			if(date != null) {
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				value = dateFormat.format(date);
			} else {
				value = "No seleccionado";
			}
		} else if ("date_finish".equals(fieldName)) {
			Date date = productionOrderDetailList.get(index).getDateFinish();
			if(date != null) {
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				value = dateFormat.format(date);
			} else {
				value = "No seleccionado";
			}
		} else if ("duration_total".equals(fieldName)) {
			Duration time = productionOrderDetailList.get(index).getTimeTotal();
			if(time != null) {
				int hours = time.getHours();
				int minutes = time.getMinutes();
				while(minutes >= 60) {
					hours = hours + 1;
					minutes = minutes - 60;
				}
				value = String.format("%d hrs  %d min", hours, minutes);
			} else {
				value = "0 hrs 0 min";
			}
		} else if ("piece_name".equals(fieldName)) {
			value = productionOrderDetailList.get(index).getProcess().getPiece().getName();
		} else if ("piece_quantity".equals(fieldName)) {
			value = productionOrderDetailList.get(index).getQuantityPiece();
		}
		return value;
	}
}
