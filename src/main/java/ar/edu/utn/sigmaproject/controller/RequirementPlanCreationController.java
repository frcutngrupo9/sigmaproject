package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.Item;
import ar.edu.utn.sigmaproject.domain.MaterialsOrder;
import ar.edu.utn.sigmaproject.domain.MaterialsOrderDetail;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanState;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.domain.RawMaterialRequirement;
import ar.edu.utn.sigmaproject.domain.SupplyRequirement;
import ar.edu.utn.sigmaproject.domain.SupplyReserved;
import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.domain.Wood;
import ar.edu.utn.sigmaproject.domain.WoodReserved;
import ar.edu.utn.sigmaproject.service.MaterialsOrderDetailRepository;
import ar.edu.utn.sigmaproject.service.MaterialsOrderRepository;
import ar.edu.utn.sigmaproject.service.ProductRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeRepository;
import ar.edu.utn.sigmaproject.service.RawMaterialRequirementRepository;
import ar.edu.utn.sigmaproject.service.SupplyRequirementRepository;
import ar.edu.utn.sigmaproject.service.SupplyReservedRepository;
import ar.edu.utn.sigmaproject.service.SupplyTypeRepository;
import ar.edu.utn.sigmaproject.service.WoodRepository;
import ar.edu.utn.sigmaproject.service.WoodReservedRepository;
import ar.edu.utn.sigmaproject.service.WoodTypeRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class RequirementPlanCreationController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox productionPlanNameTextbox;
	@Wire
	Datebox productionPlanCreationDatebox;
	@Wire
	Textbox productionPlanStateTextbox;
	@Wire
	Listbox rawMaterialRequirementListbox;
	@Wire
	Listbox supplyRequirementListbox;
	@Wire
	Button returnButton;
	@Wire
	Button returnToProductionButton;
	@Wire
	Button allRequirementReservationButton;
	@Wire
	Button materialsOrderCreationButton;

	// services
	@WireVariable
	private ProductRepository productRepository;
	@WireVariable
	private ProductionPlanRepository productionPlanRepository;
	@WireVariable
	private ProductionPlanStateRepository productionPlanStateRepository;
	@WireVariable
	private ProductionPlanStateTypeRepository productionPlanStateTypeRepository;
	@WireVariable
	private WoodRepository woodRepository;
	@WireVariable
	private WoodTypeRepository woodTypeRepository;
	@WireVariable
	private RawMaterialRequirementRepository rawMaterialRequirementRepository;
	@WireVariable
	private SupplyRequirementRepository supplyRequirementRepository;
	@WireVariable
	private WoodReservedRepository woodReservedRepository;
	@WireVariable
	private SupplyReservedRepository supplyReservedRepository;
	@WireVariable
	private SupplyTypeRepository supplyTypeRepository;
	@WireVariable
	private MaterialsOrderRepository materialsOrderRepository;
	@WireVariable
	private MaterialsOrderDetailRepository materialsOrderDetailRepository;

	// atributes
	private ProductionPlan currentProductionPlan;
	@SuppressWarnings("rawtypes")
	private EventQueue eq;

	// list
	private List<SupplyRequirement> supplyRequirementList;
	private List<RawMaterialRequirement> rawMaterialRequirementList;

	// list models
	private ListModelList<SupplyRequirement> supplyRequirementListModel;
	private ListModelList<RawMaterialRequirement> rawMaterialRequirementListModel;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		currentProductionPlan = (ProductionPlan) Executions.getCurrent().getAttribute("selected_production_plan");
		if(currentProductionPlan == null) {throw new RuntimeException("ProductionPlan not found");}

		supplyRequirementList = currentProductionPlan.getSupplyRequirements();
		supplyRequirementListModel = new ListModelList<>(supplyRequirementList);
		rawMaterialRequirementList = currentProductionPlan.getRawMaterialRequirements();
		rawMaterialRequirementListModel = new ListModelList<>(rawMaterialRequirementList);

		// listener para cuando se modifique alguna reserva
		eq = EventQueues.lookup("Requirement Reservation Queue", EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener() {
			public void onEvent(Event event) throws Exception {
				if(event.getName().equals("onSupplyReservation")) {
					supplyRequirementListbox.setModel(supplyRequirementListModel);
				} else {
					rawMaterialRequirementListbox.setModel(rawMaterialRequirementListModel);
				}
				// actualiza el estado del plan si es que esta abastecido
				updateProductionPlanState();
			}
		});

		refreshView();
	}

	protected void updateProductionPlanState() {
		// recorre todos los requerimientos para ver si estan todos abastecidos
		boolean isCompleted = true;
		for(SupplyRequirement each : supplyRequirementList) {
			if(!isSupplyRequirementFulfilled(each)) {
				isCompleted = false;
				break;
			}
		}
		if(isCompleted) {// si no cambio el valor a false en el anterior loop for
			for(RawMaterialRequirement each : rawMaterialRequirementList) {
				if(!isRawMaterialRequirementFulfilled(each)) {
					isCompleted = false;
					break;
				}
			}
		}

		ProductionPlanStateType stateTypeAbastecido = productionPlanStateTypeRepository.findFirstByName("Abastecido");
		ProductionPlanStateType stateTypeRegistrado = productionPlanStateTypeRepository.findFirstByName("Registrado");
		ProductionPlanStateType currentStateType = productionPlanStateTypeRepository.findOne(currentProductionPlan.getCurrentStateType().getId());
		ProductionPlanState productionPlanState = null;
		if(isCompleted) {
			// solo se actualiza el estado a abastecido si el estado anterior era Registrado
			if(currentStateType.equals(stateTypeRegistrado)) {
				productionPlanState = new ProductionPlanState(stateTypeAbastecido, new Date());
			}
		} else {// si dejo de estar abastecido
			// solo se actualiza el estado a Registrado si el estado anterior era abastecido
			if(currentStateType.equals(stateTypeAbastecido)) {
				productionPlanState = new ProductionPlanState(stateTypeRegistrado, new Date());
			}
		}
		if(productionPlanState != null) {
			productionPlanState = productionPlanStateRepository.save(productionPlanState);
			currentProductionPlan.setState(productionPlanState);
			currentProductionPlan = productionPlanRepository.save(currentProductionPlan);
			productionPlanStateTextbox.setText(currentProductionPlan.getCurrentStateType().getName());
		}

	}

	private void refreshView() {
		productionPlanNameTextbox.setDisabled(true);
		productionPlanCreationDatebox.setDisabled(true);
		productionPlanStateTextbox.setDisabled(true);
		supplyRequirementListbox.setModel(supplyRequirementListModel);
		rawMaterialRequirementListbox.setModel(rawMaterialRequirementListModel);
		if(currentProductionPlan != null) {
			productionPlanNameTextbox.setText(currentProductionPlan.getName());
			productionPlanCreationDatebox.setValue(currentProductionPlan.getDateCreation());
			productionPlanStateTextbox.setText(currentProductionPlan.getCurrentStateType().getName());
		}
	}

	public BigDecimal getSupplyStockReserved(SupplyRequirement supplyRequirement) {
		// como es la verdadera relacion entre estas tres clases? un SupplyRequirement tiene un SupplyType,
		// por que un SupplyReserved tiene tambien un SupplyType? La relacion es uno a uno entre SupplyRequirement y SupplyReserved?
		// Respuesta: SupplyType tiene una lista de SupplyReserved, y cada SupplyReserved tiene un SupplyRequirement para saber para que plan se está reservando.
		// SupplyReserved no tiene SupplyType, solo lo tiene SupplyRequirement
		SupplyReserved supplyReserved = supplyReservedRepository.findBySupplyRequirement(supplyRequirement);
		if(supplyReserved == null) {
			return BigDecimal.ZERO;
		} else {
			// a la cantidad reservada hay que sumarle la cantidad que se retiro para produccion
			return supplyReserved.getStockReserved().add(supplyRequirement.getQuantityWithdrawn());
		}
	}

	public BigDecimal getSupplyStockMissing(SupplyRequirement supplyRequirement) {
		return supplyRequirement.getQuantity().subtract(getSupplyStockReserved(supplyRequirement));
	}

	public BigDecimal getRawMaterialStockReserved(RawMaterialRequirement rawMaterialRequirement) {
		WoodReserved woodReserved = woodReservedRepository.findByRawMaterialRequirement(rawMaterialRequirement);
		if(woodReserved == null) {
			return BigDecimal.ZERO;
		} else {
			// a la cantidad reservada hay que sumarle la cantidad que se retiro para produccion
			return woodReserved.getStockReserved().add(rawMaterialRequirement.getQuantityWithdrawn());
		}
	}

	public BigDecimal getRawMaterialRequirementStockMissing(RawMaterialRequirement rawMaterialRequirement) {
		return rawMaterialRequirement.getQuantity().subtract(getRawMaterialStockReserved(rawMaterialRequirement));
	}

	@Listen("onClickReservation = #rawMaterialRequirementListbox")
	public void doRawMaterialRequirementReservation(ForwardEvent evt) {
		RawMaterialRequirement data = (RawMaterialRequirement) evt.getData();// obtenemos el objeto pasado por parametro
		final HashMap<String, RawMaterialRequirement> map = new HashMap<String, RawMaterialRequirement>();
		map.put("selected_raw_material_requirement", data);
		Window window = (Window)Executions.createComponents("/raw_material_reservation.zul", null, map);
		window.doModal();
	}

	@Listen("onClickReservation = #supplyRequirementListbox")
	public void doSupplyRequirementReservation(ForwardEvent evt) {
		SupplyRequirement data = (SupplyRequirement) evt.getData();// obtenemos el objeto pasado por parametro
		final HashMap<String, SupplyRequirement> map = new HashMap<String, SupplyRequirement>();
		map.put("selected_supply_requirement", data);
		Window window = (Window)Executions.createComponents("/supply_reservation.zul", null, map);
		window.doModal();
	}

	@Listen("onClick = #returnButton")
	public void returnButtonClick() {
		Include include = (Include) Selectors.iterable(this.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/production_plan_list.zul");
	}

	@Listen("onClick = #returnToProductionButton")
	public void returnToProductionButtonClick() {
		Executions.getCurrent().setAttribute("selected_production_plan", currentProductionPlan);
		Include include = (Include) Selectors.iterable(this.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/production_order_list.zul");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick = #allRequirementReservationButton")
	public void allRequirementReservationButtonClick() {
		if(!currentProductionPlan.getCurrentStateType().getName().equalsIgnoreCase("Registrado")) {
			Clients.showNotification("Imposible reservar, el Plan ya fue Abastecido.");
			return;
		}
		
		// si no existe suficiente material disponible para realizar ninguna reserva, se informa.
		boolean insufficientStockForReservation = true;
		for(SupplyRequirement each : supplyRequirementList) {
			if(each.getSupplyType().getStockAvailable().compareTo(BigDecimal.ZERO) == 1) {
				insufficientStockForReservation = false;
			}
		}
		for(RawMaterialRequirement each : rawMaterialRequirementList) {
			if(each.getWood().getStockAvailable().compareTo(BigDecimal.ZERO) == 1) {
				insufficientStockForReservation = false;
			}
		}
		if(insufficientStockForReservation) {
			Clients.showNotification("Imposible reservar, no existe suficiente material disponible para realizar la reserva.");
			return;
		}

		// se registra una reserva por cada uno de los insumos y materias primas por la cantidad necesaria
		// si existe una reserva pero no esta reservando el maximo de stock disponible, se cambia a esa cantidad
		// en caso de que no existan suficientes materiales en stock para hacer una reserva se crea el pedido de materiales
		Messagebox.show("Se realizara la reserva de todos los materiales necesarios.", "Confirmar", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
			public void onEvent(Event evt) throws InterruptedException {
				if (evt.getName().equals("onOK")) {
					for(SupplyRequirement each : supplyRequirementList) {
						BigDecimal stockAvailable = each.getSupplyType().getStockAvailable();
						if(stockAvailable.compareTo(BigDecimal.ZERO) == 1) {// si existe disponible
							SupplyReserved currentSupplyReserved = supplyReservedRepository.findBySupplyRequirement(each);
							// si existe ya una reserva se la completa si esta incompleta
							BigDecimal quantityNecessary = each.getQuantity();
							BigDecimal quantityReservation = null;// si no hay stock suficiente se reserva lo posible
							if(currentSupplyReserved == null) {
								if(stockAvailable.compareTo(quantityNecessary) == 1) { // si el disponible es mayor
									quantityReservation = quantityNecessary;
								} else {
									quantityReservation = stockAvailable;
								}
								currentSupplyReserved = new SupplyReserved(each, quantityReservation);
								currentSupplyReserved = supplyReservedRepository.save(currentSupplyReserved);
								SupplyType supplyType = each.getSupplyType();
								supplyType.getSuppliesReserved().add(currentSupplyReserved);
								supplyTypeRepository.save(supplyType);
							} else {
								// como ya hay una reserva, esa cantidad se sustrae de la cantidad necesaria
								BigDecimal quantityNonReserved = quantityNecessary.subtract(currentSupplyReserved.getStockReserved());
								if(stockAvailable.compareTo(quantityNonReserved) == 1) { // si el disponible es mayor
									quantityReservation = currentSupplyReserved.getStockReserved().add(quantityNonReserved);
								} else {
									quantityReservation = currentSupplyReserved.getStockReserved().add(stockAvailable);
								}
								currentSupplyReserved.setStockReserved(quantityReservation);
								supplyReservedRepository.save(currentSupplyReserved);
							}
						}
					}
					for(RawMaterialRequirement each : rawMaterialRequirementList) {
						Wood currentWood = each.getWood();
						BigDecimal stockAvailable = currentWood.getStockAvailable();
						if(stockAvailable.compareTo(BigDecimal.ZERO) == 1) {// si existe disponible
							WoodReserved currentWoodReserved = woodReservedRepository.findByRawMaterialRequirement(each);
							BigDecimal quantityNecessary = each.getQuantity();
							BigDecimal quantityReservation = null;// si no hay stock suficiente se reserva lo posible
							if(currentWoodReserved == null) {
								if(stockAvailable.compareTo(quantityNecessary) == 1) { // si el disponible es mayor
									quantityReservation = quantityNecessary;
								} else {
									quantityReservation = stockAvailable;
								}
								currentWoodReserved = new WoodReserved(each, quantityReservation);
								currentWoodReserved = woodReservedRepository.save(currentWoodReserved);
								currentWood.getWoodsReserved().add(currentWoodReserved);
								woodRepository.save(currentWood);
							} else {
								// como ya hay una reserva, esa cantidad se sustrae de la cantidad necesaria
								BigDecimal quantityNonReserved = quantityNecessary.subtract(currentWoodReserved.getStockReserved());
								if(stockAvailable.compareTo(quantityNonReserved) == 1) { // si el disponible es mayor
									quantityReservation = currentWoodReserved.getStockReserved().add(quantityNonReserved);
								} else {
									quantityReservation = currentWoodReserved.getStockReserved().add(stockAvailable);
								}
								currentWoodReserved.setStockReserved(quantityReservation);
								woodReservedRepository.save(currentWoodReserved);
							}
						}
					}
					supplyRequirementListbox.setModel(supplyRequirementListModel);
					rawMaterialRequirementListbox.setModel(rawMaterialRequirementListModel);
					updateProductionPlanState();// actualiza el estado del plan si es que esta abastecido
					alert("Se Realizaron las Reservas.");
				}
			}
		});

	}

	public boolean isRawMaterialRequirementFulfilled(RawMaterialRequirement supplyRequirement) {
		// si ya se ha reservado la cantidad necesaria
		boolean value = false;
		if(getRawMaterialRequirementStockMissing(supplyRequirement).compareTo(BigDecimal.ZERO) == 0) {
			value = true;
		}
		return value;
	}

	public boolean isSupplyRequirementFulfilled(SupplyRequirement supplyRequirement) {
		// si ya se ha reservado la cantidad necesaria
		boolean value = false;
		if(getSupplyStockMissing(supplyRequirement).compareTo(BigDecimal.ZERO) == 0) {
			value = true;
		}
		return value;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick = #materialsOrderCreationButton")
	public void materialsOrderCreationButtonClick() {
		// busca todos los materiales para los que no existe suficiente stock y hace un pedido por el stock faltante de cada uno de ellos
		if(!currentProductionPlan.getCurrentStateType().getName().equalsIgnoreCase("Registrado")) {
			Clients.showNotification("Imposible Generar Pedido, el Plan ya fue Abastecido.");
			return;
		}

		// verifica que el stock sea insuficiente para abastecer el plan antes de generar el pedido de materiales
		boolean insufficientStock = false;
		for(SupplyRequirement each : supplyRequirementList) {
			BigDecimal stockMissing = getSupplyStockMissing(each);
			BigDecimal stockAvailable = each.getSupplyType().getStockAvailable();
			if(stockAvailable.compareTo(stockMissing) == -1) {// si stockAvailable es menor a stockMissing
				// no existe suficiente en stock
				insufficientStock = true;
				break;
			}
		}
		for(RawMaterialRequirement each : rawMaterialRequirementList) {
			BigDecimal stockMissing = getRawMaterialRequirementStockMissing(each);
			BigDecimal stockAvailable = each.getWood().getStockAvailable();
			if(stockAvailable.compareTo(stockMissing) == -1) {// si stockAvailable es menor a stockMissing
				// no existe suficiente en stock
				insufficientStock = true;
				break;
			}
		}
		if(insufficientStock == false) {
			Clients.showNotification("Imposible Generar Pedido, el Stock disponible es suficiente para abastecer al Plan.");
			return;
		}

		// verifica que no existan pedidos de materiales asignados al plan
		List<MaterialsOrder> materialsOrder = materialsOrderRepository.findByProductionPlan(currentProductionPlan);
		if(materialsOrder != null && !materialsOrder.isEmpty()) {
			Clients.showNotification("Imposible Generar Pedido, ya existe un pedido generado anteriormente.");
			return;
		}

		Messagebox.show("Se realizara el pedido de todos los materiales para los cuales no exista stock disponible suficiente.", "Confirmar", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
			public void onEvent(Event evt) throws InterruptedException {
				if (evt.getName().equals("onOK")) {
					materialsOrderCreationAction();
				}
			}
		});
	}

	private void materialsOrderCreationAction() {
		int materialsOrderNumber = getNewOrderNumber();
		Date materialsOrderDate = new Date();
		MaterialsOrder currentMaterialsOrder = new MaterialsOrder(materialsOrderNumber, materialsOrderDate);
		currentMaterialsOrder.setProductionPlan(currentProductionPlan);
		List<MaterialsOrderDetail> materialsOrderDetailList = new ArrayList<>();
		for(SupplyRequirement each : supplyRequirementList) {
			BigDecimal stockMissing = getSupplyStockMissing(each);
			BigDecimal stockAvailable = each.getSupplyType().getStockAvailable();
			if(stockAvailable.compareTo(stockMissing) == -1) {// si stockAvailable es menor a stockMissing
				// no existe suficiente en stock por lo tanto se crea un detalle de pedido de materiales
				Item currentItem = each.getSupplyType();
				BigDecimal stockToOrder = stockMissing.subtract(stockAvailable);// cantidad necesaria que no hay en stock, entonces es lo que se agrega al pedido de materiales
				MaterialsOrderDetail materialsOrderDetail = new MaterialsOrderDetail(currentMaterialsOrder, currentItem, currentItem.getDescription(), stockToOrder);
				materialsOrderDetailList.add(materialsOrderDetail);
			}
		}
		for(RawMaterialRequirement each : rawMaterialRequirementList) {
			BigDecimal stockMissing = getRawMaterialRequirementStockMissing(each);
			BigDecimal stockAvailable = each.getWood().getStockAvailable();
			if(stockAvailable.compareTo(stockMissing) == -1) {// si stockAvailable es menor a stockMissing
				// no existe suficiente en stock por lo tanto se crea un detalle de pedido de materiales
				Item currentItem = each.getWood();
				BigDecimal stockToOrder = stockMissing.subtract(stockAvailable);// cantidad necesaria que no hay suficiente en stock
				MaterialsOrderDetail materialsOrderDetail = new MaterialsOrderDetail(currentMaterialsOrder, currentItem, currentItem.getDescription(), stockToOrder);
				materialsOrderDetailList.add(materialsOrderDetail);
			}
		}
		currentMaterialsOrder.getDetails().addAll(materialsOrderDetailList);
		currentMaterialsOrder = materialsOrderRepository.save(currentMaterialsOrder);
		refreshView();
		alert("Se Registro el Pedido de Materiales.");
	}

	private Integer getNewOrderNumber() {
		Integer lastValue = 0;
		List<MaterialsOrder> list = materialsOrderRepository.findAll();
		for(MaterialsOrder each : list) {
			if(each.getNumber() > lastValue) {
				lastValue = each.getNumber();
			}
		}
		return lastValue + 1;
	}
}
