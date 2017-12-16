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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.Item;
import ar.edu.utn.sigmaproject.domain.MaterialRequirement;
import ar.edu.utn.sigmaproject.domain.MaterialReserved;
import ar.edu.utn.sigmaproject.domain.MaterialType;
import ar.edu.utn.sigmaproject.domain.MaterialsOrder;
import ar.edu.utn.sigmaproject.domain.MaterialsOrderDetail;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanState;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.domain.StockMovement;
import ar.edu.utn.sigmaproject.domain.StockMovementDetail;
import ar.edu.utn.sigmaproject.domain.StockMovementType;
import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.domain.Wood;
import ar.edu.utn.sigmaproject.service.MaterialRequirementRepository;
import ar.edu.utn.sigmaproject.service.MaterialsOrderRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeRepository;
import ar.edu.utn.sigmaproject.service.StockMovementRepository;
import ar.edu.utn.sigmaproject.service.SupplyTypeRepository;
import ar.edu.utn.sigmaproject.service.WoodRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class MaterialsReceptionController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Window materialsOrderDeliveryWindow;
	@Wire
	Listbox materialsOrderDetailListbox;
	@Wire
	Intbox materialsOrderNumberIntbox;
	@Wire
	Textbox materialsOrderCreationDateTextbox;
	@Wire
	Datebox receptionDatebox;
	@Wire
	Textbox receiptNumberTextbox;
	@Wire
	Button saveButton;
	@Wire
	Button cancelButton;
	@Wire
	Button resetButton;

	// services
	@WireVariable
	private MaterialsOrderRepository materialsOrderRepository;
	@WireVariable
	private SupplyTypeRepository supplyTypeRepository;
	@WireVariable
	private WoodRepository woodRepository;
	@WireVariable
	private StockMovementRepository stockMovementRepository;
	@WireVariable
	private MaterialRequirementRepository materialRequirementRepository;
	@WireVariable
	private ProductionPlanStateTypeRepository productionPlanStateTypeRepository;
	@WireVariable
	private ProductionPlanStateRepository productionPlanStateRepository;
	@WireVariable
	private ProductionPlanRepository productionPlanRepository;

	// attributes
	private MaterialsOrder currentMaterialsOrder;

	// list
	private List<MaterialsOrderDetail> materialsOrderDetailList;

	// list models
	private ListModelList<MaterialsOrderDetail> materialsOrderDetailListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentMaterialsOrder = (MaterialsOrder) Executions.getCurrent().getArg().get("selected_materials_order");
		if(currentMaterialsOrder == null) {throw new RuntimeException("Materials Order null");}

		materialsOrderDetailList = currentMaterialsOrder.getDetails();
		// se asigna a la cantidad recibida la cantidad necesaria para que luego se pueda editar si es necesario
		for(MaterialsOrderDetail each : currentMaterialsOrder.getDetails()) {
			each.setQuantityReceived(each.getQuantity());
		}
		materialsOrderDetailListModel = new ListModelList<>(materialsOrderDetailList);
		refreshView();
	}

	private void refreshView() {
		materialsOrderDetailListbox.setModel(materialsOrderDetailListModel);
		materialsOrderNumberIntbox.setDisabled(true);
		materialsOrderNumberIntbox.setValue(currentMaterialsOrder.getNumber());
		materialsOrderCreationDateTextbox.setDisabled(true);
		Date date = currentMaterialsOrder.getDate();
		String dateString = "";
		if(date != null) {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			dateString = dateFormat.format(date);
		}
		materialsOrderCreationDateTextbox.setText(dateString);
		receptionDatebox.setValue(null);
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		materialsOrderDeliveryWindow.detach();
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(receptionDatebox.getValue() == null) {
			Clients.showNotification("Debe seleccionar una fecha de recepcion del pedido.", receptionDatebox);
			return;
		}
		if(Strings.isBlank(receiptNumberTextbox.getValue())) {
			Clients.showNotification("Debe Ingresar Numero de Comprobante.", receiptNumberTextbox);
			return;
		}
		// verifica que la cantidad recibida de al menos 1 material sea mayor a cero
		boolean emptyReception = true;
		for(MaterialsOrderDetail each : currentMaterialsOrder.getDetails()) {
			if(each.getQuantityReceived().compareTo(BigDecimal.ZERO) == 1) {
				emptyReception = false;
				break;
			}
		}
		if(emptyReception) {
			Clients.showNotification("Debe existir al menos 1 Material con cantidad recibida mayor a cero.", materialsOrderDetailListbox);
			return;
		}
		// guarda la recepcion de materiales
		Date receptionDate = receptionDatebox.getValue();
		String receiptNumber = receiptNumberTextbox.getText();
		currentMaterialsOrder.setDateReception(receptionDate);
		currentMaterialsOrder.setReceiptNumber(receiptNumber);
		currentMaterialsOrder = materialsOrderRepository.save(currentMaterialsOrder);
		// crea stock movement con las cantidades recibidas
		StockMovement stockMovementSupply = new StockMovement();
		stockMovementSupply.setSign((short) 1);// signo de ingreso a stock
		stockMovementSupply.setDate(new Date());
		stockMovementSupply.setType(StockMovementType.Supply);
		StockMovement stockMovementWood = new StockMovement();
		stockMovementWood.setSign((short) 1);// signo de ingreso a stock
		stockMovementWood.setDate(new Date());
		stockMovementWood.setType(StockMovementType.Wood);
		// modifica la cantidad en stock y se agrega los stock movement details
		for(MaterialsOrderDetail each : currentMaterialsOrder.getDetails()) {
			Item item = each.getItem();
			StockMovementDetail stockMovementDetail = new StockMovementDetail();
			stockMovementDetail.setDescription(item.getDescription());
			stockMovementDetail.setItem(item);
			stockMovementDetail.setQuantity(each.getQuantityReceived());
			if(item instanceof SupplyType) {
				SupplyType supplyType = (SupplyType) item;
				supplyType.setStock(supplyType.getStock().add(each.getQuantityReceived()));
				supplyType = supplyTypeRepository.save(supplyType);
				stockMovementDetail.setStockMovement(stockMovementSupply);
				stockMovementSupply.getDetails().add(stockMovementDetail);
			} else if (item instanceof Wood) {
				Wood wood = (Wood) item;
				wood.setStock(wood.getStock().add(each.getQuantityReceived()));
				wood = woodRepository.save(wood);
				stockMovementDetail.setStockMovement(stockMovementWood);
				stockMovementWood.getDetails().add(stockMovementDetail);
			}
		}
		// se comprueba q posean detalles por si llegaron materiales de un tipo y no otro
		if(!stockMovementSupply.getDetails().isEmpty()) {
			stockMovementRepository.save(stockMovementSupply);
		}
		if(!stockMovementWood.getDetails().isEmpty()) {
			stockMovementRepository.save(stockMovementWood);
		}
		// agrega ese stock a la reserva del plan (solo si el pedido tiene plan asignado)
		ProductionPlan productionPlan = currentMaterialsOrder.getProductionPlan();
		if(productionPlan != null) {
			for(MaterialsOrderDetail each : currentMaterialsOrder.getDetails()) {
				Item item = each.getItem();
				MaterialRequirement requirement = materialRequirementRepository.findByProductionPlanAndItem(productionPlan, item);
				MaterialReserved reserved = getMaterialReserved(requirement);
				BigDecimal quantityReservation = each.getQuantityReceived();
				if(each.getQuantityReceived().compareTo(requirement.getQuantity()) == 1) {// si la cantidad recibida es mayor a la necesaria del requerimiento significa que se debe reservar solo la necesaria
					quantityReservation = requirement.getQuantity();
				}
				if(reserved == null) {// no existe reserva, se crea la reserva y por la cantidad recibida
					if(item instanceof SupplyType) {
						reserved = new MaterialReserved(item, MaterialType.Supply, requirement, quantityReservation);
					} else if (item instanceof Wood) {
						reserved = new MaterialReserved(item, MaterialType.Wood, requirement, quantityReservation);
					}
					item.getMaterialReservedList().add(reserved);
				} else {// existe reserva, se suma la cantidad recibida a la actual
					// si la cantidad sin reservar es menos a la cantidad recibida, se suma solo la cantidad sin reservar, esto puede pasar si ingresaron materiales y se reservaron sin recibir los materiales del pedido
					BigDecimal quantityNonReserved = requirement.getQuantity().subtract(reserved.getStockReserved());
					if(quantityNonReserved.compareTo(quantityReservation) == -1) {// si quantityNonReserved es menor a quantityReservation
						quantityReservation = quantityNonReserved;
					}
					reserved.setStockReserved(reserved.getStockReserved().add(quantityReservation));
				}
				if(item instanceof SupplyType) {
					supplyTypeRepository.save((SupplyType) item);
				} else if (item instanceof Wood) {
					woodRepository.save((Wood) item);
				}
			}
		}
		if(currentMaterialsOrder.isTotallyReceived()) {
			if(productionPlan != null) {// solo se actualiza el estado si tiene plan asignado
				updateProductionPlanState(productionPlan);// actualiza el estado del plan a abastecido
			}
		} else {
			// crea otro pedido de materiales con las cantidades que aun no se recibieron en caso de que no se haya recibido completamente
			alert("Se Creara un nuevo Pedido para los Materiales Faltantes.");
			materialsOrderCreationAction(currentMaterialsOrder);			
		}
		EventQueue<Event> eq = EventQueues.lookup("Materials Reception Queue", EventQueues.DESKTOP, true);
		eq.publish(new Event("onMaterialsReception", null, null));
		alert("Recepcion de Materiales Registrada.");
		materialsOrderDeliveryWindow.detach();
	}

	@Listen("onOrderDetailsChange = #materialsOrderDetailListbox")
	public void doOrderDetailsChange(ForwardEvent evt) {
		MaterialsOrderDetail data = (MaterialsOrderDetail) evt.getData();// obtenemos el objeto pasado por parametro
		InputEvent inputEvent = (InputEvent) evt.getOrigin();
		Spinner origin = (Spinner)evt.getOrigin().getTarget();
		String inputValue = inputEvent.getValue();
		BigDecimal value = null;
		if(inputValue == null || inputValue.equals("")) {
			value = BigDecimal.ZERO;
		} else {
			origin.setValue(Integer.valueOf(inputEvent.getValue()));
			value = new BigDecimal(origin.intValue());
		}
		data.setQuantityReceived(value);
	}

	private void materialsOrderCreationAction(MaterialsOrder materialsOrder) {
		int materialsOrderNumber = getNewOrderNumber();
		Date materialsOrderDate = new Date();
		MaterialsOrder newMaterialsOrder = new MaterialsOrder(materialsOrderNumber, materialsOrderDate);
		newMaterialsOrder.setProductionPlan(materialsOrder.getProductionPlan());
		List<MaterialsOrderDetail> materialsOrderDetailList = new ArrayList<>();
		for(MaterialsOrderDetail each : currentMaterialsOrder.getDetails()) {
			Item item = each.getItem();
			BigDecimal stockToOrder = each.getQuantity().subtract(each.getQuantityReceived());
			if(stockToOrder.compareTo(BigDecimal.ZERO) != 0) {// si no es igual lo recibido con lo pedido
				MaterialsOrderDetail materialsOrderDetail = new MaterialsOrderDetail(newMaterialsOrder, item, item.getDescription(), stockToOrder);
				materialsOrderDetailList.add(materialsOrderDetail);
			}
		}
		newMaterialsOrder.getDetails().addAll(materialsOrderDetailList);
		newMaterialsOrder = materialsOrderRepository.save(newMaterialsOrder);
		alert("Se Registro el Pedido de Materiales Faltantes.");
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

	protected void updateProductionPlanState(ProductionPlan productionPlan) {
		boolean isCompleted = isAllReservationsFulfilled(productionPlan);
		ProductionPlanStateType stateTypeAbastecido = productionPlanStateTypeRepository.findFirstByName("Abastecido");
		ProductionPlanStateType stateTypeRegistrado = productionPlanStateTypeRepository.findFirstByName("Registrado");
		ProductionPlanStateType currentStateType = productionPlanStateTypeRepository.findOne(productionPlan.getCurrentStateType().getId());
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
			productionPlan.setState(productionPlanState);
			productionPlan = productionPlanRepository.save(productionPlan);
		}
	}

	private boolean isAllReservationsFulfilled(ProductionPlan productionPlan) {
		// recorre todos los requerimientos para ver si estan todos abastecidos
		for(MaterialRequirement each : productionPlan.getMaterialRequirements()) {
			BigDecimal stockReserved = BigDecimal.ZERO;
			MaterialReserved reservation = getMaterialReserved(each);
			if(reservation != null) {// se encontro reserva
				stockReserved = reservation.getStockReserved().add(each.getQuantityWithdrawn());// se suma la cantidad que se retiro para produccion
			}
			if(each.getQuantity().subtract(stockReserved).compareTo(BigDecimal.ZERO) != 0) {
				return false;
			}
		}
		return true;
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
