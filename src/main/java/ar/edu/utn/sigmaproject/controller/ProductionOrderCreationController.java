package ar.edu.utn.sigmaproject.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.zkoss.image.AImage;
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
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Machine;
import ar.edu.utn.sigmaproject.domain.MachineType;
import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.ProcessState;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.domain.ProductionOrderMaterial;
import ar.edu.utn.sigmaproject.domain.ProductionOrderState;
import ar.edu.utn.sigmaproject.domain.ProductionOrderStateType;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.domain.Worker;
import ar.edu.utn.sigmaproject.service.MachineRepository;
import ar.edu.utn.sigmaproject.service.MachineTypeRepository;
import ar.edu.utn.sigmaproject.service.OrderRepository;
import ar.edu.utn.sigmaproject.service.OrderStateRepository;
import ar.edu.utn.sigmaproject.service.OrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.PieceRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeRepository;
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
	Grid processTypeGrid;
	@Wire
	Button autoAssignButton;
	@Wire
	Image productImage;

	// services
	@WireVariable
	private ProductionOrderRepository productionOrderRepository;
	//	@WireVariable
	//	private ProductionOrderDetailRepository productionOrderDetailRepository;
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
	@WireVariable
	private PieceRepository pieceRepository;
	@WireVariable
	private ProductionPlanStateTypeRepository productionPlanStateTypeRepository;
	@WireVariable
	private ProductionPlanStateRepository productionPlanStateRepository;
	@WireVariable
	private ProductionPlanRepository productionPlanRepository;
	@WireVariable
	private OrderStateTypeRepository orderStateTypeRepository;
	@WireVariable
	private OrderStateRepository orderStateRepository;
	@WireVariable
	private OrderRepository orderRepository;

	// atributes
	private ProductionOrder currentProductionOrder;
	private ProductionPlan currentProductionPlan;

	// list
	private List<ProductionOrderDetail> productionOrderDetailList;
	private List<Worker> workerList;
	private List<Machine> machineList;
	private List<ProcessType> processTypeList;

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
		} catch (IOException exception) {

		}
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
		productionPlanNameTextbox.setDisabled(true);
		productNameTextbox.setDisabled(true);
		productCodeTextbox.setDisabled(true);
		//		productionPlanCreationDatebox.setDisabled(true);
		productUnitsIntbox.setDisabled(true);
		productionPlanStateTypeTextbox.setDisabled(true);
		productionPlanNameTextbox.setText(currentProductionPlan.getName());
		//		productionPlanCreationDatebox.setValue(currentProductionPlan.getDateCreation());
		ProductionPlanStateType lastProductionPlanStateType = currentProductionPlan.getCurrentStateType();
		if(lastProductionPlanStateType != null) {
			productionPlanStateTypeTextbox.setText(lastProductionPlanStateType.getName());
		} else {
			productionPlanStateTypeTextbox.setText("[Sin Estado]");
		}
		productNameTextbox.setText(currentProductionOrder.getProduct().getName());
		productCodeTextbox.setText(currentProductionOrder.getProduct().getCode());
		productUnitsIntbox.setValue(currentProductionOrder.getUnits());
		workerList = workerRepository.findAll();
		productionOrderStartDatebox.setValue(currentProductionOrder.getDateStart());
		productionOrderFinishDatebox.setValue(currentProductionOrder.getDateFinish());
		refreshProductionOrderDetailGridView();
		refreshProductionOrderOrderSupplyAndRawMaterialListbox();
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		resetButton.setDisabled(false);

		refreshProcessTypeGridView();
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
		if(currentProductionOrder.getCurrentStateType().getName().equalsIgnoreCase("Cancelada")) {
			alert("No se puede modificar una Orden de Produccion Cancelada.");
			return;
		}
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
			}
		}
		Date productionOrderDateStart = productionOrderStartDatebox.getValue();
		if (productionOrderDateStart == null) {
			Clients.showNotification("Debe Seleccionar Fecha de Inicio", productionOrderStartDatebox);
			return;
		}
		if(currentProductionOrder.getNumber() == 0) {
			currentProductionOrder.setNumber(getNewProductionOrderNumber());
		}
		// la fecha inicio y fin se calcularon y agregaron a currentProductionOrder al modificar la fecha inicio
		// el estado de la orden debe cambiar automaticamente 
		ProductionOrderStateType productionOrderStateType = productionOrderStateTypeRepository.findFirstByName("Preparada");
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

	//"Registrado""Abastecido""Lanzado""En Ejecucion""Finalizado""Cancelado""Suspendido"

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

	public String getPieceNameByProcess(Process process) {
		return pieceRepository.findByProcesses(process).getName();
	}

	public String getMachineTypeNameByProcess(Process process) {
		if(process.getType().getMachineType() != null) {
			return process.getType().getMachineType().getName();
		}
		return "NINGUNA";
	}

	public String getMachineTypeNameByProcessType(ProcessType processType) {
		if(processType.getMachineType() != null) {
			return processType.getMachineType().getName();
		}
		return "NINGUNA";
	}

	public boolean isMachineNecessary(ProcessType processType) {
		if(processType.getMachineType() != null) {
			return true;
		}
		return false;
	}

	public ListModelList<Machine> getMachineListModel(ProductionOrderDetail productionOrderDetail) {
		List<Machine> list = new ArrayList<Machine>();
		MachineType machineType = machineTypeRepository.findOne(productionOrderDetail.getProcess().getType().getMachineType().getId());
		if (machineType != null) {
			for (Machine machine : machineList) {
				if (machineType.equals(machineTypeRepository.findOne(machine.getMachineType().getId()))) {
					list.add(machine);
				}
			}
		}
		return new ListModelList<>(list);
	}

	public ListModelList<Worker> getWorkerListModel(ProductionOrderDetail productionOrderDetail) {
		// TODO: mostrar solo los empleados disponibles en los horarios del proceso
		return new ListModelList<>(workerRepository.findAll());
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
		refreshProcessTypeGridView();
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
		refreshProcessTypeGridView();
	}

	public ListModelList<Machine> getMachineListModelByProcessType(ProcessType processType) {
		return new ListModelList<>(getMachineListByProcessType(processType));
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

	public ListModelList<Worker> getProcessTypeWorkerListModel(ProcessType processType) {
		// TODO: debe buscar los empleados basandose en la disponibilidad de horarios, por lo que las fechas ya deberian estar seleccionadas
		return new ListModelList<>(workerList);
	}

	@Listen("onEditProcessTypeWorker = #processTypeGrid")
	public void doEditProcessTypeWorker(ForwardEvent evt) {
		//selecciona el trabajador para todos los procesos que involucren ese tipo de proceso
		ProcessType data = (ProcessType) evt.getData();// obtenemos el objeto pasado por parametro
		Combobox element = (Combobox) evt.getOrigin().getTarget();// obtenemos el elemento web
		Worker workerSelected = (Worker)element.getSelectedItem().getValue();
		// asigna el trabajador a todos los detalles que necesitan ese tipo de proceso
		for(ProductionOrderDetail each : productionOrderDetailList) {
			if(each.getProcess().getType().equals(data)) {// comprueba si es el mismo tipo de proceso
				each.setWorker(workerSelected);
			}
		}
		refreshProcessTypeGridView();
		// debe actualizar tambien la ProductionOrderDetailGridView para que los cambios sea aplicados en el otro grid
		refreshProductionOrderDetailGridView();
	}

	@Listen("onCreateProcessTypeWorkerCombobox = #processTypeGrid")
	public void doCreateProcessTypeWorkerCombobox(ForwardEvent evt) {// metodo utilizado para seleccionar el item del combobox luego de crearlo
		//TODO: debe dejar seleccionado los empleados que esten seleccionados en esos procesos en los detalles de orden
		// y en caso de que en un mismo tipo de proceso esten seleccionados mas de 1 empleado, mostrar la opcion mixto o custom
		ProcessType data = (ProcessType) evt.getData();// obtenemos el objeto pasado por parametro
		Combobox element = (Combobox) evt.getOrigin().getTarget();// obtenemos el elemento web
		Worker worker = getProcessTypeWorker(data);
		int value = -1;
		if(worker != null) {
			List<Comboitem> comboitemList = element.getItems();
			for (int i = 0; i < comboitemList.size(); i++) {
				Comboitem item = comboitemList.get(i);
				if (item != null) {
					Worker itemWorker = (Worker) item.getValue();
					itemWorker = workerRepository.findOne(itemWorker.getId());// actualiza en base a la BD para poder hacer la comparacion
					if (itemWorker.equals(workerRepository.findOne(worker.getId()))) {
						value = i;
					}
				}
			}
		}
		element.setSelectedIndex(value);
	}

	private Worker getProcessTypeWorker(ProcessType processType) {
		// verifica si todos los detalles que tengan ese tipo de proceso estan asignados a algun trabajador
		// en ese caso se devuelve el trabajador, caso contrario (no estan asignados todos, o estan asignados mas de 1 trabajador) se devuelve null
		Worker prevWorker = null;
		for(ProductionOrderDetail each : productionOrderDetailList) {
			if(each.getProcess().getType() == processType) {
				if(each.getWorker() != null) {
					if(prevWorker == null) {//la primera vez carga el trabajador y no hace comparacion
						prevWorker = each.getWorker();
					} else {
						if(!each.getWorker().equals(prevWorker)) {
							return null;
						}
					}
				} else {
					return null;
				}
			}
		}
		return prevWorker;
	}

	@Listen("onEditProcessTypeMachine = #processTypeGrid")
	public void doEditProcessTypeMachine(ForwardEvent evt) {
		ProcessType data = (ProcessType) evt.getData();// obtenemos el objeto pasado por parametro
		Combobox element = (Combobox) evt.getOrigin().getTarget();// obtenemos el elemento web
		Machine machineSelected = (Machine)element.getSelectedItem().getValue();
		// asigna la misma maquina a todos los detalles que sean del tipo de proceso
		for(ProductionOrderDetail each : productionOrderDetailList) {
			if(each.getProcess().getType().equals(data)) {
				each.setMachine(machineSelected);
			}
		}
		refreshProcessTypeGridView();
		// debe actualizar tambien la ProductionOrderDetailGridView para que los cambios sea aplicados en el otro grid
		refreshProductionOrderDetailGridView();
	}

	@Listen("onCreateProcessTypeMachineCombobox = #processTypeGrid")
	public void doCreateProcessTypeMachineCombobox(ForwardEvent evt) {// metodo utilizado para seleccionar el item del combobox luego de crearlo
		ProcessType data = (ProcessType) evt.getData();
		Combobox element = (Combobox) evt.getOrigin().getTarget();// obtenemos el elemento web
		int value = -1;
		Machine machine = getProcessTypeMachine(data);
		if(machine != null) {
			List<Comboitem> comboitemList = element.getItems();
			for (int i = 0; i < comboitemList.size(); i++) {
				Comboitem item = comboitemList.get(i);
				if (item != null) {
					Machine itemMachine = (Machine) item.getValue();
					itemMachine = machineRepository.findOne(itemMachine.getId());// actualiza en base a la BD para poder hacer la comparacion
					if (itemMachine.equals(machineRepository.findOne(machine.getId()))) {
						value = i;
					}
				}
			}
		}
		element.setSelectedIndex(value);
	}

	private Machine getProcessTypeMachine(ProcessType processType) {
		// verifica si todos los detalles que tengan ese tipo de proceso estan asignados a alguna maquina
		// en ese caso se devuelve la maquina, caso contrario (no estan asignados todos, o estan asignados mas de 1 trabajador) se devuelve null
		Machine prevMachine = null;
		for(ProductionOrderDetail each : productionOrderDetailList) {
			if(each.getProcess().getType() == processType) {
				if(each.getMachine() != null) {
					if(prevMachine == null) {//la primera vez carga la maquina y no hace comparacion
						prevMachine = each.getMachine();
					} else {
						if(!each.getMachine().equals(prevMachine)) {// si no son todas iguales
							return null;
						}
					}
				} else {
					return null;
				}
			}
		}
		return prevMachine;
	}

	private void refreshProcessTypeGridView() {
		// la lista de procesos se crea en base a los tipos de procesos que incluye la orden
		processTypeList = currentProductionOrder.getProcessTypeList();
		sortProcessTypeListBySequence();
		ListModelList<ProcessType> processTypeListModelList = new ListModelList<ProcessType>(processTypeList);
		processTypeGrid.setModel(processTypeListModelList);
	}

	private void sortProcessTypeListBySequence() {
		Comparator<ProcessType> comp = new Comparator<ProcessType>() {
			@Override
			public int compare(ProcessType a, ProcessType b) {
				return a.getSequence().compareTo(b.getSequence());
			}
		};
		Collections.sort(processTypeList, comp);
	}

	@Listen("onClick = #autoAssignButton")
	public void autoAssignButtonClick() {
		// asigna la primer maquina o trabajador disponible a cada detalle
		// solo si el detalle no es cancelado
		Worker worker = null;
		Machine machine = null;
		if(workerList.size() > 0) {
			worker = workerList.get(0);
		}
		for(ProductionOrderDetail each : productionOrderDetailList) {
			if(each.getState() == ProcessState.Cancelado) {
				each.setWorker(null);
				each.setMachine(null);
			} else {
				List<Machine> list = getMachineListByProcessType(each.getProcess().getType());
				if(list.size() > 0) {
					machine = list.get(0);
				}
				each.setWorker(worker);
				each.setMachine(machine);
			}
		}
		refreshProductionOrderDetailGridView();
		refreshProcessTypeGridView();
	}

	@Listen("onChange = #productionOrderStartDatebox")
	public void doChangeProductionOrderStartDatebox() {
		refreshProcessDates();
	}

	private void refreshProcessDates() {
		Date startDate = getDateTimeStartWork(productionOrderStartDatebox.getValue());
		// asigna el valor a la clase para que calcule todos los tiempos de los detalles automaticamente
		currentProductionOrder.setDateStart(startDate);
		productionOrderDetailList = currentProductionOrder.getDetails();
		sortProductionOrderDetailListByProcessTypeSequence();
		productionOrderStartDatebox.setValue(currentProductionOrder.getDateStart());// modifica el valor del datebox para que contenga la hora de inicio
		productionOrderFinishDatebox.setValue(currentProductionOrder.getDateFinish());
		refreshProductionOrderDetailGridView();
		refreshProcessTypeGridView();
	}

	private Date getDateTimeStartWork(Date startDate) {
		// devuelve la union entre la fecha del datebox y la hora de inicio de trabajo
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

	public List<Piece> getProcessTypePieceList(ProcessType processType) {
		// devuelve la lista de piezas del processType
		List<Piece> pieceList = new ArrayList<Piece>();
		for(ProductionOrderDetail each : productionOrderDetailList) {
			if(processType.equals(each.getProcess().getType())) {
				pieceList.add(pieceRepository.findByProcesses(each.getProcess()));
			}
		}
		return pieceList;
	}

	@Listen("onChangeProductionOrderDetailStartDate = #productionOrderDetailGrid")
	public void doChangeProductionOrderDetailStartDate(ForwardEvent evt) {
		ProductionOrderDetail data = (ProductionOrderDetail) evt.getData();// obtenemos el objeto pasado por parametro
		Datebox element = (Datebox) evt.getOrigin().getTarget();// obtenemos el elemento web
		data.setDateStart(element.getValue());
		// cambia la fecha de fin tambien
		Date finish = ProductionDateTimeHelper.getFinishDate(element.getValue(), data.getTimeTotal());
		data.setDateFinish(finish);
		// cambia el valor del elemento con fecha fin
		Row fila = (Row)element.getParent();
		Datebox dateboxFinish = (Datebox) fila.getChildren().get(fila.getChildren().size()-7);
		dateboxFinish.setValue(finish);
		//TODO: recalcular todos los tiempos de los procesos posteriores para que inicien el final
	}
}
