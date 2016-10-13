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
import org.zkoss.zul.Cell;
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
import ar.edu.utn.sigmaproject.domain.Worker;
import ar.edu.utn.sigmaproject.service.MachineRepository;
import ar.edu.utn.sigmaproject.service.PieceRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderDetailRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderRawMaterialRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderSupplyRepository;
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
	Datebox productionOrderRealDatebox;
	@Wire
	Datebox productionOrderRealFinishedDatebox;
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
		workerList = workerRepository.findAll();
		workerListModel = new ListModelList<>(workerList);
		workerCombobox.setModel(workerListModel);
		machineList = machineRepository.findAll();
		List<ProductionOrderStateType> productionOrderStateTypeList = productionOrderStateTypeRepository.findAll();
		productionOrderStateTypeListModel = new ListModelList<ProductionOrderStateType>(productionOrderStateTypeList);
		productionOrderStateTypeCombobox.setModel(productionOrderStateTypeListModel);
		
		productionOrderSupplyList = currentProductionOrder.getProductionOrderSupplies();
		productionOrderRawMaterialList = currentProductionOrder.getProductionOrderRawMaterials();
		productionOrderSupplyListModel = new ListModelList<ProductionOrderSupply>(productionOrderSupplyList);
		productionOrderRawMaterialListModel = new ListModelList<ProductionOrderRawMaterial>(productionOrderRawMaterialList);
		productionOrderSupplyListbox.setModel(productionOrderSupplyListModel);
		productionOrderRawMaterialListbox.setModel(productionOrderRawMaterialListModel);
		
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
		if(currentProductionOrder.getNumber() != null) {
			productionOrderNumberSpinner.setValue(currentProductionOrder.getNumber());
		} else {
			productionOrderNumberSpinner.setValue(getNewProductionOrderNumber());
		}
		productionOrderDatebox.setValue(currentProductionOrder.getDate());
		if (currentProductionOrder.getWorker() != null) {
			workerListModel.addToSelection(currentProductionOrder.getWorker());
			workerCombobox.setModel(workerListModel);
		} else {
			workerCombobox.setSelectedIndex(-1);
		}
		productionOrderFinishedDatebox.setValue(currentProductionOrder.getDateFinished());
		productionOrderRealDatebox.setValue(currentProductionOrder.getRealDate());
		productionOrderRealFinishedDatebox.setValue(currentProductionOrder.getRealDateFinished());
		productionOrderDetailListModel = new ListModelList<ProductionOrderDetail>(productionOrderDetailList);
		productionOrderDetailGrid.setModel(productionOrderDetailListModel);
		productionOrderStateTypeListModel.addToSelection(currentProductionOrder.getCurrentStateType());
		productionOrderStateTypeCombobox.setModel(productionOrderStateTypeListModel);
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		resetButton.setDisabled(false);
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
		List<ProductionOrderDetail> details = new ArrayList<>();
		for(Piece piece : currentProductionOrder.getProduct().getPieces()) {
			List<Process> auxProcessList = piece.getProcesses();
			for(Process process : auxProcessList) {
				// por cada proceso hay que crear un detalle
				Integer quantityPiece = currentProductionOrder.getUnits() * piece.getUnits();// cantidad total de la pieza
				Duration timeTotal = process.getTime().multiply(quantityPiece);// cantidad total de tiempo del proceso
				details.add(new ProductionOrderDetail(process, null, timeTotal, quantityPiece));
			}
		}
		if(details.size() != productionOrderDetailList.size()) {
			productionOrderDetailList = details;
			refreshView();
		}
	}

	@Transactional
	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		int selectedIndexWorker = workerCombobox.getSelectedIndex();
		if (selectedIndexWorker == -1) {// no hay un empleado seleccionado
			Clients.showNotification("Debe seleccionar un Empleado", workerCombobox);
			return;
		}
		if(!productionOrderStateTypeCombobox.getSelectedItem().getValue().equals(productionOrderStateTypeRepository.findFirstByName("Generada"))) {
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
		Date productionOrderRealDate = productionOrderRealDatebox.getValue();
		Date productionOrderRealDateFinished = productionOrderRealFinishedDatebox.getValue();
		currentProductionOrder.setNumber(productionOrderNumber);
		currentProductionOrder.setWorker(productionOrderWorker);
		currentProductionOrder.setDate(productionOrderDate);
		currentProductionOrder.setDateFinished(productionOrderDateFinished);
		currentProductionOrder.setRealDate(productionOrderRealDate);
		currentProductionOrder.setRealDateFinished(productionOrderRealDateFinished);
		// el estado de la orden debe cambiar automaticamente en base a las modificaciones que se le hagan y no se deberia poder cambiar manualmente excepto en el caso de la cancelacion
//		ProductionOrderStateType ProductionOrderStateType = productionOrderStateTypeCombobox.getSelectedItem().getValue();
//		ProductionOrderState productionOrderState = new ProductionOrderState(ProductionOrderStateType, new Date());
//		productionOrderState = productionOrderStateRepository.save(productionOrderState);
//		currentProductionOrder.setState(productionOrderState);
		productionOrderDetailList = productionOrderDetailRepository.save(productionOrderDetailList);
		currentProductionOrder.setDetails(productionOrderDetailList);
		
		productionOrderSupplyList = productionOrderSupplyRepository.save(productionOrderSupplyList);
		productionOrderRawMaterialList = productionOrderRawMaterialRepository.save(productionOrderRawMaterialList);
		currentProductionOrder.setProductionOrderSupplies(productionOrderSupplyList);
		currentProductionOrder.setProductionOrderRawMaterials(productionOrderRawMaterialList);
		
		currentProductionOrder = productionOrderRepository.save(currentProductionOrder);
		
		alert("Orden de Produccion Guardada.");
		//cancelButtonClick();
		refreshView();
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
		Machine machine = (Machine)element.getSelectedItem().getValue();
		data.setMachine(machine);// cargamos al objeto el valor actualizado del elemento web
		// asigna la misma maquina a todos los detalles que necesitan ese tipo de maquina
		//		for(ProductionOrderDetail each : productionOrderDetailList) {
		//			if(!data.equals(each)) {// no modifica el mismo detalle
		//				if(each.getProcess().getType().getMachineType().equals(machine.getMachineType())) {
		//					each.setMachine(machine);
		//				}
		//			}
		//		}//TODO
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
			element.setValue(quantityPiece.doubleValue());
			data.setQuantityFinished(quantityPiece);
			chkbox.setChecked(true);
		} else {
			data.setQuantityFinished(value);
			if(value.compareTo(quantityPiece) == 0) {
				chkbox.setChecked(true);
			} else {
				chkbox.setChecked(false);
			}
		}
	}
	
	protected void updateProductionOrderState() {
		// recorre todos los procesos para ver si estan todos finalizados
		boolean isFinished = true;
		for(ProductionOrderDetail each : productionOrderDetailList) {
			if(!each.isFinished()) {
				isFinished = false;
			}
		}
		if(isFinished) {
			ProductionOrderStateType productionOrderStateType = productionOrderStateTypeRepository.findFirstByName("Finalizada");
			ProductionOrderState productionOrderState = new ProductionOrderState(productionOrderStateType, new Date());
			productionOrderState = productionOrderStateRepository.save(productionOrderState);
			currentProductionOrder.setState(productionOrderState);
			currentProductionOrder = productionOrderRepository.save(currentProductionOrder);
			refreshView();
		} else {
			boolean isStarted = true;
			// si ninguno de los procesos esta finalizado
			for(ProductionOrderDetail each : productionOrderDetailList) {
				if(each.isFinished()) {
					isStarted = false;
				}
			}
			if(isStarted) {
				ProductionOrderStateType productionOrderStateType = productionOrderStateTypeRepository.findFirstByName("Generada");
				ProductionOrderState productionOrderState = new ProductionOrderState(productionOrderStateType, new Date());
				productionOrderState = productionOrderStateRepository.save(productionOrderState);
				currentProductionOrder.setState(productionOrderState);
				currentProductionOrder = productionOrderRepository.save(currentProductionOrder);
				refreshView();
			} else {
				// si alguno de los procesos esta finalizado
				ProductionOrderStateType productionOrderStateType = productionOrderStateTypeRepository.findFirstByName("Iniciada");
				if(!currentProductionOrder.getCurrentStateType().equals(productionOrderStateType)) {
					// si dejo de estar completo
					ProductionOrderState productionOrderState = new ProductionOrderState(productionOrderStateType, new Date());
					productionOrderState = productionOrderStateRepository.save(productionOrderState);
					currentProductionOrder.setState(productionOrderState);
					currentProductionOrder = productionOrderRepository.save(currentProductionOrder);
					refreshView();
				}
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
}
