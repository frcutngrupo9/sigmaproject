package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.Duration;

import org.springframework.transaction.annotation.Transactional;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
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
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Machine;
import ar.edu.utn.sigmaproject.domain.MachineType;
import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderState;
import ar.edu.utn.sigmaproject.domain.OrderStateType;
import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.domain.ProductionOrderRawMaterial;
import ar.edu.utn.sigmaproject.domain.ProductionOrderState;
import ar.edu.utn.sigmaproject.domain.ProductionOrderStateType;
import ar.edu.utn.sigmaproject.domain.ProductionOrderSupply;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanDetail;
import ar.edu.utn.sigmaproject.domain.ProductionPlanState;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.domain.RawMaterial;
import ar.edu.utn.sigmaproject.domain.RawMaterialRequirement;
import ar.edu.utn.sigmaproject.domain.Supply;
import ar.edu.utn.sigmaproject.domain.SupplyRequirement;
import ar.edu.utn.sigmaproject.domain.SupplyReserved;
import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.domain.Wood;
import ar.edu.utn.sigmaproject.domain.WoodReserved;
import ar.edu.utn.sigmaproject.service.MachineRepository;
import ar.edu.utn.sigmaproject.service.MachineTypeRepository;
import ar.edu.utn.sigmaproject.service.OrderRepository;
import ar.edu.utn.sigmaproject.service.OrderStateRepository;
import ar.edu.utn.sigmaproject.service.OrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.PieceRepository;
import ar.edu.utn.sigmaproject.service.ProductRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderDetailRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderRawMaterialRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderSupplyRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeRepository;
import ar.edu.utn.sigmaproject.service.RawMaterialRequirementRepository;
import ar.edu.utn.sigmaproject.service.SupplyRequirementRepository;
import ar.edu.utn.sigmaproject.service.SupplyReservedRepository;
import ar.edu.utn.sigmaproject.service.SupplyTypeRepository;
import ar.edu.utn.sigmaproject.service.WoodRepository;
import ar.edu.utn.sigmaproject.service.WoodReservedRepository;
import ar.edu.utn.sigmaproject.service.WorkerRepository;


