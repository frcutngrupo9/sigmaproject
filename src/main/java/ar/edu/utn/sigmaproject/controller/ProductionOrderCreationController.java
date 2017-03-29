package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.Duration;

import org.springframework.transaction.annotation.Transactional;
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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Machine;
import ar.edu.utn.sigmaproject.domain.MachineType;
import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.domain.ProductionOrderRawMaterial;
import ar.edu.utn.sigmaproject.domain.ProductionOrderState;
import ar.edu.utn.sigmaproject.domain.ProductionOrderStateType;
import ar.edu.utn.sigmaproject.domain.ProductionOrderSupply;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.domain.RawMaterialRequirement;
import ar.edu.utn.sigmaproject.domain.SupplyRequirement;
import ar.edu.utn.sigmaproject.domain.SupplyReserved;
import ar.edu.utn.sigmaproject.domain.WoodReserved;
import ar.edu.utn.sigmaproject.domain.Worker;
import ar.edu.utn.sigmaproject.service.MachineRepository;
import ar.edu.utn.sigmaproject.service.MachineTypeRepository;
import ar.edu.utn.sigmaproject.service.OrderRepository;
import ar.edu.utn.sigmaproject.service.OrderStateRepository;
import ar.edu.utn.sigmaproject.service.OrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.PieceRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderDetailRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderRawMaterialRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderSupplyRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeRepository;
import ar.edu.utn.sigmaproject.service.SupplyReservedRepository;
import ar.edu.utn.sigmaproject.service.WoodReservedRepository;
import ar.edu.utn.sigmaproject.service.WorkerRepository;

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
	//	@Wire
	//	Datebox productionPlanCreationDatebox;
	@Wire
	Grid productionOrderDetailGrid;
	//	@Wire
	//	Spinner productionOrderNumberSpinner;
	@Wire
	Intbox productUnitsIntbox;
	//	@Wire
	//	Combobox workerCombobox;
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
	//	@Wire
	//	Combobox productionOrderStateTypeCombobox;
	@Wire
	Button generateDetailsButton;
	@Wire
	Listbox productionOrderSupplyListbox;
	@Wire
	Listbox productionOrderRawMaterialListbox;
	//	@Wire
	//	Listbox candidateWorkerListbox;
	//	@Wire
	//	Listbox chosenWorkerListbox;
	//	@Wire
	//	Button chooseWorkerButton;
	//	@Wire
	//	Button removeWorkerButton;
	@Wire
	Grid processTypeGrid;

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
	@WireVariable
	private PieceRepository pieceRepository;
	@WireVariable
	private WoodReservedRepository woodReservedRepository;
	@WireVariable
	private SupplyReservedRepository supplyReservedRepository;
	@WireVariable
	private ProductionOrderSupplyRepository productionOrderSupplyRepository;
	@WireVariable
	private ProductionOrderRawMaterialRepository productionOrderRawMaterialRepository;
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
	private List<ProductionOrderSupply> productionOrderSupplyList;
	private List<ProductionOrderRawMaterial> productionOrderRawMaterialList;
	private List<ProcessType> processTypeList;

	// list models
	private ListModelList<ProductionOrderDetail> productionOrderDetailListModel;
	private ListModelList<Worker> workerListModel;
	//	private ListModelList<ProductionOrderStateType> productionOrderStateTypeListModel;
	private ListModelList<ProductionOrderSupply> productionOrderSupplyListModel;
	private ListModelList<ProductionOrderRawMaterial> productionOrderRawMaterialListModel;
	//	private ListModelList<Worker> candidateWorkerListModel;
	//	private ListModelList<Worker> chosenWorkerListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentProductionOrder = (ProductionOrder) Executions.getCurrent().getAttribute("selected_production_order");
		if(currentProductionOrder == null) {throw new RuntimeException("ProductionOrder not found");}
		currentProductionPlan = (ProductionPlan) Executions.getCurrent().getAttribute("selected_production_plan");
		if(currentProductionPlan == null) {throw new RuntimeException("ProductionPlan not found");}

		productionOrderDetailList = currentProductionOrder.getDetails();
		//List<ProductionOrderDetail> details = getProductionOrderDetailList(currentProductionOrder);// genera los detalles para para ver si no se edito el producto y que posea una cantidad mas grande de procesos.
		//if(details.size() != productionOrderDetailList.size()) {
		//	productionOrderDetailList = details;
		//}

		machineList = machineRepository.findAll();

		// la lista de procesos se crea en base a los tipos de procesos que incluye la orden
		processTypeList = getProcessTypeTotalList();

		refreshView();
	}

	private void refreshView() {
		productionOrderStartDatebox.setDisabled(true);
		productionOrderFinishDatebox.setDisabled(true);
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
		//		if(currentProductionOrder.getNumber()!=null && currentProductionOrder.getNumber()!=0) {
		//			productionOrderNumberSpinner.setValue(currentProductionOrder.getNumber());
		//		} else {
		//			productionOrderNumberSpinner.setValue(getNewProductionOrderNumber());
		//		}
		workerList = workerRepository.findAll();
		workerListModel = new ListModelList<>(workerList);
		if (currentProductionOrder.getWorker() != null) {
			workerListModel.addToSelection(workerRepository.findOne(currentProductionOrder.getWorker().getId()));
		}
		//		fillCandidateWorkerListbox();
		//		workerCombobox.setModel(workerListModel);
		productionOrderStartDatebox.setValue(currentProductionOrder.getDateStart());
		productionOrderFinishDatebox.setValue(currentProductionOrder.getDateFinish());
		refreshProductionOrderDetailGridView();
		refreshProductionOrderOrderSupplyAndRawMaterialListbox();
		//		List<ProductionOrderStateType> productionOrderStateTypeList = productionOrderStateTypeRepository.findAll();
		//		productionOrderStateTypeListModel = new ListModelList<ProductionOrderStateType>(productionOrderStateTypeList);
		//		productionOrderStateTypeListModel.addToSelection(productionOrderStateTypeRepository.findOne(currentProductionOrder.getCurrentStateType().getId()));
		//		productionOrderStateTypeCombobox.setModel(productionOrderStateTypeListModel);
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		resetButton.setDisabled(false);
		refreshProcessTypeGridView();
	}

	//	private void fillCandidateWorkerListbox() {
	//		//TODO debe cargar solo los empleados que esten disponibles en las fechas indicadas por la orden de produccion
	//		candidateWorkerListModel = new ListModelList<>(workerList);
	//		candidateWorkerListbox.setModel(candidateWorkerListModel);
	//		chosenWorkerListModel = new ListModelList<>(new ArrayList<Worker>());
	//		chosenWorkerListbox.setModel(chosenWorkerListModel);
	//	}

	private void refreshProductionOrderDetailGridView() {
		productionOrderDetailListModel = new ListModelList<ProductionOrderDetail>(productionOrderDetailList);
		productionOrderDetailGrid.setModel(productionOrderDetailListModel);
	}

	private void refreshProductionOrderOrderSupplyAndRawMaterialListbox() {
		productionOrderSupplyList = currentProductionOrder.getProductionOrderSupplies();
		productionOrderRawMaterialList = currentProductionOrder.getProductionOrderRawMaterials();
		productionOrderSupplyListModel = new ListModelList<ProductionOrderSupply>(productionOrderSupplyList);
		productionOrderRawMaterialListModel = new ListModelList<ProductionOrderRawMaterial>(productionOrderRawMaterialList);
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

	@Listen("onClick = #generateDetailsButton")
	public void generateDetailListClick() {
		List<ProductionOrderDetail> details = getProductionOrderDetailList(currentProductionOrder);
		if(details.size() != productionOrderDetailList.size()) {
			productionOrderDetailList = details;
			refreshView();
		}
	}

	private List<ProductionOrderDetail> getProductionOrderDetailList(ProductionOrder productionOrder) {
		List<ProductionOrderDetail> productionOrderDetailList = new ArrayList<>();
		for(Piece piece : productionOrder.getProduct().getPieces()) {
			List<Process> auxProcessList = piece.getProcesses();
			for(Process process : auxProcessList) {
				// por cada proceso hay que crear un detalle
				//TODO verificar si el tiempo de proceso es por todas las piezas iguales de un producto o individual
				Integer quantityPiece = productionOrder.getUnits() * piece.getUnits();// cantidad total de la pieza
				Duration timeTotal = process.getTime().multiply(productionOrder.getUnits());// cantidad total de tiempo del proceso
				productionOrderDetailList.add(new ProductionOrderDetail(process, null, timeTotal, quantityPiece));
			}
		}
		return productionOrderDetailList;
	}
	
	private boolean isEditionAllowed() {
		// no se puede modificar si el plan esta Cancelado, Lanzado, En Ejecucion o Finalizado. Si esta Suspendido se puede modificar para solucionar problemas de maquinas en reparacion o empreados ausentes.
		ProductionPlanStateType stateLanzado = productionPlanStateTypeRepository.findFirstByName("Lanzado");
		ProductionPlanStateType stateEnEjecucion = productionPlanStateTypeRepository.findFirstByName("En Ejecucion");
		ProductionPlanStateType stateFinalizado = productionPlanStateTypeRepository.findFirstByName("Finalizado");
		ProductionPlanStateType stateCancelado = productionPlanStateTypeRepository.findFirstByName("Cancelado");
		ProductionPlanStateType currentStateType = currentProductionPlan.getCurrentStateType();
		currentStateType = productionPlanStateTypeRepository.findOne(currentStateType.getId());
		if(currentStateType.equals(stateLanzado) || currentStateType.equals(stateEnEjecucion) || currentStateType.equals(stateFinalizado) || currentStateType.equals(stateCancelado)) {
			return false;
		}
		return true;
	}

	@Transactional
	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		ProductionOrderStateType lastStateType = productionOrderStateTypeRepository.findOne(currentProductionOrder.getCurrentStateType().getId());
		if(!isEditionAllowed()) {
			alert("No se puede modificar porque el Plan de Produccion esta Cancelado, Lanzado, En Ejecucion o Finalizado.");
			return;
		}
		if(lastStateType.equals(productionOrderStateTypeRepository.findFirstByName("Cancelada"))) {
			alert("No se puede modificar una Orden de Produccion Cancelada.");
			return;
		}
		//		int selectedIndexWorker = workerCombobox.getSelectedIndex();
		//		if (selectedIndexWorker == -1) {// no hay un empleado seleccionado
		//			Clients.showNotification("Debe seleccionar un Empleado");
		//			return;
		//		}
		for (ProductionOrderDetail productionOrderDetail : productionOrderDetailList) {
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
		//		Integer productionOrderNumber = productionOrderNumberSpinner.getValue();
		//		Worker productionOrderWorker = workerCombobox.getSelectedItem().getValue();
		Date productionOrderDateStart = productionOrderStartDatebox.getValue();
		Date productionOrderDateFinish = productionOrderFinishDatebox.getValue();
		if (productionOrderDateStart == null) {
			Clients.showNotification("Debe Seleccionar Fecha de Inicio", productionOrderStartDatebox);
			return;
		}
		//Date productionOrderRealDateStart = productionOrderRealStartDatebox.getValue();
		//Date productionOrderRealDateFinish = productionOrderRealFinishDatebox.getValue();
		//		currentProductionOrder.setNumber(productionOrderNumber);
		if(currentProductionOrder.getNumber() == 0) {
			currentProductionOrder.setNumber(getNewProductionOrderNumber());
		}
		//		currentProductionOrder.setWorker(productionOrderWorker);
		currentProductionOrder.setDateStart(productionOrderDateStart);
		currentProductionOrder.setDateFinish(productionOrderDateFinish);
		//currentProductionOrder.setDateStartReal(productionOrderRealDateStart);
		//currentProductionOrder.setDateFinishReal(productionOrderRealDateFinish);
		productionOrderDetailList = productionOrderDetailRepository.save(productionOrderDetailList);
		currentProductionOrder.setDetails(productionOrderDetailList);

		productionOrderSupplyList = productionOrderSupplyRepository.save(productionOrderSupplyList);
		productionOrderRawMaterialList = productionOrderRawMaterialRepository.save(productionOrderRawMaterialList);
		currentProductionOrder.setProductionOrderSupplies(productionOrderSupplyList);
		currentProductionOrder.setProductionOrderRawMaterials(productionOrderRawMaterialList);

		// el estado de la orden debe cambiar automaticamente 
		ProductionOrderStateType productionOrderStateType = productionOrderStateTypeRepository.findFirstByName("Preparada");
		if(!productionOrderStateType.equals(lastStateType)) { // no se graba si es el mismo estado
			ProductionOrderState productionOrderState = new ProductionOrderState(productionOrderStateType, new Date());
			productionOrderState = productionOrderStateRepository.save(productionOrderState);
			currentProductionOrder.setState(productionOrderState);
		}

		currentProductionOrder = productionOrderRepository.save(currentProductionOrder);

//		updateProductionPlanState();

		alert("Orden de Produccion Guardada.");
		//cancelButtonClick();
		refreshView();
	}
	
	/*
	private void updateProductionPlanState() {
		// recorre todas las ordenes del plan y comprueba
		// si no inicio ninguna, se guarda el estado en base a sus requerimientos
		// si inicio 1 o mas pero no todas, se guarda estado "En Ejecucion" si no es ese el estado actual
		// si finalizaron todas, se guarda estado "Finalizado" si no es ese el estado actual
		ProductionPlanStateType currentProductionPlanStateType = currentProductionPlan.getCurrentStateType();
		ProductionPlanStateType newProductionPlanStateType = null;
		boolean allNotStarted = true;
		boolean allFinish = true;
		List<ProductionOrder> productionOrderList = productionOrderRepository.findByProductionPlan(currentProductionPlan);
		for(ProductionOrder each : productionOrderList) {
			if(!productionOrderStateTypeRepository.findOne(each.getCurrentStateType().getId()).equals(productionOrderStateTypeRepository.findFirstByName("Registrada"))) {
				// si existe alguna con estado diferente de no iniciado
				allNotStarted = false;
			}
			if(!productionOrderStateTypeRepository.findOne(each.getCurrentStateType().getId()).equals(productionOrderStateTypeRepository.findFirstByName("Finalizada"))) {
				// si existe alguna con estado diferente de finalizado
				allFinish = false;
			}
		}

		OrderStateType orderStateType = null;
		if(allFinish) {
			newProductionPlanStateType = productionPlanStateTypeRepository.findFirstByName("Finalizado");
			orderStateType = orderStateTypeRepository.findFirstByName("Finalizado");
		} else if(allNotStarted) {// ninguno iniciado, se busca el estado del plan en base a sus requerimientos
			newProductionPlanStateType = getProductionPlanStateType();
			orderStateType = orderStateTypeRepository.findFirstByName("Planificado");
		} else {
			newProductionPlanStateType = productionPlanStateTypeRepository.findFirstByName("En Ejecucion");
			orderStateType = orderStateTypeRepository.findFirstByName("En Produccion");
		}

		//si el estado actual es igual al nuevo no se realiza el guardado
		if(newProductionPlanStateType!=null && !productionPlanStateTypeRepository.findOne(currentProductionPlanStateType.getId()).equals(newProductionPlanStateType)) {
			ProductionPlanState productionPlanState = new ProductionPlanState(newProductionPlanStateType, new Date());
			productionPlanState = productionPlanStateRepository.save(productionPlanState);
			currentProductionPlan.setState(productionPlanState);
			currentProductionPlan = productionPlanRepository.save(currentProductionPlan);

			// se cambia el estado de todos los pedidos en base al estado del plan
			if(orderStateType != null) {
				List<ProductionPlanDetail> productionPlanDetailList = currentProductionPlan.getPlanDetails();
				for(ProductionPlanDetail each : productionPlanDetailList) {
					Order order = each.getOrder();
					OrderState state = new OrderState(orderStateType, new Date());
					state = orderStateRepository.save(state);
					order.setState(state);
					orderRepository.save(order);
				}
			}
		}

	}*/

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

	@Listen("onChange = #productionOrderStartDatebox")
	public void productionOrderStartDateboxOnChange() {
		//Date finishDate = getFinishDate(productionOrderStartDatebox.getValue(), currentProductionOrder.getDurationTotal());
		//productionOrderFinishDatebox.setValue(finishDate);
	}

	private ProductionPlanStateType getProductionPlanStateType() {
		// recorre todos los requerimientos para ver si estan todos abastecidos
		boolean isCompleted = true;
		List<SupplyRequirement> supplyRequirementList = currentProductionPlan.getSupplyRequirements();
		for(SupplyRequirement each : supplyRequirementList) {
			if(!isSupplyRequirementFulfilled(each)) {
				isCompleted = false;
				break;
			}
		}
		List<RawMaterialRequirement> rawMaterialRequirementList = currentProductionPlan.getRawMaterialRequirements();
		if(isCompleted) {// si no cambio el valor a false en el anterior loop for
			for(RawMaterialRequirement each : rawMaterialRequirementList) {
				if(!isRawMaterialRequirementFulfilled(each)) {
					isCompleted = false;
					break;
				}
			}
		}
		ProductionPlanStateType productionPlanStateType = null;
		if(isCompleted) {
			productionPlanStateType = productionPlanStateTypeRepository.findFirstByName("Abastecido");
		} else {
			productionPlanStateType = productionPlanStateTypeRepository.findFirstByName("Registrado");
		}
		return productionPlanStateType;
	}

	private boolean isRawMaterialRequirementFulfilled(RawMaterialRequirement supplyRequirement) {
		// si ya se ha reservado la cantidad necesaria
		boolean value = false;
		if(getRawMaterialRequirementStockMissing(supplyRequirement).doubleValue() == 0) {
			value = true;
		}
		return value;
	}

	private BigDecimal getRawMaterialRequirementStockMissing(RawMaterialRequirement rawMaterialRequirement) {
		return rawMaterialRequirement.getQuantity().subtract(getRawMaterialRequirementStockReserved(rawMaterialRequirement));
	}

	private BigDecimal getRawMaterialRequirementStockReserved(RawMaterialRequirement rawMaterialRequirement) {
		// busca todos los woodReserved, ya que puede haber, para el mismo requirement, un wood reserved de cada tipo de madera
		List<WoodReserved> woodReserved = woodReservedRepository.findByRawMaterialRequirement(rawMaterialRequirement);
		if(woodReserved.isEmpty()) {
			return BigDecimal.ZERO;
		} else {
			BigDecimal stockReserved = BigDecimal.ZERO;
			for(WoodReserved each : woodReserved) {
				stockReserved = stockReserved.add(each.getStockReserved());
			}
			return stockReserved;
		}
	}

	private boolean isSupplyRequirementFulfilled(SupplyRequirement supplyRequirement) {
		// si ya se ha reservado la cantidad necesaria
		boolean value = false;
		if(getSupplyStockMissing(supplyRequirement).doubleValue() == 0) {
			value = true;
		}
		return value;
	}

	private BigDecimal getSupplyStockMissing(SupplyRequirement supplyRequirement) {
		return supplyRequirement.getQuantity().subtract(getSupplyStockReserved(supplyRequirement));
	}

	private BigDecimal getSupplyStockReserved(SupplyRequirement supplyRequirement) {
		SupplyReserved supplyReserved = supplyReservedRepository.findBySupplyRequirement(supplyRequirement);
		if(supplyReserved == null) {
			return BigDecimal.ZERO;
		} else {
			return supplyReserved.getStockReserved();
		}
	}

	//	@Listen("onClick = #chooseWorkerButton")
	//	public void chooseWorkerButtonClick() {
	//		if(candidateWorkerListbox.getSelectedIndex() != -1) {
	//			Worker selectedWorker = candidateWorkerListModel.getElementAt(candidateWorkerListbox.getSelectedIndex());
	//			candidateWorkerListModel.remove(selectedWorker);
	//			candidateWorkerListbox.setModel(candidateWorkerListModel);
	//			chosenWorkerListModel.add(selectedWorker);
	//			chosenWorkerListbox.setModel(chosenWorkerListModel);
	//		}
	//	}
	//	
	//	@Listen("onClick = #removeWorkerButton")
	//	public void removeWorkerButtonClick() {
	//		if(chosenWorkerListbox.getSelectedIndex() != -1) {
	//			Worker selectedWorker = candidateWorkerListModel.getElementAt(candidateWorkerListbox.getSelectedIndex());
	//			chosenWorkerListModel.remove(selectedWorker);
	//			chosenWorkerListbox.setModel(chosenWorkerListModel);
	//			candidateWorkerListModel.add(selectedWorker);
	//			candidateWorkerListbox.setModel(candidateWorkerListModel);
	//		}
	//	}

	public ListModelList<Machine> getMachineListModelByProcessType(ProcessType processType) {
		List<Machine> list = new ArrayList<Machine>();
		MachineType machineType = machineTypeRepository.findOne(processType.getMachineType().getId());
		if (machineType != null) {
			for (Machine machine : machineList) {
				if (machineType.equals(machineTypeRepository.findOne(machine.getMachineType().getId()))) {
					list.add(machine);
				}
			}
		}
		return new ListModelList<>(list);
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
		processTypeGrid.setModel(new ListModelList<ProcessType>(processTypeList));
	}

	private List<ProcessType> getProcessTypeTotalList() {
		Set<ProcessType> processTypeSet = new HashSet<ProcessType>();
		for(ProductionOrderDetail eachProductionOrderDetail : productionOrderDetailList) {
			processTypeSet.add(eachProductionOrderDetail.getProcess().getType());// garantiza que los tipo de procesos no se repitan
		}
		List<ProcessType> list = new ArrayList<ProcessType>();
		for (ProcessType eachProcessType : processTypeSet) {
			list.add(eachProcessType);
		}
		return list;
	}

}
