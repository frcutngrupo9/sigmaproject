package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanState;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.domain.RawMaterialRequirement;
import ar.edu.utn.sigmaproject.domain.RawMaterialType;
import ar.edu.utn.sigmaproject.domain.SupplyRequirement;
import ar.edu.utn.sigmaproject.domain.SupplyReserved;
import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.domain.Wood;
import ar.edu.utn.sigmaproject.domain.WoodReserved;
import ar.edu.utn.sigmaproject.domain.WoodType;
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
		ProductionPlanStateType currentStateType =productionPlanStateTypeRepository.findOne(currentProductionPlan.getCurrentStateType().getId());
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
		// TODO: como es la verdadera relacion entre estas tres clases? un SupplyRequirement tiene un SupplyType,
		// por que un SupplyReserved tiene tambien un SupplyType? La relacion es uno a uno entre SupplyRequirement y SupplyReserved?
		// Respuesta: SupplyType tiene una lista de SupplyReserved, y cada SupplyReserved tiene un SupplyRequirement para saber para que plan se está reservando.
		// SupplyReserved no tiene SupplyType, solo lo tiene SupplyRequirement
		SupplyReserved supplyReserved = supplyReservedRepository.findBySupplyRequirement(supplyRequirement);
		if(supplyReserved == null) {
			return BigDecimal.ZERO;
		} else {
			return supplyReserved.getStockReserved();
		}
	}

	public BigDecimal getSupplyStockAvailable(SupplyRequirement supplyRequirement) {
		// devuelve la resta entre el stock total y el total reservado
		SupplyType supplyType = supplyRequirement.getSupplyType();
		BigDecimal stockTotal = supplyType.getStock();
		BigDecimal stockReserved = supplyType.getStockReserved();
		return stockTotal.subtract(stockReserved);
	}

	public BigDecimal getSupplyStockMissing(SupplyRequirement supplyRequirement) {
		return supplyRequirement.getQuantity().subtract(getSupplyStockReserved(supplyRequirement));
	}

	public BigDecimal getRawMaterialTypeStock(RawMaterialType rawMaterialType) {
		List<Wood> woodList = woodRepository.findByRawMaterialType(rawMaterialType);
		BigDecimal stock = BigDecimal.ZERO;
		for(Wood each : woodList) {
			stock = stock.add(each.getStock());
		}
		return stock;
	}

	public BigDecimal getRawMaterialTypeStockAvailable(RawMaterialType rawMaterialType) {
		// devuelve la resta entre el stock total y el total reservado
		BigDecimal stockTotal = getRawMaterialTypeStock(rawMaterialType);
		BigDecimal stockReservedTotal = getRawMaterialTypeStockReserved(rawMaterialType);
		return stockTotal.subtract(stockReservedTotal);
	}

	public BigDecimal getRawMaterialTypeStockReserved(RawMaterialType rawMaterialType) {
		// busca la cantidad de reserva total de la materia prima, sin importar el tipo de madera
		List<Wood> woodList = woodRepository.findByRawMaterialType(rawMaterialType);
		BigDecimal stockReserved = BigDecimal.ZERO;
		for(Wood each : woodList) {
			for(WoodReserved eachReserved : each.getWoodsReserved()) {
				if(!eachReserved.isWithdrawn()) {
					stockReserved = stockReserved.add(eachReserved.getStockReserved());
				}
			}
		}
		return stockReserved;
	}

	public BigDecimal getRawMaterialTypeStockMissing(RawMaterialType rawMaterialType) {
		return getRawMaterialTypeStock(rawMaterialType).subtract(getRawMaterialTypeStockReserved(rawMaterialType));
	}

	public BigDecimal getRawMaterialRequirementStockReserved(RawMaterialRequirement rawMaterialRequirement) {
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

	public BigDecimal getRawMaterialRequirementStockMissing(RawMaterialRequirement rawMaterialRequirement) {
		return rawMaterialRequirement.getQuantity().subtract(getRawMaterialRequirementStockReserved(rawMaterialRequirement));
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
		// se registra una reserva por cada uno de los insumos y materias primas por la cantidad necesaria
		// TODO: en caso de que no existan suficientes materiales en stock para hacer una reserva se crea el pedido de materiales
		Messagebox.show("Se realizara la reserva de todos los materiales necesarios.", "Confirmar", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
			public void onEvent(Event evt) throws InterruptedException {
				if (evt.getName().equals("onOK")) {
					for(SupplyRequirement each : supplyRequirementList) {
						SupplyReserved currentSupplyReserved = supplyReservedRepository.findBySupplyRequirement(each);
						// si existe ya una reserva se la completa si esta incompleta
						BigDecimal quantityNecessary = each.getQuantity();
						BigDecimal stockAvailable = each.getSupplyType().getStockAvailable();
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
							if(stockAvailable.compareTo(quantityNecessary.subtract(currentSupplyReserved.getStockReserved())) == 1) { // si el disponible es mayor
								quantityReservation = quantityNecessary;
							} else {
								quantityReservation = stockAvailable;
							}
							currentSupplyReserved.setStockReserved(quantityReservation);
							supplyReservedRepository.save(currentSupplyReserved);
						}
					}
					for(RawMaterialRequirement each : rawMaterialRequirementList) {
						WoodReserved currentWoodReserved = null;
						BigDecimal quantityNecessary = each.getQuantity();
						WoodType woodTypePino = woodTypeRepository.findFirstByName("Pino");// busca seleccionar el wood que sea pino
						Wood currentWood = woodRepository.findByRawMaterialTypeAndWoodType(each.getRawMaterialType(), woodTypePino);
						for(WoodReserved eachWoodReserved : currentWood.getWoodsReserved()) {
							if(rawMaterialRequirementRepository.findOne(eachWoodReserved.getRawMaterialRequirement().getId()).equals(rawMaterialRequirementRepository.findOne(each.getId()))) {
								currentWoodReserved = eachWoodReserved;
								break;
							}
						}
						BigDecimal stockAvailable = currentWood.getStockAvailable();
						BigDecimal quantityReservation = null;// si no hay stock suficiente se reserva lo posible
						if(currentWoodReserved == null) {
							if(stockAvailable.compareTo(quantityNecessary) == 1) { // si el disponible es mayor
								quantityReservation = quantityNecessary;
							} else {
								quantityReservation = stockAvailable;
							}
							currentWoodReserved = new WoodReserved(each, quantityReservation);
							currentWoodReserved = woodReservedRepository.save(currentWoodReserved);
							if(currentWood != null) {
								currentWood.getWoodsReserved().add(currentWoodReserved);
								woodRepository.save(currentWood);
							} else {
								throw new RuntimeException("currentWood null");
							}
						} else {
							// como ya hay una reserva, esa cantidad se sustrae de la cantidad necesaria
							if(stockAvailable.compareTo(quantityNecessary.subtract(currentWoodReserved.getStockReserved())) == 1) { // si el disponible es mayor
								quantityReservation = quantityNecessary;
							} else {
								quantityReservation = stockAvailable;
							}
							currentWoodReserved.setStockReserved(quantityReservation);
							woodReservedRepository.save(currentWoodReserved);
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
		if(getRawMaterialRequirementStockMissing(supplyRequirement).doubleValue() == 0) {
			value = true;
		}
		return value;
	}

	public boolean isSupplyRequirementFulfilled(SupplyRequirement supplyRequirement) {
		// si ya se ha reservado la cantidad necesaria
		boolean value = false;
		if(getSupplyStockMissing(supplyRequirement).doubleValue() == 0) {
			value = true;
		}
		return value;
	}
}
