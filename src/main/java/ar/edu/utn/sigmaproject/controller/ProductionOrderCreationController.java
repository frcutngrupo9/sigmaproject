package ar.edu.utn.sigmaproject.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.Duration;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
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
import org.zkoss.zul.Selectbox;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Machine;
import ar.edu.utn.sigmaproject.domain.MachineType;
import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.domain.ProductTotal;
import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.domain.ProductionOrderState;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanState;
import ar.edu.utn.sigmaproject.domain.Worker;
import ar.edu.utn.sigmaproject.service.MachineService;
import ar.edu.utn.sigmaproject.service.MachineTypeService;
import ar.edu.utn.sigmaproject.service.PieceService;
import ar.edu.utn.sigmaproject.service.ProcessService;
import ar.edu.utn.sigmaproject.service.ProcessTypeService;
import ar.edu.utn.sigmaproject.service.ProductionOrderDetailService;
import ar.edu.utn.sigmaproject.service.ProductionOrderService;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateService;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeService;
import ar.edu.utn.sigmaproject.service.WorkerService;
import ar.edu.utn.sigmaproject.service.impl.MachineServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.MachineTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.PieceServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProcessServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProcessTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionOrderDetailServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionOrderServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanStateServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanStateTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.WorkerServiceImpl;

