package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.Duration;

import org.springframework.transaction.annotation.Transactional;
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
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Spinner;
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
import ar.edu.utn.sigmaproject.domain.ProductionPlanState;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.domain.RawMaterialRequirement;
import ar.edu.utn.sigmaproject.domain.SupplyRequirement;
import ar.edu.utn.sigmaproject.domain.SupplyReserved;
import ar.edu.utn.sigmaproject.domain.WoodReserved;
import ar.edu.utn.sigmaproject.domain.Worker;
import ar.edu.utn.sigmaproject.service.MachineRepository;
import ar.edu.utn.sigmaproject.service.MachineTypeRepository;
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
	Textbox productNameTextbox;
	@Wire
	Datebox productionPlanCreationDatebox;
	@Wire
	Grid productionOrderDetailGrid;
	@Wire
	Spinner productionOrderNumberSpinner;
	@Wire
	Intbox productUnitsIntbox;
	@Wire
	Combobox workerCombobox;
	@Wire
	Datebox productionOrderStartDatebox;
	@Wire
	Datebox productionOrderFinishDatebox;
	@Wire
	Datebox productionOrderRealStartDatebox;
	@Wire
	Datebox productionOrderRealFinishDatebox;
	@Wire
	Button saveButton;
	@Wire
	Button cancelButton;
	@Wire
	Button resetButton;
	@Wire
	Combobox productionOrderStateTypeCombobox;
	@Wire
	Button generateDetailsButton;
	@Wire
	Listbox productionOrderSupplyListbox;
	@Wire
	Listbox productionOrderRawMaterialListbox;

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

	// atributes
	private ProductionOrder currentProductionOrder;
	private ProductionPlan currentProductionPlan;

	// list
	private List<ProductionOrderDetail> productionOrderDetailList;
	private List<Worker> workerList;
	private List<Machine> machineList;
	private List<ProductionOrderSupply> productionOrderSupplyList;
	private List<ProductionOrderRawMaterial> productionOrderRawMaterialList;

	// list models
	private ListModelList<ProductionOrderDetail> productionOrderDetailListModel;
	private ListModelList<Worker> workerListModel;
	private ListModelList<ProductionOrderStateType> productionOrderStateTypeListModel;
	private ListModelList<ProductionOrderSupply> productionOrderSupplyListModel;
	private ListModelList<ProductionOrderRawMaterial> productionOrderRawMaterialListModel;

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
		
		productionOrderSupplyList = currentProductionOrder.getProductionOrderSupplies();
		productionOrderRawMaterialList = currentProductionOrder.getProductionOrderRawMaterials();
		productionOrderSupplyListModel = new ListModelList<ProductionOrderSupply>(productionOrderSupplyList);
		productionOrderRawMaterialListModel = new ListModelList<ProductionOrderRawMaterial>(productionOrderRawMaterialList);
		productionOrderSupplyListbox.setModel(productionOrderSupplyListModel);
		productionOrderRawMaterialListbox.setModel(productionOrderRawMaterialListModel);

		refreshView();
	}

	private void refreshView() {
		productionOrderRealStartDatebox.setDisabled(true);
		productionOrderRealFinishDatebox.setDisabled(true);
		productionPlanNameTextbox.setDisabled(true);
		productNameTextbox.setDisabled(true);
		productionPlanCreationDatebox.setDisabled(true);
		productUnitsIntbox.setDisabled(true);
		productionPlanStateTypeTextbox.setDisabled(true);
		productionPlanNameTextbox.setText(currentProductionPlan.getName());
		productionPlanCreationDatebox.setValue(currentProductionPlan.getDateCreation());
		ProductionPlanStateType lastProductionPlanStateType = currentProductionPlan.getCurrentStateType();
		if(lastProductionPlanStateType != null) {
			productionPlanStateTypeTextbox.setText(lastProductionPlanStateType.getName());
		} else {
			productionPlanStateTypeTextbox.setText("[Sin Estado]");
		}
		productNameTextbox.setText(currentProductionOrder.getProduct().getName());
		productUnitsIntbox.setValue(currentProductionOrder.getUnits());
		if(currentProductionOrder.getNumber()!=null && currentProductionOrder.getNumber()!=0) {
			productionOrderNumberSpinner.setValue(currentProductionOrder.getNumber());
		} else {
			productionOrderNumberSpinner.setValue(getNewProductionOrderNumber());
		}
		workerList = workerRepository.findAll();
		workerListModel = new ListModelList<>(workerList);
		if (currentProductionOrder.getWorker() != null) {
			workerListModel.addToSelection(workerRepository.findOne(currentProductionOrder.getWorker().getId()));
		}
		workerCombobox.setModel(workerListModel);
		productionOrderStartDatebox.setValue(currentProductionOrder.getDateStart());
		productionOrderFinishDatebox.setValue(currentProductionOrder.getDateFinish());
		productionOrderRealStartDatebox.setValue(currentProductionOrder.getDateStartReal());
		productionOrderRealFinishDatebox.setValue(currentProductionOrder.getDateFinishReal());
		refreshProductionOrderDetailGridView();
		List<ProductionOrderStateType> productionOrderStateTypeList = productionOrderStateTypeRepository.findAll();
		productionOrderStateTypeListModel = new ListModelList<ProductionOrderStateType>(productionOrderStateTypeList);
		productionOrderStateTypeListModel.addToSelection(productionOrderStateTypeRepository.findOne(currentProductionOrder.getCurrentStateType().getId()));
		productionOrderStateTypeCombobox.setModel(productionOrderStateTypeListModel);
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		resetButton.setDisabled(false);
	}

	private void refreshProductionOrderDetailGridView() {
		productionOrderDetailListModel = new ListModelList<ProductionOrderDetail>(productionOrderDetailList);
		productionOrderDetailGrid.setModel(productionOrderDetailListModel);
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

	@Transactional
	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		ProductionOrderStateType lastStateType = productionOrderStateTypeRepository.findOne(currentProductionOrder.getCurrentStateType().getId());
		
		if(lastStateType.equals(productionOrderStateTypeRepository.findFirstByName("Cancelada"))) {
			alert("No se puede modificar una Orden de Produccion Cancelada.");
			return;
		}
		
		ProductionOrderStateType newStateType = getProductionOrderStateType();
		// primero se verifica si se esta iniciando la orden, (la cantidad finalizada de todos los procesos era 0 y ahora no)
		boolean isStartingNow = false;
		if(lastStateType.equals(productionOrderStateTypeRepository.findFirstByName("Generada")) && newStateType.equals(productionOrderStateTypeRepository.findFirstByName("Iniciada"))) {
				isStartingNow = true;// se esta iniciando
		}
		// comprueba que el estado del plan de produccion sea abastecido
		if(isStartingNow == true) {
			ProductionPlanStateType productionPlanStateTypeAbastecido = productionPlanStateTypeRepository.findFirstByName("Abastecido");
			ProductionPlanStateType productionPlanStateTypeEnEjecucion = productionPlanStateTypeRepository.findFirstByName("En Ejecucion");
			boolean distintoAbastecido = !productionPlanStateTypeAbastecido.equals(productionPlanStateTypeRepository.findOne(currentProductionPlan.getCurrentStateType().getId()));
			boolean distintoEnEjecucion = !productionPlanStateTypeEnEjecucion.equals(productionPlanStateTypeRepository.findOne(currentProductionPlan.getCurrentStateType().getId()));
			if(distintoAbastecido && distintoEnEjecucion) {//si el plan no esta abastecido o en ejecucion
				Clients.showNotification("No se puede iniciar la Orden hasta que el plan no este Abastecido");
				return;
			}
		}

		int selectedIndexWorker = workerCombobox.getSelectedIndex();
		if (selectedIndexWorker == -1) {// no hay un empleado seleccionado
			Clients.showNotification("Debe seleccionar un Empleado");
			return;
		}
		for (ProductionOrderDetail productionOrderDetail : productionOrderDetailList) {
			Process process = productionOrderDetail.getProcess();
			ProcessType processType = process.getType();
			MachineType machineType = processType.getMachineType();
			if (machineType != null) {
				if (productionOrderDetail.getMachine() == null) {
					Clients.showNotification("Existen Procesos sin Maquina Asignada", productionOrderDetailGrid);
					return;
				}
			}
		}
		Integer productionOrderNumber = productionOrderNumberSpinner.getValue();
		Worker productionOrderWorker = workerCombobox.getSelectedItem().getValue();
		Date productionOrderDateStart = productionOrderStartDatebox.getValue();
		Date productionOrderDateFinish = productionOrderFinishDatebox.getValue();
		//Date productionOrderRealDateStart = productionOrderRealStartDatebox.getValue();
		//Date productionOrderRealDateFinish = productionOrderRealFinishDatebox.getValue();
		currentProductionOrder.setNumber(productionOrderNumber);
		currentProductionOrder.setWorker(productionOrderWorker);
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

		// el estado de la orden debe cambiar automaticamente en base a las modificaciones que se le hagan y no se deberia poder cambiar manualmente excepto en el caso de la cancelacion
		ProductionOrderStateType productionOrderStateType = null;
		if(!lastStateType.equals(newStateType)) {// si el estado anterior es distindo del nuevo
			if(newStateType.equals(productionOrderStateTypeRepository.findFirstByName("Generada"))) {// si el nuevo estado es no iniciado
				currentProductionOrder.setDateStartReal(null);
				currentProductionOrder.setDateFinishReal(null);
			} else if(newStateType.equals(productionOrderStateTypeRepository.findFirstByName("Iniciada"))) {
				currentProductionOrder.setDateStartReal(new Date());
				currentProductionOrder.setDateFinishReal(null);
			} else if(newStateType.equals(productionOrderStateTypeRepository.findFirstByName("Finalizada"))) {
				currentProductionOrder.setDateFinishReal(new Date());
				if(currentProductionOrder.getDateStartReal() == null) {
					currentProductionOrder.setDateStartReal(new Date());
				}
			}
			productionOrderStateType = newStateType;
		}
		if(productionOrderStateType != null) {
			ProductionOrderState productionOrderState = new ProductionOrderState(productionOrderStateType, new Date());
			productionOrderState = productionOrderStateRepository.save(productionOrderState);
			currentProductionOrder.setState(productionOrderState);
		}
		
		currentProductionOrder = productionOrderRepository.save(currentProductionOrder);

		updateProductionPlanState();
		
		alert("Orden de Produccion Guardada.");
		//cancelButtonClick();
		refreshView();
	}
	
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
			if(!productionOrderStateTypeRepository.findOne(each.getCurrentStateType().getId()).equals(productionOrderStateTypeRepository.findFirstByName("Generada"))) {
				// si existe alguna con estado diferente de no iniciado
				allNotStarted = false;
			}
			if(!productionOrderStateTypeRepository.findOne(each.getCurrentStateType().getId()).equals(productionOrderStateTypeRepository.findFirstByName("Finalizada"))) {
				// si existe alguna con estado diferente de finalizado
				allFinish = false;
			}
		}
		
		if(allFinish) {
			newProductionPlanStateType = productionPlanStateTypeRepository.findFirstByName("Finalizado");
		} else if(allNotStarted) {// ninguno iniciado, se busca el estado del plan en base a sus requerimientos
			newProductionPlanStateType = getProductionPlanStateType();
		} else {
			newProductionPlanStateType = productionPlanStateTypeRepository.findFirstByName("En Ejecucion");
		}
		
		//si el estado actual es igual al nuevo no se realiza el guardado
		if(newProductionPlanStateType!=null && !productionPlanStateTypeRepository.findOne(currentProductionPlanStateType.getId()).equals(newProductionPlanStateType)) {
			ProductionPlanState productionPlanState = new ProductionPlanState(newProductionPlanStateType, new Date());
			productionPlanState = productionPlanStateRepository.save(productionPlanState);
			currentProductionPlan.setState(productionPlanState);
			currentProductionPlan = productionPlanRepository.save(currentProductionPlan);
		}
		
	}
	
	//"Planificado""Abastecido""En Ejecucion""Finalizado""Cancelado""Suspendido"
	
	private ProductionOrderStateType getProductionOrderStateType() {
		// devuelve el estado actual de la orden de produccion
		// "No iniciada" si no inicio, "Iniciada" o "Finalizada" si finalizaron todos los detalles
		ProductionOrderStateType productionOrderStateType = null;
		boolean finished = true;// si se finalizo todos los procesos
		boolean notStarted = true;// no inicio ningun proceso
		for (ProductionOrderDetail each : productionOrderDetailList) {
			if(each.isFinished()==true || each.getQuantityFinished().compareTo(BigDecimal.ZERO)!=0) {
				// si inicio o finalizo algun proceso
				notStarted = false;
			}
			if(each.isFinished()!=true) {
				// si no finalizo algun proceso
				finished = false;
			}
		}
		
		if(finished) {
			productionOrderStateType = productionOrderStateTypeRepository.findFirstByName("Finalizada");
		} else if(notStarted) {
			productionOrderStateType = productionOrderStateTypeRepository.findFirstByName("Generada");
		} else {// si no esta finalizado ni no iniciado, esta iniciado
			productionOrderStateType = productionOrderStateTypeRepository.findFirstByName("Iniciada");
		}
		return productionOrderStateType;
	}

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

	public boolean isMachineNecessary(Process process) {
		if(process.getType().getMachineType() != null) {
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

	@Listen("onEditProductionOrderDetailQuantityFinished = #productionOrderDetailGrid")
	public void doEditProductionOrderDetailQuantityFinished(ForwardEvent evt) {
		ProductionOrderDetail data = (ProductionOrderDetail) evt.getData();// obtenemos el objeto pasado por parametro
		InputEvent inputEvent = (InputEvent) evt.getOrigin();
		String inputValue = inputEvent.getValue();
		BigDecimal value = null;
		if(inputValue == null || inputValue.equals("")) {
			value = BigDecimal.ZERO;
		} else {
			value = new BigDecimal(inputValue);
		}
		BigDecimal quantityPiece = new BigDecimal(data.getQuantityPiece());
		Doublebox element = (Doublebox) evt.getOrigin().getTarget();
		Row fila = (Row)element.getParent();
		Checkbox chkbox = (Checkbox)fila.getChildren().get(fila.getChildren().size()-1);
		if(value.compareTo(quantityPiece) > 0) {
			// si el valor ingresado supera la cantidad, se lo modifica y se agrega la cantidad
			element.setValue(quantityPiece.doubleValue());
			data.setQuantityFinished(quantityPiece);
			chkbox.setChecked(true);
			data.setFinished(true);
		} else {// si el valor ingresado es igual o menos a la cantidad
			data.setQuantityFinished(value);
			if(value.compareTo(quantityPiece) == 0) {
				chkbox.setChecked(true);
				data.setFinished(true);
			} else {
				chkbox.setChecked(false);
				data.setFinished(false);
			}
		}
	}

	@Listen("onEditProductionOrderDetailIsFinished = #productionOrderDetailGrid")
	public void doEditProductionOrderDetailIsFinished(ForwardEvent evt) {
		ProductionOrderDetail data = (ProductionOrderDetail) evt.getData();// obtenemos el objeto pasado por parametro
		Checkbox element = (Checkbox) evt.getOrigin().getTarget();// obtenemos el elemento web
		data.setFinished(element.isChecked());// cargamos al objeto el valor
	}

	@Listen("onEditUsedSupply = #productionOrderSupplyListbox")
	public void doEditUsedSupply(ForwardEvent evt) {
		ProductionOrderSupply data = (ProductionOrderSupply) evt.getData();// obtenemos el objeto pasado por parametro
		InputEvent inputEvent = (InputEvent) evt.getOrigin();
		String inputValue = inputEvent.getValue();
		BigDecimal value = null;
		if(inputValue == null || inputValue.equals("")) {
			value = BigDecimal.ZERO;
		} else {
			value = new BigDecimal(inputValue);
		}
		if(value.compareTo(data.getQuantity()) > 0) {
			Doublebox element = (Doublebox) evt.getOrigin().getTarget();
			element.setValue(data.getQuantity().doubleValue());
			data.setQuantityUsed(data.getQuantity());
		} else {
			data.setQuantityUsed(value);
		}
	}

	@Listen("onEditUsedRawMaterial = #productionOrderRawMaterialListbox")
	public void doEditUsedRawMaterial(ForwardEvent evt) {
		ProductionOrderRawMaterial data = (ProductionOrderRawMaterial) evt.getData();// obtenemos el objeto pasado por parametro
		InputEvent inputEvent = (InputEvent) evt.getOrigin();
		String inputValue = inputEvent.getValue();
		BigDecimal value = null;
		if(inputValue == null || inputValue.equals("")) {
			value = BigDecimal.ZERO;
		} else {
			value = new BigDecimal(inputValue);
		}
		if(value.compareTo(data.getQuantity()) > 0) {
			Doublebox element = (Doublebox) evt.getOrigin().getTarget();
			element.setValue(data.getQuantity().doubleValue());
			data.setQuantityUsed(data.getQuantity());
		} else {
			data.setQuantityUsed(value);
		}
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
			productionPlanStateType = productionPlanStateTypeRepository.findFirstByName("Planificado");
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
	
	
}
