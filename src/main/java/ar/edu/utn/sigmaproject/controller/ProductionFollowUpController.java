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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
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
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.Item;
import ar.edu.utn.sigmaproject.domain.Machine;
import ar.edu.utn.sigmaproject.domain.MachineType;
import ar.edu.utn.sigmaproject.domain.MaterialRequirement;
import ar.edu.utn.sigmaproject.domain.MaterialReserved;
import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderState;
import ar.edu.utn.sigmaproject.domain.OrderStateType;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.ProcessState;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductMaterial;
import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.domain.ProductionOrderMaterial;
import ar.edu.utn.sigmaproject.domain.ProductionOrderState;
import ar.edu.utn.sigmaproject.domain.ProductionOrderStateType;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanDetail;
import ar.edu.utn.sigmaproject.domain.ProductionPlanState;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.domain.Replanning;
import ar.edu.utn.sigmaproject.domain.StockMovement;
import ar.edu.utn.sigmaproject.domain.StockMovementDetail;
import ar.edu.utn.sigmaproject.domain.StockMovementType;
import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.domain.Wood;
import ar.edu.utn.sigmaproject.domain.Worker;
import ar.edu.utn.sigmaproject.service.MachineRepository;
import ar.edu.utn.sigmaproject.service.MachineTypeRepository;
import ar.edu.utn.sigmaproject.service.MaterialRequirementRepository;
import ar.edu.utn.sigmaproject.service.MaterialReservedRepository;
import ar.edu.utn.sigmaproject.service.OrderRepository;
import ar.edu.utn.sigmaproject.service.OrderStateRepository;
import ar.edu.utn.sigmaproject.service.OrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeRepository;
import ar.edu.utn.sigmaproject.service.StockMovementRepository;
import ar.edu.utn.sigmaproject.service.SupplyTypeRepository;
import ar.edu.utn.sigmaproject.service.WoodRepository;
import ar.edu.utn.sigmaproject.util.ProductionDateTimeHelper;
import ar.edu.utn.sigmaproject.util.RenderElHelper;


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
	Intbox productUnitsFinishIntbox;
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
	Listbox productionOrderSupplyListbox;
	@Wire
	Listbox productionOrderRawMaterialListbox;
	@Wire
	Button materialsWithdrawalButton;
	@Wire
	Image productImage;
	@Wire
	Button printButton;
	@Wire
	Popup workerSelectionPopup;
	@Wire
	Listbox workerSelectionListbox;

	// services
	@WireVariable
	private ProductionOrderRepository productionOrderRepository;
	@WireVariable
	private ProductionOrderStateRepository productionOrderStateRepository;
	@WireVariable
	private ProductionOrderStateTypeRepository productionOrderStateTypeRepository;
	@WireVariable
	private MachineRepository machineRepository;
	@WireVariable
	private MachineTypeRepository machineTypeRepository;
	@WireVariable
	private WoodRepository woodRepository;
	@WireVariable
	private MaterialRequirementRepository materialRequirementRepository;
	@WireVariable
	private MaterialReservedRepository materialReservedRepository;
	@WireVariable
	private SupplyTypeRepository supplyTypeRepository;
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
	@WireVariable
	private StockMovementRepository stockMovementRepository;

	// atributes
	private ProductionOrder currentProductionOrder;
	private ProductionPlan currentProductionPlan;

	// list
	private List<ProductionOrderDetail> productionOrderDetailList;
	private List<ProductionOrderMaterial> productionOrderSupplyList;
	private List<ProductionOrderMaterial> productionOrderRawMaterialList;

	// list models
	private ListModelList<ProductionOrderDetail> productionOrderDetailListModel;
	private ListModelList<ProductionOrderMaterial> productionOrderSupplyListModel;
	private ListModelList<ProductionOrderMaterial> productionOrderRawMaterialListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentProductionOrder = (ProductionOrder) Executions.getCurrent().getAttribute("selected_production_order");
		if(currentProductionOrder == null) {throw new RuntimeException("ProductionOrder not found");}
		currentProductionPlan = currentProductionOrder.getProductionPlan();
		workerSelectionListbox.setModel(new ListModelList<>(currentProductionOrder.getWorkerList()));
		boolean saveProductionOrder = false;
		currentProductionOrder = productionOrderRepository.findOne(currentProductionOrder.getId());
		for(ProductionOrderMaterial each : currentProductionOrder.getProductionOrderMaterials()) {
			if(each.getQuantityUsed()==null || each.getQuantityUsed().compareTo(BigDecimal.ZERO)==0) {
				each.setQuantityUsed(each.getQuantity());
				if(saveProductionOrder == false) {
					saveProductionOrder = true;
				}
			}
		}
		if(saveProductionOrder == true) {
			currentProductionOrder = productionOrderRepository.save(currentProductionOrder);
		}
		createListener();
		refreshView();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createListener() {
		EventQueue<Event> eq = EventQueues.lookup("Replanning Update Queue", EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener() {
			public void onEvent(Event event) throws Exception {
				if(event.getName().equals("onReplanningUpdate")) {
					refreshView();
				}
			}
		});
	}

	private void refreshView() {
		currentProductionOrder = productionOrderRepository.findOne(currentProductionOrder.getId());
		currentProductionOrder.sortDetailsByProcessTypeSequence();
		productionOrderDetailList = currentProductionOrder.getDetails();
		org.zkoss.image.Image img = null;
		try {
			img = new AImage("", currentProductionOrder.getProduct().getImageData());
		} catch (IOException exception) {

		}
		if(img != null) {
			int[] heightAndWidthArray = RenderElHelper.getHeightAndWidthScaled(img, 175);
			productImage.setHeight(heightAndWidthArray[0] + "px");
			productImage.setWidth(heightAndWidthArray[1] + "px");
			productImage.setStyle("margin: 8px");
		} else {
			productImage.setHeight("0px");
			productImage.setWidth("0px");
			productImage.setStyle("margin: 0px");
		}
		productImage.setContent(img);
		productionPlanNameTextbox.setDisabled(true);
		productNameTextbox.setDisabled(true);
		productUnitsIntbox.setDisabled(true);
		productUnitsFinishIntbox.setDisabled(false);
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
		if(currentProductionOrder.getUnitsFinish() == null || currentProductionOrder.getUnitsFinish() == 0) {
			productUnitsFinishIntbox.setValue(currentProductionOrder.getUnits());
		} else {
			productUnitsFinishIntbox.setValue(currentProductionOrder.getUnitsFinish());
		}
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
		productionOrderSupplyListModel = new ListModelList<ProductionOrderMaterial>(productionOrderSupplyList);
		productionOrderRawMaterialListModel = new ListModelList<ProductionOrderMaterial>(productionOrderRawMaterialList);
		productionOrderSupplyListbox.setModel(productionOrderSupplyListModel);
		productionOrderRawMaterialListbox.setModel(productionOrderRawMaterialListModel);
	}

	@Transactional
	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		ProductionOrderStateType lastStateType = productionOrderStateTypeRepository.findOne(currentProductionOrder.getCurrentStateType().getId());
		List<ProductionOrderMaterial> productionOrderMaterials = new ArrayList<ProductionOrderMaterial>();
		productionOrderMaterials.addAll(productionOrderSupplyList);
		productionOrderMaterials.addAll(productionOrderRawMaterialList);
		currentProductionOrder.setProductionOrderMaterials(productionOrderMaterials);
		// el estado de la orden debe cambiar automaticamente a 3 estados: Preparada, Iniciada, Finalizada
		// TODO si esta finalizada no se debe poder modificar.
		ProductionOrderStateType newStateType = getProductionOrderStateType();
		ProductionOrderStateType productionOrderStateType = null;
		if(!lastStateType.getName().equals(newStateType.getName())) {// si el estado anterior es distindo del nuevo
			if(newStateType.getName().equals("Finalizada")) {
				// si esta Finalizada y aun no se retiraron los materiales de stock, se los retiran
				if(currentProductionOrder.getDateMaterialsWithdrawal() == null) {
					alert("Se registrara el Retiro de Materiales de Stock.");
					materialsWithdrawalAction();
				}
				// se actualiza el stock de productos
				Product product = currentProductionOrder.getProduct();
				// TODO: sumar las cantidades REALES que se produjeron
				product.setStock(product.getStock() + currentProductionOrder.getUnits());
				product = productRepository.save(product);
			}
			productionOrderStateType = newStateType;
		}
		Date dateStartReal = currentProductionOrder.getStartRealDateFromDetails();
		Date dateFinishReal = currentProductionOrder.getFinishRealDateFromDetails();
		currentProductionOrder.setDateStartReal(dateStartReal);
		currentProductionOrder.setDateFinishReal(dateFinishReal);
		currentProductionOrder.setUnitsFinish(productUnitsFinishIntbox.getValue());
		if(productionOrderStateType != null) {
			ProductionOrderState productionOrderState = new ProductionOrderState(productionOrderStateType, new Date());
			productionOrderState = productionOrderStateRepository.save(productionOrderState);
			currentProductionOrder.setState(productionOrderState);
		}
		currentProductionOrder = productionOrderRepository.save(currentProductionOrder);
		updateProductionPlanState();
		alert("Avance de Produccion Registrada.");
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
		// si el estado previo es igual al nuevo no se realiza el guardado
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
		}
	}

	private ProductionOrderStateType getProductionOrderStateType() {
		// devuelve el estado actual de la orden de produccion
		// "Preparada" si no inicio, "Iniciada" o "Finalizada" si finalizaron todos los detalles
		ProductionOrderStateType productionOrderStateType = null;
		boolean finished = true;// si se finalizo todos los procesos
		boolean notStarted = true;// no inicio ningun proceso
		for (ProductionOrderDetail each : productionOrderDetailList) {// no se verifica los cancelados
			if(each.getState() != ProcessState.Cancelado) {
				if(each.getState()==ProcessState.Realizado || each.getQuantityFinished().compareTo(BigDecimal.ZERO)!=0) {
					// si inicio o finalizo algun proceso
					notStarted = false;
				}
				if(each.getState()!=ProcessState.Realizado) {
					// si no finalizo algun proceso
					finished = false;
				}
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
		Include include = (Include) Selectors.iterable(this.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/production_follow_up_list.zul");
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
	}

	public boolean isMachineNecessary(Process process) {
		if(process.getType().getMachineType() != null) {
			return true;
		}
		return false;
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
		Datebox dateboxStartReal = (Datebox) fila.getChildren().get(fila.getChildren().size()-8);
		Datebox dateboxFinishReal = (Datebox) fila.getChildren().get(fila.getChildren().size()-7);
		if(value.compareTo(quantityPiece) >= 0) {
			// si el valor ingresado supera la cantidad, se lo modifica y se agrega la cantidad
			if(value.compareTo(quantityPiece) > 0) {
				element.setValue(quantityPiece.doubleValue());
				data.setQuantityFinished(quantityPiece);
			} else {
				data.setQuantityFinished(value);
			}
			chkbox.setChecked(true);
			data.setState(ProcessState.Realizado);
			data.setDateStartReal(data.getDateStart());
			data.setDateFinishReal(data.getDateFinish());
			dateboxStartReal.setValue(data.getDateStartReal());
			dateboxFinishReal.setValue(data.getDateFinishReal());
			dateboxStartReal.setDisabled(false);
			dateboxFinishReal.setDisabled(false);
		} else {// si el valor ingresado es igual o menor a la cantidad
			data.setState(ProcessState.Pendiente);
		}
	}

	@Listen("onEditProductionOrderDetailIsFinished = #productionOrderDetailGrid")
	public void doEditProductionOrderDetailIsFinished(ForwardEvent evt) {
		ProductionOrderDetail data = (ProductionOrderDetail) evt.getData();// obtenemos el objeto pasado por parametro
		Checkbox element = (Checkbox) evt.getOrigin().getTarget();// obtenemos el elemento web
		Row fila = (Row)element.getParent();
		Doublebox doublebox = (Doublebox) fila.getChildren().get(fila.getChildren().size()-1);
		Datebox dateboxStartReal = (Datebox) fila.getChildren().get(fila.getChildren().size()-8);
		Datebox dateboxFinishReal = (Datebox) fila.getChildren().get(fila.getChildren().size()-7);
		if(element.isChecked()) {
			data.setState(ProcessState.Realizado);
			// agregamos como cantidad finalizada el total
			data.setQuantityFinished(new BigDecimal(data.getQuantityPiece()));
			doublebox.setValue(data.getQuantityPiece());
			// si no existe valor previo se utilizan los valores estimados
			Date dateStartReal = data.getDateStart();
			Date dateFinishReal = data.getDateFinish();
			int indexDetail = productionOrderDetailList.indexOf(data);
			if(indexDetail > 0) {// si no es el primero
				int indexPreviousDetail = indexDetail-1;
				if(productionOrderDetailList.get(indexPreviousDetail).getState() == ProcessState.Cancelado) {
					// si el anterior esta cancelado buscamos el anterior a ese y seguimos buscando hasta que econtremos uno no cancelado o nos salagamos de la lista
					indexPreviousDetail = indexPreviousDetail - 1;
					while(productionOrderDetailList.get(indexPreviousDetail).getState() == ProcessState.Cancelado) {
						indexPreviousDetail = indexPreviousDetail - 1;
						if(indexPreviousDetail < 0) {
							break;
						}
					}
				}
				if(indexPreviousDetail >= 0) {// si no se sale de la lista
					ProductionOrderDetail previousDetail = productionOrderDetailList.get(indexPreviousDetail);
					dateStartReal = previousDetail.getDateFinishReal();
					dateFinishReal = ProductionDateTimeHelper.getFinishDate(dateStartReal, data.getTimeTotal());// se calcula el final real en base al valor del inicio
				}
			}
			// si el proceso fue replanificado, en lugar de seguir las fechas reales del proceso anterior, se asignan las fechas planificadas
			if(isDetailReplanned(data)) {
				data.setDateStartReal(data.getDateStart());
				data.setDateFinishReal(data.getDateFinish());
			} else {
				// agregamos fechas reales para q no sea necesario modificar si no hace falta
				data.setDateStartReal(dateStartReal);
				data.setDateFinishReal(dateFinishReal);
			}
			dateboxStartReal.setValue(data.getDateStartReal());
			dateboxFinishReal.setValue(data.getDateFinishReal());
			dateboxStartReal.setDisabled(false);
			dateboxFinishReal.setDisabled(false);
		} else {
			data.setState(ProcessState.Pendiente);
			data.setDateStartReal(null);
			data.setDateFinishReal(null);
			dateboxStartReal.setValue(null);
			dateboxFinishReal.setValue(null);
			dateboxStartReal.setDisabled(true);
			dateboxFinishReal.setDisabled(true);
		}
	}

	private boolean isDetailReplanned(ProductionOrderDetail data) {
		// busca en las replanificaciones si aparece el detalle que viene por parametro
		for(Replanning each : currentProductionOrder.getReplanningList()) {
			if(each.getProductionOrderDetail().getId() == data.getId()) {
				return true;
			}
		}
		return false;
	}

	@Listen("onUsedMaterialChange = #productionOrderSupplyListbox, #productionOrderRawMaterialListbox")
	public void doUsedMaterialChange(ForwardEvent evt) {
		ProductionOrderMaterial data = (ProductionOrderMaterial) evt.getData();// obtenemos el objeto pasado por parametro
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

	@Listen("onEditUsedMaterialObservation = #productionOrderSupplyListbox, #productionOrderRawMaterialListbox")
	public void doEditUsedMaterialObservation(ForwardEvent evt) {
		ProductionOrderMaterial data = (ProductionOrderMaterial) evt.getData();// obtenemos el objeto pasado por parametro
		Textbox origin = (Textbox)evt.getOrigin().getTarget();
		InputEvent inputEvent = (InputEvent) evt.getOrigin();
		origin.setValue(inputEvent.getValue());
		data.setObservation(origin.getText());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick = #materialsWithdrawalButton")
	public void materialsWithdrawalButtonClick() {
		// si ya se retiraron los materiales mostrar mensaje
		if(currentProductionOrder.getDateMaterialsWithdrawal() != null) {
			Clients.showNotification("Imposible Retirar Materiales, los Materiales ya han sido retirados.");
			return;
		}
		Messagebox.show("Se realizara el retiro de todos los materiales reservados para la orden produccion.", "Confirmar", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
			public void onEvent(Event evt) throws InterruptedException {
				if (evt.getName().equals("onOK")) {
					materialsWithdrawalAction();
					alert("Retiro de Materiales Registrado.");
				}
			}
		});
	}

	private void materialsWithdrawalAction() {
		// buscamos los materiales necesarios para esta orden y luego vemos cuales son las reservas para dichos materiales y disminuimos su cantidad tanto en la reserva como en el stock
		Product product = currentProductionOrder.getProduct();
		Integer units = currentProductionOrder.getUnits();
		String observation = "Para el plan: " + currentProductionOrder.getProductionPlan().getName() + ", nro de orden: " + currentProductionOrder.getNumber();

		// crea stock movement con las cantidades retiradas
		StockMovement stockMovementSupply = new StockMovement();
		stockMovementSupply.setSign((short) -1);// signo de egreso de stock
		stockMovementSupply.setDate(new Date());
		stockMovementSupply.setType(StockMovementType.Supply);
		stockMovementSupply.setObservation(observation);
		StockMovement stockMovementWood = new StockMovement();
		stockMovementWood.setSign((short) -1);// signo de egreso de stock
		stockMovementWood.setDate(new Date());
		stockMovementWood.setType(StockMovementType.Wood);
		stockMovementWood.setObservation(observation);

		for(ProductMaterial each : product.getMaterials()) {
			// la cantidad total a restar de la orden, es la cantidad de materiales del producto, por la cantidad de unidades del producto
			BigDecimal quantityTotal = each.getQuantity().multiply(new BigDecimal(units));
			Item item = each.getItem();
			// se actualiza la cantidad extraida en el requirement
			MaterialRequirement materialRequirement = materialRequirementRepository.findByProductionPlanAndItem(currentProductionPlan, item);
			BigDecimal quantityWithdrawn = materialRequirement.getQuantityWithdrawn().add(quantityTotal);
			materialRequirement.setQuantityWithdrawn(quantityWithdrawn);
			materialRequirementRepository.save(materialRequirement);
			// se busca la reserva del material y se resta la cantidad, y se suma esa cantidad a la cantidad retirada del requirement
			MaterialReserved materialReserved = getMaterialReserved(materialRequirement);
			materialReserved = materialReservedRepository.findOne(materialReserved.getId());
			materialReserved.setStockReserved(materialReserved.getStockReserved().subtract(quantityTotal));
			// se crean los detalles del stock movement
			StockMovementDetail stockMovementDetail = new StockMovementDetail();
			stockMovementDetail.setDescription(item.getDescription());
			stockMovementDetail.setItem(item);
			stockMovementDetail.setQuantity(quantityWithdrawn);
			// se modifica el stock y se guarda con las reservas
			if(item instanceof SupplyType) {
				stockMovementDetail.setStockMovement(stockMovementSupply);
				SupplyType supplyType = (SupplyType) item;
				supplyType = supplyTypeRepository.findOne(supplyType.getId());
				int index = supplyType.getMaterialReservedList().indexOf(materialReserved);
				supplyType.setStock(supplyType.getStock().subtract(quantityTotal));
				supplyType.getMaterialReservedList().set(index, materialReserved);
				supplyTypeRepository.save(supplyType);
				stockMovementSupply.getDetails().add(stockMovementDetail);
			} else if (item instanceof Wood) {
				stockMovementDetail.setStockMovement(stockMovementWood);
				Wood wood = (Wood) item;
				wood = woodRepository.findOne(wood.getId());
				int index = wood.getMaterialReservedList().indexOf(materialReserved);
				wood.setStock(wood.getStock().subtract(quantityTotal));
				wood.getMaterialReservedList().set(index, materialReserved);
				woodRepository.save(wood);
				stockMovementWood.getDetails().add(stockMovementDetail);
			}
		}
		currentProductionOrder.setDateMaterialsWithdrawal(new Date());
		currentProductionOrder = productionOrderRepository.save(currentProductionOrder);
		if(!stockMovementSupply.getDetails().isEmpty()) {
			stockMovementRepository.save(stockMovementSupply);
		}
		if(!stockMovementWood.getDetails().isEmpty()) {
			stockMovementRepository.save(stockMovementWood);
		}
	}

	public boolean isStateFinish(ProcessState processState) {
		return processState == ProcessState.Realizado;
	}

	public boolean isStateCancel(ProcessState processState) {
		return processState == ProcessState.Cancelado;
	}

	@Listen("onChangeProductionOrderDetailDateStartReal = #productionOrderDetailGrid")
	public void doChangeProductionOrderDetailDateStartReal(ForwardEvent evt) {
		ProductionOrderDetail data = (ProductionOrderDetail) evt.getData();// obtenemos el objeto pasado por parametro
		Datebox element = (Datebox) evt.getOrigin().getTarget();// obtenemos el elemento web
		Date value = element.getValue();
		if(value != null) {
			data.setDateStartReal(value);
		} else {
			// no se modifica
			element.setValue(data.getDateStartReal());
		}
	}

	@Listen("onChangeProductionOrderDetailDateFinishReal = #productionOrderDetailGrid")
	public void doChangeProductionOrderDetailDateFinishReal(ForwardEvent evt) {
		ProductionOrderDetail data = (ProductionOrderDetail) evt.getData();// obtenemos el objeto pasado por parametro
		Datebox element = (Datebox) evt.getOrigin().getTarget();// obtenemos el elemento web
		Date value = element.getValue();
		if(value != null) {
			// verificamos que la fecha ingresada no sea de un dia posterior a la fecha planificada y tampoco anterior a la de inicio real
			if(isDateFinishRealValid(data, value)) {
				data.setDateFinishReal(value);
			} else {
				alert("No se puede seleccionar una fecha futura o previa al inicio real.");
				element.setValue(data.getDateFinishReal());
			}
		} else {
			// no se modifica 
			element.setValue(data.getDateFinishReal());
		}
	}

	private boolean isDateFinishRealValid(ProductionOrderDetail productionOrderDetail, Date dateFinishReal) {
		if(dateFinishReal.before(productionOrderDetail.getDateStartReal())) {
			return false;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(productionOrderDetail.getDateFinish());// se usa la fecha planificada de fin
		cal.set(Calendar.HOUR_OF_DAY, ProductionDateTimeHelper.getFirstHourOfDay());
		cal.set(Calendar.MINUTE, ProductionDateTimeHelper.getFirstMinuteOfDay());
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		int nextDay = cal.get(Calendar.DAY_OF_YEAR) + 1;
		if(nextDay > 365) {
			nextDay = 1;
		}
		cal.set(Calendar.DAY_OF_YEAR, nextDay);
		Date oneDayAfter = cal.getTime();
		if(dateFinishReal.after(oneDayAfter)) {
			return false;
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

	@Listen("onClick = #replanningButton")
	public void replanningButtonClick() {
		// muestra la ventana donde se puede agregar la replanificacion
		Executions.getCurrent().setAttribute("selected_production_order", currentProductionOrder);
		final Window win = (Window) Executions.createComponents("/replanning_list.zul", null, null);
		win.setSizable(false);
		win.setPosition("center");
		win.doModal();
	}

	@Listen("onClick = #printButton")
	public void printButtonClick() {
		// muestra el popup para seleccionar el empleado y va a la pantalla de generar el reporte
		workerSelectionPopup.open(printButton, "at_pointer");
	}

	@Listen("onSelect = #workerSelectionListbox")
	public void workerSelectionListboxSelect() {
		Worker workerSelected = workerSelectionListbox.getSelectedItem().getValue();
		Executions.getCurrent().setAttribute("selected_production_plan", currentProductionPlan);
		Executions.getCurrent().setAttribute("selected_production_order", currentProductionOrder);
		Executions.getCurrent().setAttribute("selected_worker", workerSelected);

		Executions.getCurrent().setAttribute("return_page_name", "production_follow_up");
		Map<String, Object> returnParameters = new HashMap<String, Object>();
		returnParameters.put("selected_production_order", currentProductionOrder);
		Executions.getCurrent().setAttribute("return_parameters", returnParameters);

		Include include = (Include) Selectors.iterable(this.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/report_production_order.zul");
	}
}