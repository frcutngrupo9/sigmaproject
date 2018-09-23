/*
 * The MIT License
 *
 * Copyright (C) 2017 SigmaProject.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
import ar.edu.utn.sigmaproject.domain.MaterialRequirement;
import ar.edu.utn.sigmaproject.domain.MaterialReserved;
import ar.edu.utn.sigmaproject.domain.MaterialsOrder;
import ar.edu.utn.sigmaproject.domain.MaterialsOrderDetail;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanState;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.domain.Wood;
import ar.edu.utn.sigmaproject.service.MaterialsOrderRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeRepository;
import ar.edu.utn.sigmaproject.service.SupplyTypeRepository;
import ar.edu.utn.sigmaproject.service.WoodRepository;

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
	private ProductionPlanRepository productionPlanRepository;
	@WireVariable
	private ProductionPlanStateRepository productionPlanStateRepository;
	@WireVariable
	private ProductionPlanStateTypeRepository productionPlanStateTypeRepository;
	@WireVariable
	private WoodRepository woodRepository;
	@WireVariable
	private SupplyTypeRepository supplyTypeRepository;
	@WireVariable
	private MaterialsOrderRepository materialsOrderRepository;

	// atributes
	private ProductionPlan currentProductionPlan;
	@SuppressWarnings("rawtypes")
	private EventQueue eq;

	// list
	private List<MaterialRequirement> supplyRequirementList;
	private List<MaterialRequirement> rawMaterialRequirementList;

	// list models
	private ListModelList<MaterialRequirement> supplyRequirementListModel;
	private ListModelList<MaterialRequirement> rawMaterialRequirementListModel;

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
		createRequirementReservationListener();
		refreshView();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createRequirementReservationListener() {
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
	}

	private void updateProductionPlanState() {
		// recorre todos los requerimientos para ver si estan todos abastecidos
		boolean isCompleted = true;
		for(MaterialRequirement each : currentProductionPlan.getMaterialRequirements()) {
			if(!isMaterialRequirementFulfilled(each)) {
				isCompleted = false;
				break;
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

	public BigDecimal getMaterialStockReserved(MaterialRequirement materialRequirement) {
		MaterialReserved materialReserved = getMaterialReserved(materialRequirement);
		if(materialReserved == null) {
			return BigDecimal.ZERO;
		} else {
			// a la cantidad reservada hay que sumarle la cantidad que se retiro para produccion
			return materialReserved.getStockReserved().add(materialRequirement.getQuantityWithdrawn());
		}
	}

	public BigDecimal getMaterialStockMissing(MaterialRequirement materialRequirement) {
		return materialRequirement.getQuantity().subtract(getMaterialStockReserved(materialRequirement));
	}

	@Listen("onClickReservation = #rawMaterialRequirementListbox")
	public void doRawMaterialRequirementReservation(ForwardEvent evt) {
		MaterialRequirement data = (MaterialRequirement) evt.getData();// obtenemos el objeto pasado por parametro
		final HashMap<String, MaterialRequirement> map = new HashMap<String, MaterialRequirement>();
		map.put("selected_raw_material_requirement", data);
		Window window = (Window)Executions.createComponents("/raw_material_reservation.zul", null, map);
		window.doModal();
	}

	@Listen("onClickReservation = #supplyRequirementListbox")
	public void doSupplyRequirementReservation(ForwardEvent evt) {
		MaterialRequirement data = (MaterialRequirement) evt.getData();// obtenemos el objeto pasado por parametro
		final HashMap<String, MaterialRequirement> map = new HashMap<String, MaterialRequirement>();
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
		if(insufficientStockForReservation()) {
			Clients.showNotification("Imposible reservar, no existe suficiente material disponible para realizar la reserva.");
			return;
		}
		if(isAllRequirementReservationDone()) {
			Clients.showNotification("Imposible reservar, ya se realizaron las reservas posibles.");
			return;
		}
		// se registra una reserva por cada uno de los insumos y materias primas por la cantidad necesaria
		// si existe una reserva pero no esta reservando el maximo de stock disponible, se cambia a esa cantidad
		// en caso de que no existan suficientes materiales en stock para hacer una reserva se crea el pedido de materiales
		Messagebox.show("Se realizara la reserva de todos los materiales necesarios.", "Confirmar", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
			public void onEvent(Event evt) throws InterruptedException {
				if (evt.getName().equals("onOK")) {
					doAllRequirementReservation();
					alert("Se Realizaron las Reservas.");
				}
			}
		});
	}

	private void doAllRequirementReservation() {
		for(MaterialRequirement each : currentProductionPlan.getMaterialRequirements()) {
			Item item = each.getItem();
			BigDecimal stockAvailable = BigDecimal.ZERO;
			if(item instanceof SupplyType) {
				SupplyType supplyType = (SupplyType) each.getItem();
				stockAvailable = supplyType.getStockAvailable();
			} else if (item instanceof Wood) {
				Wood wood = (Wood) each.getItem();
				stockAvailable = wood.getStockAvailable();
			}
			if(stockAvailable.compareTo(BigDecimal.ZERO) == 1) {// si existe disponible
				MaterialReserved currentMaterialReserved = getMaterialReserved(each);
				// si existe ya una reserva se la completa si esta incompleta
				BigDecimal quantityNecessary = each.getQuantity();
				BigDecimal quantityReservation = null;// si no hay stock suficiente se reserva lo posible
				if(currentMaterialReserved == null) {
					if(stockAvailable.compareTo(quantityNecessary) == 1) { // si el disponible es mayor
						quantityReservation = quantityNecessary;
					} else {
						quantityReservation = stockAvailable;
					}
					if(item instanceof SupplyType) {
						currentMaterialReserved = new MaterialReserved(item, each, quantityReservation);
						SupplyType supplyType = (SupplyType) each.getItem();
						supplyType.getSuppliesReserved().add(currentMaterialReserved);
					} else if (item instanceof Wood) {
						currentMaterialReserved = new MaterialReserved(item, each, quantityReservation);
						Wood wood = (Wood) each.getItem();
						wood.getWoodsReserved().add(currentMaterialReserved);
					}
				} else {
					// como ya hay una reserva, esa cantidad se sustrae de la cantidad necesaria
					BigDecimal quantityNonReserved = quantityNecessary.subtract(currentMaterialReserved.getStockReserved());
					if(stockAvailable.compareTo(quantityNonReserved) == 1) { // si el disponible es mayor
						quantityReservation = currentMaterialReserved.getStockReserved().add(quantityNonReserved);
					} else {
						quantityReservation = currentMaterialReserved.getStockReserved().add(stockAvailable);
					}
					currentMaterialReserved.setStockReserved(quantityReservation);
				}
				if(item instanceof SupplyType) {
					SupplyType supplyType = (SupplyType) each.getItem();
					supplyType = supplyTypeRepository.save(supplyType);
				} else if (item instanceof Wood) {
					Wood wood = (Wood) each.getItem();
					wood = woodRepository.save(wood);
				}
			}
		}
		currentProductionPlan = productionPlanRepository.findOne(currentProductionPlan.getId());// buscamos el plan nuevamente para que contenga en sus atributos los cambios recientes
		supplyRequirementList = currentProductionPlan.getSupplyRequirements();
		rawMaterialRequirementList = currentProductionPlan.getRawMaterialRequirements();
		supplyRequirementListModel = new ListModelList<>(supplyRequirementList);
		rawMaterialRequirementListModel = new ListModelList<>(rawMaterialRequirementList);
		supplyRequirementListbox.setModel(supplyRequirementListModel);
		rawMaterialRequirementListbox.setModel(rawMaterialRequirementListModel);
		updateProductionPlanState();// actualiza el estado del plan
	}

	private boolean insufficientStockForReservation() {
		// si no existe nada de stock disponible
		for(MaterialRequirement each : currentProductionPlan.getMaterialRequirements()) {
			Item item = each.getItem();
			if(item instanceof SupplyType) {
				SupplyType supplyType = (SupplyType) each.getItem();
				if(supplyType.getStockAvailable().compareTo(BigDecimal.ZERO) == 1) {
					return false;
				}
			} else if (item instanceof Wood) {
				Wood wood = (Wood) each.getItem();
				if(wood.getStockAvailable().compareTo(BigDecimal.ZERO) == 1) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean isMaterialRequirementFulfilled(MaterialRequirement materialRequirement) {
		// si ya se ha reservado la cantidad necesaria
		return getMaterialStockMissing(materialRequirement).compareTo(BigDecimal.ZERO) == 0;
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
		for(MaterialRequirement each : currentProductionPlan.getMaterialRequirements()) {
			BigDecimal stockMissing = getMaterialStockMissing(each);
			Item item = each.getItem();
			if(item instanceof SupplyType) {
				SupplyType supplyType = (SupplyType) each.getItem();
				BigDecimal stockAvailable = supplyType.getStockAvailable();
				if(stockAvailable.compareTo(stockMissing) == -1) {// si stockAvailable es menor a stockMissing
					// no existe suficiente en stock
					insufficientStock = true;
					break;
				}
			} else if (item instanceof Wood) {
				Wood wood = (Wood) each.getItem();
				BigDecimal stockAvailable = wood.getStockAvailable();
				if(stockAvailable.compareTo(stockMissing) == -1) {// si stockAvailable es menor a stockMissing
					// no existe suficiente en stock
					insufficientStock = true;
					break;
				}
			}
		}
		if(insufficientStock == false) {
			Clients.showNotification("Imposible Generar Pedido, el Stock disponible es suficiente para abastecer al Plan.");
			return;
		}
		// antes de hacer los pedidos realiza la reserva de los materiales disponibles si no se realizo aun
		if(!isAllRequirementReservationDone()) {
			Clients.showNotification("Debe realizar las reservas antes de crear el pedido de materiales.");
			return;
		}
		// verifica que no existan pedidos de materiales asignados al plan
		List<MaterialsOrder> materialsOrder = materialsOrderRepository.findByProductionPlan(currentProductionPlan);
		if(materialsOrder != null && !materialsOrder.isEmpty()) {
			//TODO verifica que los pedidos existentes sean suficientes para satisfacer la necesidad del plan, ya que existe la posibilidad que el stock haya disminuido antes de que se realizaran las reservas de la cantidad disponible
			// si existen pedidos pero la cantidad de esos pedidos no es suficiente para satisfacer el stock faltante, se modifica la cantidad del ultimo pedido no recibido.
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

	private boolean isAllRequirementReservationDone() {
		// verifica si ya se ha reservado la cantidad disponible de todos los materiales
		boolean value = true;
		for(MaterialRequirement each : currentProductionPlan.getMaterialRequirements()) {
			Item item = each.getItem();
			BigDecimal stockAvailable = BigDecimal.ZERO;
			if(item instanceof SupplyType) {
				SupplyType supplyType = (SupplyType) each.getItem();
				stockAvailable = supplyType.getStockAvailable();
			} else if (item instanceof Wood) {
				Wood wood = (Wood) each.getItem();
				stockAvailable = wood.getStockAvailable();
			}
			if(stockAvailable.compareTo(BigDecimal.ZERO) == 1) {// si existe disponible
				MaterialReserved currentMaterialReserved = getMaterialReserved(each);
				BigDecimal quantityNecessary = each.getQuantity();
				if(currentMaterialReserved == null) {
					// si no hay reserva y existe disponible devolver false
					return false;
				} else {
					// como ya hay una reserva, se verifica que exista cantidad no reservada
					BigDecimal quantityNonReserved = quantityNecessary.subtract(currentMaterialReserved.getStockReserved());
					if(quantityNonReserved.compareTo(BigDecimal.ZERO) != 0) { // si existe cantidad no reservada
						return false;
					}
				}
			}
		}
		return value;
	}

	private void materialsOrderCreationAction() {
		int materialsOrderNumber = getNewOrderNumber();
		Date materialsOrderDate = new Date();
		MaterialsOrder currentMaterialsOrder = new MaterialsOrder(materialsOrderNumber, materialsOrderDate);
		currentMaterialsOrder.setProductionPlan(currentProductionPlan);
		List<MaterialsOrderDetail> materialsOrderDetailList = new ArrayList<>();
		for(MaterialRequirement each : currentProductionPlan.getMaterialRequirements()) {
			Item item = each.getItem();
			BigDecimal stockAvailable = BigDecimal.ZERO;
			if(item instanceof SupplyType) {
				SupplyType supplyType = (SupplyType) each.getItem();
				stockAvailable = supplyType.getStockAvailable();
			} else if (item instanceof Wood) {
				Wood wood = (Wood) each.getItem();
				stockAvailable = wood.getStockAvailable();
			}
			BigDecimal stockMissing = getMaterialStockMissing(each);
			if(stockAvailable.compareTo(stockMissing) == -1) {// si stockAvailable es menor a stockMissing
				// no existe suficiente en stock por lo tanto se crea un detalle de pedido de materiales
				BigDecimal stockToOrder = stockMissing.subtract(stockAvailable);// cantidad necesaria que no hay en stock, entonces es lo que se agrega al pedido de materiales
				MaterialsOrderDetail materialsOrderDetail = new MaterialsOrderDetail(currentMaterialsOrder, item, item.getDescription(), stockToOrder);
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

	private MaterialReserved getMaterialReserved(MaterialRequirement materialRequirement) {
		for(MaterialReserved each: materialRequirement.getItem().getMaterialReservedList()) {
			if(productionPlanRepository.findOne(each.getMaterialRequirement().getProductionPlan().getId()).equals(productionPlanRepository.findOne(materialRequirement.getProductionPlan().getId()))) {
				return each;
			}
		}
		return null;
	}
}