@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProductionFollowUpController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox productionPlanNameTextbox;
	@Wire
	Textbox productionPlanStateTypeTextbox;
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
	Button generateDetailsButton;
	@Wire
	Listbox productionOrderSupplyListbox;
	@Wire
	Listbox productionOrderRawMaterialListbox;
	@Wire
	Button materialsWithdrawalButton;

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
	private WoodRepository woodRepository;
	@WireVariable
	private RawMaterialRequirementRepository rawMaterialRequirementRepository;
	@WireVariable
	private SupplyReservedRepository supplyReservedRepository;
	@WireVariable
	private SupplyTypeRepository supplyTypeRepository;
	@WireVariable
	private SupplyRequirementRepository supplyRequirementRepository;
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
	@WireVariable
	private ProductRepository productRepository;

	// atributes
	private ProductionOrder currentProductionOrder;
	private ProductionPlan currentProductionPlan;

	// list
	private List<ProductionOrderDetail> productionOrderDetailList;
	private List<Machine> machineList;
	private List<ProductionOrderSupply> productionOrderSupplyList;
	private List<ProductionOrderRawMaterial> productionOrderRawMaterialList;

	// list models
	private ListModelList<ProductionOrderDetail> productionOrderDetailListModel;
	private ListModelList<ProductionOrderSupply> productionOrderSupplyListModel;
	private ListModelList<ProductionOrderRawMaterial> productionOrderRawMaterialListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentProductionOrder = (ProductionOrder) Executions.getCurrent().getAttribute("selected_production_order");
		if(currentProductionOrder == null) {throw new RuntimeException("ProductionOrder not found");}
		currentProductionPlan = currentProductionOrder.getProductionPlan();
		productionOrderDetailList = currentProductionOrder.getDetails();

		machineList = machineRepository.findAll();
		refreshView();
	}

	private void refreshView() {
		productionOrderStartDatebox.setDisabled(true);
		productionOrderFinishDatebox.setDisabled(true);
		productionOrderRealStartDatebox.setDisabled(true);
		productionOrderRealFinishDatebox.setDisabled(true);
		productionPlanNameTextbox.setDisabled(true);
		productNameTextbox.setDisabled(true);
		productUnitsIntbox.setDisabled(true);
		productionPlanStateTypeTextbox.setDisabled(true);
		productionPlanNameTextbox.setText(currentProductionPlan.getName());
		ProductionPlanStateType lastProductionPlanStateType = currentProductionPlan.getCurrentStateType();
		if(lastProductionPlanStateType != null) {
			productionPlanStateTypeTextbox.setText(lastProductionPlanStateType.getName());
		} else {
			productionPlanStateTypeTextbox.setText("[Sin Estado]");
		}
		productNameTextbox.setText(currentProductionOrder.getProduct().getName());
		productUnitsIntbox.setValue(currentProductionOrder.getUnits());
		productionOrderStartDatebox.setValue(currentProductionOrder.getDateStart());
		productionOrderFinishDatebox.setValue(currentProductionOrder.getDateFinish());
		productionOrderRealStartDatebox.setValue(currentProductionOrder.getDateStartReal());
		productionOrderRealFinishDatebox.setValue(currentProductionOrder.getDateFinishReal());
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
		productionOrderSupplyList = currentProductionOrder.getProductionOrderSupplies();
		productionOrderRawMaterialList = currentProductionOrder.getProductionOrderRawMaterials();
		productionOrderSupplyListModel = new ListModelList<ProductionOrderSupply>(productionOrderSupplyList);
		productionOrderRawMaterialListModel = new ListModelList<ProductionOrderRawMaterial>(productionOrderRawMaterialList);
		productionOrderSupplyListbox.setModel(productionOrderSupplyListModel);
		productionOrderRawMaterialListbox.setModel(productionOrderRawMaterialListModel);
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

		//Date productionOrderRealDateStart = productionOrderRealStartDatebox.getValue();
		//Date productionOrderRealDateFinish = productionOrderRealFinishDatebox.getValue();
		//currentProductionOrder.setDateStartReal(productionOrderRealDateStart);
		//currentProductionOrder.setDateFinishReal(productionOrderRealDateFinish);
		productionOrderDetailList = productionOrderDetailRepository.save(productionOrderDetailList);
		currentProductionOrder.setDetails(productionOrderDetailList);

		productionOrderSupplyList = productionOrderSupplyRepository.save(productionOrderSupplyList);
		productionOrderRawMaterialList = productionOrderRawMaterialRepository.save(productionOrderRawMaterialList);
		currentProductionOrder.setProductionOrderSupplies(productionOrderSupplyList);
		currentProductionOrder.setProductionOrderRawMaterials(productionOrderRawMaterialList);

		// el estado de la orden debe cambiar automaticamente a 3 estados: Preparada, Iniciada, Finalizada
		ProductionOrderStateType newStateType = getProductionOrderStateType();
		ProductionOrderStateType productionOrderStateType = null;
		if(!lastStateType.equals(newStateType)) {// si el estado anterior es distindo del nuevo
			if(newStateType.equals(productionOrderStateTypeRepository.findFirstByName("Preparada"))) {
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

		alert("Avance de Produccion Guardada.");
		//cancelButtonClick();
		refreshView();
	}

	private void updateProductionPlanState() {
		// el estado del plan debe cambiar automaticamente a 3 estados: Lanzado, En Ejecucion, Finalizado
		// recorre todas las ordenes del plan y comprueba
		// si no inicio ninguna, se guarda el estado "Lanzado"
		// si inicio 1 o mas pero no todas, se guarda estado "En Ejecucion"
		// si finalizaron todas, se guarda estado "Finalizado"
		ProductionPlanStateType currentProductionPlanStateType = productionPlanStateTypeRepository.findOne(currentProductionPlan.getCurrentStateType().getId());
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
		} else if(allNotStarted) {// ninguno iniciado
			newProductionPlanStateType = productionPlanStateTypeRepository.findFirstByName("Lanzado");
			orderStateType = orderStateTypeRepository.findFirstByName("Planificado");
		} else {
			newProductionPlanStateType = productionPlanStateTypeRepository.findFirstByName("En Ejecucion");
			orderStateType = orderStateTypeRepository.findFirstByName("En Produccion");
		}

		//si el estado previo es igual al nuevo no se realiza el guardado
		if(newProductionPlanStateType!=null && !currentProductionPlanStateType.equals(newProductionPlanStateType)) {
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

			//TODO: si el estado es En Ejecucion se restan los materiales de stock

			// se modifica la cantidad en stock si el estado es Finalizado
			if(productionPlanStateTypeRepository.findFirstByName("Finalizado").equals(newProductionPlanStateType)) {
				for(ProductionOrder each : productionOrderList) {
					Product product = each.getProduct();
					// TODO: sumar las cantidades REALES que se produjeron
					product.setStock(product.getStock() + each.getUnits());
					product = productRepository.save(product);
				}
			}
		}

	}


	private ProductionOrderStateType getProductionOrderStateType() {
		// devuelve el estado actual de la orden de produccion
		// "Preparada" si no inicio, "Iniciada" o "Finalizada" si finalizaron todos los detalles
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
			productionOrderStateType = productionOrderStateTypeRepository.findFirstByName("Preparada");
		} else {// si no esta Finalizada ni Preparada, esta Iniciada
			productionOrderStateType = productionOrderStateTypeRepository.findFirstByName("Iniciada");
		}
		return productionOrderStateType;
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		Include include = (Include) Selectors.iterable(productionOrderDetailGrid.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/production_follow_up_list.zul");
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
		Checkbox chkbox = (Checkbox)fila.getChildren().get(0);
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
		data.setQuantityUsed(value);
	}

	@Listen("onEditUsedSupplyObservation = #productionOrderSupplyListbox")
	public void doEditUsedSupplyObservation(ForwardEvent evt) {
		ProductionOrderSupply data = (ProductionOrderSupply) evt.getData();// obtenemos el objeto pasado por parametro
		Textbox origin = (Textbox)evt.getOrigin().getTarget();
		InputEvent inputEvent = (InputEvent) evt.getOrigin();
		origin.setValue(inputEvent.getValue());
		data.setObservation(origin.getText());
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
		data.setQuantityUsed(value);
	}

	@Listen("onEditUsedRawMaterialObservation = #productionOrderRawMaterialListbox")
	public void doEditUsedRawMaterialObservation(ForwardEvent evt) {
		ProductionOrderRawMaterial data = (ProductionOrderRawMaterial) evt.getData();// obtenemos el objeto pasado por parametro
		Textbox origin = (Textbox)evt.getOrigin().getTarget();
		InputEvent inputEvent = (InputEvent) evt.getOrigin();
		origin.setValue(inputEvent.getValue());
		data.setObservation(origin.getText());
	}

	@Listen("onCompleteUsedSupply = #productionOrderSupplyListbox")
	public void doCompleteUsedSupply(ForwardEvent evt) {
		ProductionOrderSupply data = (ProductionOrderSupply) evt.getData();// obtenemos el objeto pasado por parametro
		Button element = (Button) evt.getOrigin().getTarget();
		Listcell listcell = (Listcell)element.getParent();
		Doublebox doublebox = (Doublebox)listcell.getChildren().get(listcell.getChildren().size()-2);
		doublebox.setValue(data.getQuantity().doubleValue());
		data.setQuantityUsed(data.getQuantity());
	}

	@Listen("onCompleteUsedRawMaterial = #productionOrderRawMaterialListbox")
	public void doCompleteUsedRawMaterial(ForwardEvent evt) {
		ProductionOrderRawMaterial data = (ProductionOrderRawMaterial) evt.getData();// obtenemos el objeto pasado por parametro
		Button element = (Button) evt.getOrigin().getTarget();
		Listcell listcell = (Listcell)element.getParent();
		Doublebox doublebox = (Doublebox)listcell.getChildren().get(listcell.getChildren().size()-2);
		doublebox.setValue(data.getQuantity().doubleValue());
		data.setQuantityUsed(data.getQuantity());
	}

	@Listen("onChange = #productionOrderStartDatebox")
	public void productionOrderStartDateboxOnChange() {
		//Date finishDate = getFinishDate(productionOrderStartDatebox.getValue(), currentProductionOrder.getDurationTotal());
		//productionOrderFinishDatebox.setValue(finishDate);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick = #materialsWithdrawalButton")
	public void materialsWithdrawalButtonClick() {
		// si ya se retiraron los materiales mostrar mensaje
		if(currentProductionOrder.getDateMaterialsWithdrawal() != null) {
			Clients.showNotification("Imposible Retirar Materiales, los Materiales ya se han retirado anteriormente.");
			return;
		}
		Messagebox.show("Se realizara el retiro de todos los materiales reservados para la orden produccion.", "Confirmar", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
			public void onEvent(Event evt) throws InterruptedException {
				if (evt.getName().equals("onOK")) {
					materialsWithdrawalAction();
				}
			}
		});
	}

	private void materialsWithdrawalAction() {
		// buscamos los materiales necesarios para esta orden y luego vemos cuales son las reservas para dichos materiales y disminuimos su cantidad tanto en la reserva como en el stock
		Product product = currentProductionOrder.getProduct();
		Integer units = currentProductionOrder.getUnits();
		// la cantidad a restar es la cantidad de materiales del producto por la cantidad de unidades del producto
		List<Supply> supplyList = product.getSupplies();
		List<RawMaterial> woodList = product.getRawMaterials();
		for(Supply each : supplyList) {
			BigDecimal quantityTotal = each.getQuantity().multiply(new BigDecimal(units));
			SupplyType supplyType = each.getSupplyType();
			supplyType.setStock(supplyType.getStock().subtract(quantityTotal));
			supplyType = supplyTypeRepository.save(supplyType);
			// se busca la reserva del material y se resta la cantidad, y se suma esa cantidad a la cantidad retirada del requirement
			SupplyRequirement supplyRequirement = null;
			for(SupplyRequirement req : currentProductionPlan.getSupplyRequirements()) {
				if(supplyTypeRepository.findOne(req.getSupplyType().getId()).equals(supplyTypeRepository.findOne(supplyType.getId()))) {
					supplyRequirement = req;
					break;
				}
			}
			SupplyReserved supplyReserved = supplyReservedRepository.findBySupplyRequirement(supplyRequirement);
			supplyReserved.setStockReserved(supplyReserved.getStockReserved().subtract(quantityTotal));
			supplyReservedRepository.save(supplyReserved);
			supplyRequirement.setQuantityWithdrawn(supplyRequirement.getQuantityWithdrawn().add(quantityTotal));
			supplyRequirementRepository.save(supplyRequirement);
		}
		for(RawMaterial each : woodList) {
			BigDecimal quantityTotal = each.getQuantity().multiply(new BigDecimal(units));
			Wood wood = each.getWood();
			wood.setStock(wood.getStock().subtract(quantityTotal));
			wood = woodRepository.save(wood);
			// se busca la reserva del material y se resta la cantidad, y se suma esa cantidad a la cantidad retirada del requirement
			RawMaterialRequirement rawMaterialRequirement = null;
			for(RawMaterialRequirement req : currentProductionPlan.getRawMaterialRequirements()) {
				if(woodRepository.findOne(req.getWood().getId()).equals(woodRepository.findOne(wood.getId()))) {
					rawMaterialRequirement = req;
					break;
				}
			}
			WoodReserved woodReserved = woodReservedRepository.findByRawMaterialRequirement(rawMaterialRequirement);
			woodReserved.setStockReserved(woodReserved.getStockReserved().subtract(quantityTotal));
			woodReservedRepository.save(woodReserved);
			rawMaterialRequirement.setQuantityWithdrawn(rawMaterialRequirement.getQuantityWithdrawn().add(quantityTotal));
			rawMaterialRequirementRepository.save(rawMaterialRequirement);
		}
		currentProductionOrder.setDateMaterialsWithdrawal(new Date());
		currentProductionOrder = productionOrderRepository.save(currentProductionOrder);
		alert("Retiro de Materiales Registrado.");
	}
}