public class ProductionOrderCreationController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox productionPlanNameTextbox;
	@Wire
	Textbox productionPlanStateTypeTextbox;
	@Wire
	Textbox productNameTextbox;
	@Wire
	Datebox productionPlanDatebox;
	@Wire
	Grid productionOrderDetailGrid;
	@Wire
	Spinner productionOrderNumberSpinner;
	@Wire
	Intbox productUnitsIntbox;
	@Wire
	Selectbox workerSelectbox;
	@Wire
	Datebox productionOrderDatebox;
	@Wire
	Datebox productionOrderFinishedDatebox;
	@Wire
	Button saveButton;
	@Wire
	Button cancelButton;
	@Wire
	Button resetButton;
	@Wire
	Combobox productionOrderStateCombobox;

	// services
	private ProductionOrderService productionOrderService = new ProductionOrderServiceImpl();
	private ProductionOrderDetailService productionOrderDetailService = new ProductionOrderDetailServiceImpl();
	private PieceService pieceService = new PieceServiceImpl();
	private ProcessService processService = new ProcessServiceImpl();
	private ProcessTypeService processTypeService = new ProcessTypeServiceImpl();
	private WorkerService workerService = new WorkerServiceImpl();
	private MachineService machineService = new MachineServiceImpl();
	private MachineTypeService machineTypeService = new MachineTypeServiceImpl();
	private ProductionPlanStateService productionPlanStateService = new ProductionPlanStateServiceImpl();
	private ProductionPlanStateTypeService productionPlanStateTypeService = new ProductionPlanStateTypeServiceImpl();

	// atributes
	private ProductionOrder currentProductionOrder;
	private ProductTotal currentProduct;
	private ProductionPlan currentProductionPlan;

	// list
	private List<ProductionOrderDetail> productionOrderDetailList;
	private List<Worker> workerList;
	private List<Machine> machineList;

	// list models
	private ListModelList<ProductionOrderDetail> productionOrderDetailListModel;
	private ListModelList<Worker> workerListModel;
	private ListModelList<ProductionOrderState> productionOrderStateListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		
		currentProduct = (ProductTotal) Executions.getCurrent().getAttribute("selected_product");
		currentProductionPlan = (ProductionPlan) Executions.getCurrent().getAttribute("selected_production_plan");
		currentProductionOrder = productionOrderService.getProductionOrder(currentProductionPlan, currentProduct);

		if(currentProductionOrder == null) {// es una nueva orden de produccion, se deben crear los detalles
			List<Process> processList = new ArrayList<Process>();// lista donde se guardaran todos los procesos del producto
			List<Piece> auxPieceList = pieceService.getPieceList(currentProduct.getId());
			for(Piece piece : auxPieceList) {
				List<Process> auxProcessList = processService.getProcessList(piece.getId());
				for(Process process : auxProcessList) {
					processList.add(Process.clone(process));
				}
			}
			// por cada proceso hay que crear un detalle de orden de produccion
			productionOrderDetailList = new ArrayList<ProductionOrderDetail>(); 
			for(Process process : processList) {
				Integer idProcess = process.getId();
				Integer quantityPiece = currentProduct.getTotalUnits() * pieceService.getPiece(process.getIdPiece()).getUnits();// cantidad total de la pieza
				Duration timeTotal = process.getTime().multiply(quantityPiece);// cantidad total de tiempo del proceso
				productionOrderDetailList.add(new ProductionOrderDetail(null, idProcess, null, timeTotal, quantityPiece));
			}
		} else {// es una orden de produccion ya creada, se buscan sus detalles
			productionOrderDetailList = productionOrderDetailService.getProductionOrderDetailList(currentProductionOrder.getId());
		}
		productionOrderDetailListModel = new ListModelList<ProductionOrderDetail>(productionOrderDetailList);

		workerList = workerService.getWorkerList();
		workerListModel = new ListModelList<Worker>(workerList);
		workerSelectbox.setModel(workerListModel);
		
		machineList = machineService.getMachineList();
		
		productionOrderStateListModel = new ListModelList<ProductionOrderState>(ProductionOrderState.values());
		productionOrderStateCombobox.setModel(productionOrderStateListModel);
	}
	
	@Listen("onAfterRender = #productionOrderStateCombobox")
	public void productCategoryComboboxSelection() {// se hace refresh despues de q se renderizo el combobox para que se le pueda setear un valor seleccionado
		refreshView();
	}

	private void refreshView() {
		productionPlanNameTextbox.setDisabled(true);
		productNameTextbox.setDisabled(true);
		productionPlanDatebox.setDisabled(true);
		productUnitsIntbox.setDisabled(true);
		productionPlanStateTypeTextbox.setDisabled(true);
		productionPlanNameTextbox.setText(currentProductionPlan.getName());
		productionPlanDatebox.setValue(currentProductionPlan.getDate());
		productNameTextbox.setText(currentProduct.getName());
		productUnitsIntbox.setValue(currentProduct.getTotalUnits());
		ProductionPlanState lastProductionPlanState = productionPlanStateService.getLastProductionPlanState(currentProductionPlan.getId());
		if(lastProductionPlanState != null) {
			productionPlanStateTypeTextbox.setText(productionPlanStateTypeService.getProductionPlanStateType(lastProductionPlanState.getIdProductionPlanStateType()).getName().toUpperCase());
		} else {
			productionPlanStateTypeTextbox.setText("[Sin Estado]");
		}
		productionOrderDetailGrid.setModel(productionOrderDetailListModel);
		if(currentProductionOrder == null) {// nueva orden de produccion
			productionOrderNumberSpinner.setValue(null);
			productionOrderDatebox.setValue(new Date());
			workerSelectbox.setSelectedIndex(-1);
			productionOrderFinishedDatebox.setValue(null);
			productionOrderStateCombobox.setDisabled(true);
			productionOrderStateCombobox.setSelectedIndex(productionOrderStateListModel.indexOf(ProductionOrderState.Generada));
		} else {// edicion de orden de produccion
			productionOrderNumberSpinner.setValue(currentProductionOrder.getNumber());
			productionOrderDatebox.setValue(currentProductionOrder.getDate());
			Integer workerId = currentProductionOrder.getIdWorker();
			if(workerId != null) {
				Worker aux = workerService.getWorker(workerId);
				workerListModel.addToSelection(aux);
				workerSelectbox.setModel(workerListModel);
			} else {
				workerSelectbox.setSelectedIndex(-1);
			}
			productionOrderFinishedDatebox.setValue(currentProductionOrder.getDateFinished());
			productionOrderStateCombobox.setDisabled(false);
			productionOrderStateCombobox.setSelectedIndex(productionOrderStateListModel.indexOf(currentProductionOrder.getState()));
		}
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		resetButton.setDisabled(false);
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		int selectedIndexWorker = workerSelectbox.getSelectedIndex();
		if(selectedIndexWorker == -1) {// no hay un empleado seleccionado
			Clients.showNotification("Debe seleccionar un Empleado", workerSelectbox);
			return;
		}
		for(ProductionOrderDetail productionOrderDetail : productionOrderDetailList) {
			Process process = processService.getProcess(productionOrderDetail.getIdProcess());
			ProcessType processType = processTypeService.getProcessType(process.getIdProcessType());
			MachineType machineType = machineTypeService.getMachineType(processType.getIdMachineType());
			if(machineType != null) {
				if(productionOrderDetail.getIdMachine() == null) {
					Clients.showNotification("Existen Procesos sin Maquina Asignada", productionOrderDetailGrid);
					return;
				}
			}
		}
		Integer productionOrderNumber = productionOrderNumberSpinner.getValue();
		Worker productionOrderWorker = workerListModel.getElementAt(workerSelectbox.getSelectedIndex());
		Date productionOrderDate = productionOrderDatebox.getValue();
		Date productionOrderDateFinished = productionOrderFinishedDatebox.getValue();
		ProductionOrderState productionOrderState = productionOrderStateCombobox.getSelectedItem().getValue();

		if(currentProductionOrder == null) {// nueva orden de produccion
			currentProductionOrder = new ProductionOrder(null, currentProductionPlan.getId(), currentProduct.getId(), productionOrderWorker.getId(), productionOrderNumber, currentProduct.getTotalUnits(), productionOrderDate, productionOrderDateFinished, productionOrderState);
			currentProductionOrder = productionOrderService.saveProductionOrder(currentProductionOrder, productionOrderDetailList);
		} else {// se edita
			currentProductionOrder.setNumber(productionOrderNumber);
			currentProductionOrder.setIdWorker(productionOrderWorker.getId());
			currentProductionOrder.setDate(productionOrderDate);
			currentProductionOrder.setDateFinished(productionOrderDateFinished);
			currentProductionOrder.setState(productionOrderState);
			currentProductionOrder = productionOrderService.updateProductionOrder(currentProductionOrder, productionOrderDetailList);
		}
		alert("Orden de Produccion Guardada.");
		cancelButtonClick();// volvemos a la lista de ordenes de produccion
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		Executions.getCurrent().setAttribute("selected_production_plan", currentProductionPlan);
		Include include = (Include) Selectors.iterable(productionOrderDetailGrid.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/production_order_list.zul");
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
	}

	public Process getProcess(int idProcess) {
		return processService.getProcess(idProcess);
	}

	public ProcessType getProcessType(int idProcess) {
		return processTypeService.getProcessType(getProcess(idProcess).getIdProcessType());
	}

	public String getProcessTime(int idProcess) {
		return getFormatedTime(getProcess(idProcess).getTime());
	}

	public Piece getPiece(int idPiece) {
		return pieceService.getPiece(idPiece);
	}

	public Piece getPieceByProcessId(int idProcess) {
		return pieceService.getPiece(getProcess(idProcess).getIdPiece());
	}

	public String getFormatedTime(Duration time) {
		return String.format("Dias: %d Horas: %d Minutos: %d", time.getDays(), time.getHours(), time.getMinutes());
	}
	
	public ListModelList<Machine> getMachineListModel(ProductionOrderDetail productionOrderDetail) {
		List<Machine> list = new ArrayList<Machine>();
		MachineType machineType = machineTypeService.getMachineType(processTypeService.getProcessType(processService.getProcess(productionOrderDetail.getIdProcess()).getIdProcessType()).getIdMachineType());
		if(machineType != null) {
			for(Machine machine : machineList) {
				if(machineType.getId().equals(machine.getIdMachineType())) {
					list.add(Machine.clone(machine));
				}
			}
		}
		return new ListModelList<Machine>(list);
	}
	
	public String getMachineTypeName(ProductionOrderDetail productionOrderDetail) {
		String name = "Ninguna";
		Process process = processService.getProcess(productionOrderDetail.getIdProcess());
		ProcessType processType = processTypeService.getProcessType(process.getIdProcessType());
		MachineType machineType = machineTypeService.getMachineType(processType.getIdMachineType());
		if(machineType != null) {
			name = machineType.getName();
		}
		return name;
	}
	
	@Listen("onCreateMachineCombobox = #productionOrderDetailGrid")
	public void doCreateMachineCombobox(ForwardEvent evt) {// metodo utilizado para seleccionar el item del combobox luego de crearlo
		ProductionOrderDetail data = (ProductionOrderDetail) evt.getData();// obtenemos el objeto pasado por parametro
		Combobox element = (Combobox) evt.getOrigin().getTarget();// obtenemos el elemento web
		int value = -1;
		for(int i = 0; i < element.getItems().size(); i++){
			if(element.getItems().get(i) != null){
				Comboitem item = (Comboitem)element.getItems().get(i);
				if(((Machine)item.getValue()).getId().equals(data.getIdMachine())){
					value = i;
				}
			}
		}
		element.setSelectedIndex(value);
	}
	
	@Listen("onEditProductionOrderDetailMachine = #productionOrderDetailGrid")
	public void doEditProductionOrderDetailMachine(ForwardEvent evt) {
		ProductionOrderDetail data = (ProductionOrderDetail) evt.getData();// obtenemos el objeto pasado por parametro
		Combobox element = (Combobox) evt.getOrigin().getTarget();// obtenemos el elemento web
		data.setIdMachine(((Machine)element.getSelectedItem().getValue()).getId());// cargamos al objeto el valor actualizado del elemento web
	}

	@Listen("onEditProductionOrderDetailQuantityFinished = #productionOrderDetailGrid")
	public void doEditProductionOrderDetailQuantityFinished(ForwardEvent evt) {
		ProductionOrderDetail data = (ProductionOrderDetail) evt.getData();// obtenemos el objeto pasado por parametro
		Doublebox element = (Doublebox) evt.getOrigin().getTarget();// obtenemos el elemento web
		data.setQuantityFinished(element.getValue());// cargamos al objeto el valor actualizado del elemento web
	}

	@Listen("onEditProductionOrderDetailIsFinished = #productionOrderDetailGrid")
	public void doEditProductionOrderDetailIsFinished(ForwardEvent evt) {
		ProductionOrderDetail data = (ProductionOrderDetail) evt.getData();// obtenemos el objeto pasado por parametro
		Checkbox element = (Checkbox) evt.getOrigin().getTarget();// obtenemos el elemento web
		data.setFinished(element.isChecked());// cargamos al objeto el valor actualizado del elemento web
	}
}
