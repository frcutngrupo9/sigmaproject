package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
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
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Machine;
import ar.edu.utn.sigmaproject.domain.MachineType;
import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.domain.ProductionOrderState;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.domain.Worker;
import ar.edu.utn.sigmaproject.service.MachineRepository;
import ar.edu.utn.sigmaproject.service.PieceRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderDetailRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateRepository;
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
	Datebox productionPlanDatebox;
	@Wire
	Grid productionOrderDetailGrid;
	@Wire
	Spinner productionOrderNumberSpinner;
	@Wire
	Intbox productUnitsIntbox;
	@Wire
	Combobox workerCombobox;
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
	@WireVariable
	private ProductionOrderRepository productionOrderRepository;
	@WireVariable
	private ProductionOrderDetailRepository productionOrderDetailRepository;
	@WireVariable
	private ProductionOrderStateRepository productionOrderStateRepository;
	@WireVariable
	private MachineRepository machineRepository;
	@WireVariable
	private WorkerRepository workerRepository;
	@WireVariable
	private PieceRepository pieceRepository;

	// atributes
	private ProductionOrder currentProductionOrder;
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
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentProductionOrder = (ProductionOrder) Executions.getCurrent().getAttribute("selected_production_order");
		if(currentProductionOrder == null) {throw new RuntimeException("ProductionOrder not found");}
		currentProductionPlan = (ProductionPlan) Executions.getCurrent().getAttribute("selected_production_plan");
		if(currentProductionPlan == null) {throw new RuntimeException("ProductionPlan not found");}

		productionOrderDetailList = currentProductionOrder.getDetails();
		if(productionOrderDetailList.isEmpty()) {// es una nueva orden de produccion, se deben crear los detalles
			for(Piece piece : currentProductionOrder.getProduct().getPieces()) {
				List<Process> auxProcessList = piece.getProcesses();
				for(Process process : auxProcessList) {
					// por cada proceso hay que crear un detalle de orden de produccion
					Integer quantityPiece = currentProductionOrder.getUnits() * piece.getUnits();// cantidad total de la pieza
					Duration timeTotal = process.getTime().multiply(quantityPiece);// cantidad total de tiempo del proceso
					productionOrderDetailList.add(new ProductionOrderDetail(process, null, timeTotal, quantityPiece));
				}
			}

		}
		productionOrderDetailListModel = new ListModelList<ProductionOrderDetail>(productionOrderDetailList);

		workerList = workerRepository.findAll();
		workerListModel = new ListModelList<>(workerList);
		workerCombobox.setModel(workerListModel);

		machineList = machineRepository.findAll();

		List<ProductionOrderState> productionOrderStateList = productionOrderStateRepository.findAll();
		productionOrderStateListModel = new ListModelList<ProductionOrderState>(productionOrderStateList);
		productionOrderStateCombobox.setModel(productionOrderStateListModel);
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
		ProductionPlanStateType lastProductionPlanStateType = currentProductionPlan.getCurrentStateType();
		if(lastProductionPlanStateType != null) {
			productionPlanStateTypeTextbox.setText(lastProductionPlanStateType.getName());
		} else {
			productionPlanStateTypeTextbox.setText("[Sin Estado]");
		}
		productNameTextbox.setText(currentProductionOrder.getProduct().getName());
		productUnitsIntbox.setValue(currentProductionOrder.getUnits());
		productionOrderNumberSpinner.setValue(currentProductionOrder.getNumber());
		productionOrderDatebox.setValue(currentProductionOrder.getDate());
		if (currentProductionOrder.getWorker() != null) {
			workerListModel.addToSelection(currentProductionOrder.getWorker());
			workerCombobox.setModel(workerListModel);
		} else {
			workerCombobox.setSelectedIndex(-1);
		}
		productionOrderFinishedDatebox.setValue(currentProductionOrder.getDateFinished());
		productionOrderDetailGrid.setModel(productionOrderDetailListModel);
		productionOrderStateListModel.addToSelection(currentProductionOrder.getState());
		productionOrderStateCombobox.setModel(productionOrderStateListModel);
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		resetButton.setDisabled(false);
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		int selectedIndexWorker = workerCombobox.getSelectedIndex();
		if (selectedIndexWorker == -1) {// no hay un empleado seleccionado
			Clients.showNotification("Debe seleccionar un Empleado", workerCombobox);
			return;
		}
		if(!productionOrderStateCombobox.getSelectedItem().getValue().equals(productionOrderStateRepository.findByName("Generada"))) {
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
		}
		Integer productionOrderNumber = productionOrderNumberSpinner.getValue();
		Worker productionOrderWorker = workerCombobox.getSelectedItem().getValue();
		Date productionOrderDate = productionOrderDatebox.getValue();
		Date productionOrderDateFinished = productionOrderFinishedDatebox.getValue();
		ProductionOrderState productionOrderState = productionOrderStateCombobox.getSelectedItem().getValue();
		currentProductionOrder.setNumber(productionOrderNumber);
		currentProductionOrder.setWorker(productionOrderWorker);
		currentProductionOrder.setDate(productionOrderDate);
		currentProductionOrder.setDateFinished(productionOrderDateFinished);
		currentProductionOrder.setState(productionOrderState);
		for (ProductionOrderDetail each : productionOrderDetailList) {
			each = productionOrderDetailRepository.save(each);
		}
		currentProductionOrder.setDetails(productionOrderDetailList);
		currentProductionOrder = productionOrderRepository.save(currentProductionOrder);
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

	public String getPieceNameByProcess(Process process) {
		return pieceRepository.findByProcesses(process).getName();
	}

	public String getMachineTypeNameByProcess(Process process) {
		if(process.getType().getMachineType() != null) {
			return process.getType().getMachineType().getName();
		}
		return "";
	}

	public ListModelList<Machine> getMachineListModel(ProductionOrderDetail productionOrderDetail) {
		List<Machine> list = new ArrayList<Machine>();
		MachineType machineType = productionOrderDetail.getProcess().getType().getMachineType();
		if (machineType != null) {
			for (Machine machine : machineList) {
				if (machineType.equals(machine.getMachineType())) {
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
		for (int i = 0; i < element.getItems().size(); i++) {
			if (element.getItems().get(i) != null) {
				Comboitem item = element.getItems().get(i);
				if (item.getValue().equals(data.getMachine())) {
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
		data.setMachine((Machine)element.getSelectedItem().getValue());// cargamos al objeto el valor actualizado del elemento web
	}

	@Listen("onEditProductionOrderDetailQuantityFinished = #productionOrderDetailGrid")
	public void doEditProductionOrderDetailQuantityFinished(ForwardEvent evt) {
		ProductionOrderDetail data = (ProductionOrderDetail) evt.getData();// obtenemos el objeto pasado por parametro
		Doublebox element = (Doublebox) evt.getOrigin().getTarget();// obtenemos el elemento web
		data.setQuantityFinished(BigDecimal.valueOf(element.getValue()));// cargamos al objeto el valor actualizado del elemento web
	}

	@Listen("onEditProductionOrderDetailIsFinished = #productionOrderDetailGrid")
	public void doEditProductionOrderDetailIsFinished(ForwardEvent evt) {
		ProductionOrderDetail data = (ProductionOrderDetail) evt.getData();// obtenemos el objeto pasado por parametro
		Checkbox element = (Checkbox) evt.getOrigin().getTarget();// obtenemos el elemento web
		data.setFinished(element.isChecked());// cargamos al objeto el valor actualizado del elemento web
	}
}
