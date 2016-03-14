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
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.Worker;
import ar.edu.utn.sigmaproject.service.MachineService;
import ar.edu.utn.sigmaproject.service.MachineTypeService;
import ar.edu.utn.sigmaproject.service.PieceService;
import ar.edu.utn.sigmaproject.service.ProcessService;
import ar.edu.utn.sigmaproject.service.ProcessTypeService;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.ProductionOrderDetailService;
import ar.edu.utn.sigmaproject.service.ProductionOrderService;
import ar.edu.utn.sigmaproject.service.ProductionPlanService;
import ar.edu.utn.sigmaproject.service.WorkerService;
import ar.edu.utn.sigmaproject.service.impl.MachineServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.MachineTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.PieceServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProcessServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProcessTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionOrderDetailServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionOrderServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.WorkerServiceImpl;

public class ProductionOrderCreationController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox productionPlanNameTextbox;
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

	// services
	private ProductionOrderService productionOrderService = new ProductionOrderServiceImpl();
	private ProductionOrderDetailService productionOrderDetailService = new ProductionOrderDetailServiceImpl();
	private ProductionPlanService productionPlanService = new ProductionPlanServiceImpl();
	private ProductService productService = new ProductServiceImpl();
	private PieceService pieceService = new PieceServiceImpl();
	private ProcessService processService = new ProcessServiceImpl();
	private ProcessTypeService processTypeService = new ProcessTypeServiceImpl();
	private WorkerService workerService = new WorkerServiceImpl();
	private MachineService machineService = new MachineServiceImpl();
	private MachineTypeService machineTypeService = new MachineTypeServiceImpl();

	// atributes
	private ProductionOrder currentProductionOrder;

	// list
	private List<ProductionOrderDetail> productionOrderDetailList;
	private List<Worker> workerList;
	private List<Machine> machineList;

	// list models
	private ListModelList<ProductionOrderDetail> productionOrderDetailListModel;
	private ListModelList<Worker> workerListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);

		currentProductionOrder = (ProductionOrder) Executions.getCurrent().getAttribute("selected_production_order");

		if(currentProductionOrder != null) {
			if(currentProductionOrder.getId() == null) {// es una nueva orden de produccion, se deben crear los detalles de orden de produccion
				Product product = productService.getProduct(currentProductionOrder.getIdProduct());
				List<Process> processList = new ArrayList<Process>();// lista donde se guardaran todos los procesos del producto
				List<Piece> auxPieceList = pieceService.getPieceList(product.getId());
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
					Integer quantityPiece = currentProductionOrder.getUnits() * pieceService.getPiece(process.getIdPiece()).getUnits();// cantidad total de la pieza
					Duration timeTotal = process.getTime().multiply(quantityPiece);// cantidad total de tiempo del proceso
					productionOrderDetailList.add(new ProductionOrderDetail(null, idProcess, null, timeTotal, quantityPiece));
				}
			} else {// es una orden de produccion ya creada, se buscan sus detalles
				productionOrderDetailList = productionOrderDetailService.getProductionOrderDetailList(currentProductionOrder.getId());
			}
		}
		productionOrderDetailListModel = new ListModelList<ProductionOrderDetail>(productionOrderDetailList);

		workerList = workerService.getWorkerList();
		workerListModel = new ListModelList<Worker>(workerList);
		workerSelectbox.setModel(workerListModel);
		
		machineList = machineService.getMachineList();

		refreshView();
	}

	private void refreshView() {
		productionPlanNameTextbox.setDisabled(true);
		productNameTextbox.setDisabled(true);
		productionPlanDatebox.setDisabled(true);
		productUnitsIntbox.setDisabled(true);

		productionOrderDetailGrid.setModel(productionOrderDetailListModel);

		if(currentProductionOrder != null) {
			if(currentProductionOrder.getId() == null) {// nueva orden de produccion
				productionOrderNumberSpinner.setValue(getNewProductionOrderNumber());
				productionOrderDatebox.setValue(new Date());
			} else {// edicion de orden de produccion
				productionOrderNumberSpinner.setValue(currentProductionOrder.getNumber());
				productionOrderDatebox.setValue(currentProductionOrder.getDate());
			}
			ProductionPlan currentProductionPlan = productionPlanService.getProductionPlan(currentProductionOrder.getIdProductionPlan());
			productionPlanNameTextbox.setText(currentProductionPlan.getName());
			productionPlanDatebox.setValue(currentProductionPlan.getDate());
			productNameTextbox.setText(productService.getProduct(currentProductionOrder.getIdProduct()).getName());
			productUnitsIntbox.setValue(currentProductionOrder.getUnits());

			Integer workerId = currentProductionOrder.getIdWorker();
			if(workerId != null) {
				Worker aux = workerService.getWorker(workerId);
				workerListModel.addToSelection(aux);
				workerSelectbox.setModel(workerListModel);
			} else {
				workerSelectbox.setSelectedIndex(-1);
			}
			productionOrderFinishedDatebox.setValue(currentProductionOrder.getDateFinished());
			saveButton.setDisabled(false);
			cancelButton.setDisabled(false);
			resetButton.setDisabled(false);
		}

	}

	private Integer getNewProductionOrderNumber() {
		Integer aux = 0;
		List<ProductionOrder> list = productionOrderService.getProductionOrderList(currentProductionOrder.getIdProductionPlan());
		for(ProductionOrder each:list) {
			if(each.getNumber() != null && each.getNumber() > aux) {
				aux = each.getNumber();
			}
		}
		return aux + 1;
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		int selectedIndexWorker = workerSelectbox.getSelectedIndex();
		if(selectedIndexWorker == -1) {// no hay un empleado seleccionado
			Clients.showNotification("Debe seleccionar un Empleado", workerSelectbox);
			return;
		}

		currentProductionOrder.setNumber(productionOrderNumberSpinner.getValue());
		currentProductionOrder.setIdWorker(workerListModel.getElementAt(workerSelectbox.getSelectedIndex()).getId());
		currentProductionOrder.setDate(productionOrderDatebox.getValue());
		currentProductionOrder.setDateFinished(productionOrderFinishedDatebox.getValue());

		if(currentProductionOrder.getId() == null) {// nueva orden de produccion
			currentProductionOrder = productionOrderService.saveProductionOrder(currentProductionOrder, productionOrderDetailList);
		} else {// se edita
			currentProductionOrder = productionOrderService.updateProductionOrder(currentProductionOrder, productionOrderDetailList);
		}
		alert("Orden de Produccion Guardada.");
		cancelButtonClick();// volvemos a la lista de ordenes de produccion
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		ProductionPlan currentProductionPlan = productionPlanService.getProductionPlan(currentProductionOrder.getIdProductionPlan());
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
	public void doCreateMachineCombobox(ForwardEvent evt) {
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
