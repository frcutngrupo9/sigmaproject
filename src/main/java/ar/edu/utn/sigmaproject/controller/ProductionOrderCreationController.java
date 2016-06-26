package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.Duration;

import ar.edu.utn.sigmaproject.domain.*;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.service.MachineRepository;
import ar.edu.utn.sigmaproject.service.PieceRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateRepository;
import ar.edu.utn.sigmaproject.service.WorkerRepository;
import ar.edu.utn.sigmaproject.util.RepositoryHelper;

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
import org.zkoss.zul.Selectbox;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;

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
	@WireVariable
	private ProductionOrderRepository productionOrderRepository;
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
	private Product currentProduct;
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

		currentProduct = (Product) Executions.getCurrent().getAttribute("selected_product");
		currentProductionPlan = (ProductionPlan) Executions.getCurrent().getAttribute("selected_production_plan");
		currentProductionOrder = productionOrderRepository.findByProductionPlanAndProduct(currentProductionPlan, currentProduct);

		if(currentProductionOrder == null) {// es una nueva orden de produccion, se deben crear los detalles
			productionOrderDetailList = new ArrayList<>();
			for(Piece piece : currentProduct.getPieces()) {
				List<Process> auxProcessList = piece.getProcesses();
				for(Process process : auxProcessList) {
					// por cada proceso hay que crear un detalle de orden de produccion
					Integer quantityPiece = currentProductionPlan.getProductTotal(currentProduct).getTotalUnits() * piece.getUnits();// cantidad total de la pieza
					Duration timeTotal = process.getTime().multiply(quantityPiece);// cantidad total de tiempo del proceso
					productionOrderDetailList.add(new ProductionOrderDetail(process, null, timeTotal, quantityPiece));
				}
			}

		} else {// es una orden de produccion ya creada, se buscan sus detalles
			productionOrderDetailList = currentProductionOrder.getDetails();
		}
		productionOrderDetailListModel = new ListModelList<ProductionOrderDetail>(productionOrderDetailList);

		workerList = workerRepository.findAll();
		workerListModel = new ListModelList<>(workerList);
		workerSelectbox.setModel(workerListModel);

		machineList = machineRepository.findAll();

		List<ProductionOrderState> productionOrderStateList = productionOrderStateRepository.findAll();
		productionOrderStateListModel = new ListModelList<ProductionOrderState>(productionOrderStateList);
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
		productUnitsIntbox.setValue(currentProductionPlan.getProductTotal(currentProduct).getTotalUnits());
		ProductionPlanStateType lastProductionPlanStateType = currentProductionPlan.getCurrentStateType();
		if(lastProductionPlanStateType != null) {
			productionPlanStateTypeTextbox.setText(lastProductionPlanStateType.getName().toUpperCase());
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
			productionOrderStateCombobox.setSelectedIndex(-1);
		} else {// edicion de orden de produccion
			productionOrderNumberSpinner.setValue(currentProductionOrder.getNumber());
			productionOrderDatebox.setValue(currentProductionOrder.getDate());
			if (currentProductionOrder.getWorker() != null) {
				workerListModel.addToSelection(currentProductionOrder.getWorker());
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
		if (selectedIndexWorker == -1) {// no hay un empleado seleccionado
			Clients.showNotification("Debe seleccionar un Empleado", workerSelectbox);
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
		Worker productionOrderWorker = workerListModel.getElementAt(workerSelectbox.getSelectedIndex());
		Date productionOrderDate = productionOrderDatebox.getValue();
		Date productionOrderDateFinished = productionOrderFinishedDatebox.getValue();
		ProductionOrderState productionOrderState = productionOrderStateCombobox.getSelectedItem().getValue();

		if (currentProductionOrder == null) {// nueva orden de produccion
			currentProductionOrder = new ProductionOrder(currentProductionPlan, currentProduct, productionOrderWorker, productionOrderNumber, currentProductionPlan.getProductTotal(currentProduct).getTotalUnits(), productionOrderDate, productionOrderDateFinished, productionOrderState);
		} else {// se edita
			currentProductionOrder.setNumber(productionOrderNumber);
			currentProductionOrder.setWorker(productionOrderWorker);
			currentProductionOrder.setDate(productionOrderDate);
			currentProductionOrder.setDateFinished(productionOrderDateFinished);
			currentProductionOrder.setState(productionOrderState);
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
