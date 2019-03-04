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
import java.util.Map;

import javax.xml.datatype.Duration;

import org.springframework.transaction.annotation.Transactional;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Item;
import ar.edu.utn.sigmaproject.domain.MaterialRequirement;
import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.domain.OrderState;
import ar.edu.utn.sigmaproject.domain.OrderStateType;
import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.ProcessState;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductMaterial;
import ar.edu.utn.sigmaproject.domain.ProductTotal;
import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.domain.ProductionOrderMaterial;
import ar.edu.utn.sigmaproject.domain.ProductionOrderState;
import ar.edu.utn.sigmaproject.domain.ProductionOrderStateType;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanDetail;
import ar.edu.utn.sigmaproject.domain.ProductionPlanState;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.service.OrderRepository;
import ar.edu.utn.sigmaproject.service.OrderStateRepository;
import ar.edu.utn.sigmaproject.service.OrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeRepository;

public class ProductionPlanCreationController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Listbox orderPopupListbox;
	@Wire
	Grid productionPlanDetailGrid;
	@Wire
	Bandbox orderBandbox;
	@Wire
	Textbox productionPlanNameTextbox;
	@Wire
	Button resetProductionPlanButton;
	@Wire
	Button saveProductionPlanButton;
	@Wire
	Button deleteProductionPlanButton;
	@Wire
	Button newProductionPlanButton;
	@Wire
	Listbox productTotalListbox;
	@Wire
	Combobox productionPlanStateTypeCombobox;
	@Wire
	Caption productionPlanCaption;
	@Wire
	Button returnButton;
	@Wire
	Button returnToProductionButton;
	@Wire
	Button returnToRequirementPlanButton;

	// services
	@WireVariable
	private OrderRepository orderRepository;
	@WireVariable
	private OrderStateRepository orderStateRepository;
	@WireVariable
	private OrderStateTypeRepository orderStateTypeRepository;
	@WireVariable
	private ProductRepository productRepository;
	@WireVariable
	private ProductionPlanRepository productionPlanRepository;
	@WireVariable
	private ProductionPlanStateRepository productionPlanStateRepository;
	@WireVariable
	private ProductionPlanStateTypeRepository productionPlanStateTypeRepository;
	@WireVariable
	private ProductionOrderStateRepository productionOrderStateRepository;
	@WireVariable
	private ProductionOrderStateTypeRepository productionOrderStateTypeRepository;

	// list
	private List<Order> orderPopupList;
	private List<ProductionPlanDetail> productionPlanDetailList;
	private List<ProductTotal> productTotalList;

	// list models
	private ListModelList<Order> orderPopupListModel;
	private ListModelList<ProductionPlanDetail> productionPlanDetailListModel;
	private ListModelList<ProductionPlanStateType> productionPlanStateTypeListModel;

	// atributes
	private ProductionPlan currentProductionPlan;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		productTotalList = new ArrayList<ProductTotal>();
		currentProductionPlan = (ProductionPlan) Executions.getCurrent().getAttribute("selected_production_plan");

		refreshViewProductionPlan();
		refreshProductTotalListbox();
	}

	@Listen("onSelect = #orderPopupListbox")
	public void selectionOrderPopupListbox() {
		Order order = (Order) orderPopupListbox.getSelectedItem().getValue();
		addOrder(order);
	}

	private void refreshOrder() {
		orderBandbox.setValue("");
		orderPopupListbox.clearSelection();
		orderBandbox.close();
	}

	private void refreshOrderPopupList() {// el popup se actualiza en base a los detalles
		List<Order> orderList = orderRepository.findAll();
		orderPopupList = new ArrayList<>();
		// se buscan los pedidos que no estan asignados a un plan y no estan cancelados (estan en estado Creado)
		OrderStateType orderStateTypeInitiated = orderStateTypeRepository.findFirstByName("Creado");
		for(Order each : orderList) {
			if(orderStateTypeRepository.findOne(each.getCurrentStateType().getId()).equals(orderStateTypeInitiated)) {
				orderPopupList.add(each);
			}
		}
		for(ProductionPlanDetail productionPlanDetail : productionPlanDetailList) {
			// no debe contener los pedidos que ya estan en el detalle
			Order aux = productionPlanDetail.getOrder();
			orderPopupList.remove(orderRepository.findOne(aux.getId()));
		}
		orderPopupListModel = new ListModelList<Order>(orderPopupList);
		orderPopupListbox.setModel(orderPopupListModel);
	}

	@Listen("onClick = #resetProductionPlanButton")
	public void resetProductionPlan() {
		refreshViewProductionPlan();
	}

	@Listen("onClick = #deleteProductionPlanButton")
	public void deleteProductionPlan() {
		// no se puede eliminar el plan si ya esta abastecido, parcialmente abastecido o iniciado
		// si se elimina, se deben volver los pedidos a su estado anterior
		if(currentProductionPlan != null) {
			if(!currentProductionPlan.getCurrentStateType().getName().equals("Registrado")) {
				Messagebox.show("No se puede eliminar, el plan se encuentra en un estado posterior a registrado.", "Informacion", Messagebox.OK, Messagebox.ERROR);
				return;
			}
			setProductionPlanDetailStates("Creado");
			productionPlanRepository.delete(currentProductionPlan);
			currentProductionPlan = null;
			refreshViewProductionPlan();
		}
	}

	@Transactional
	@Listen("onClick = #saveProductionPlanButton")
	public void saveProductionPlan() {
		if(currentProductionPlan != null && currentProductionPlan.getCurrentStateType().getName().compareTo("Registrado") != 0) {
			alert("No se puede modificar:  el plan se encuentra en estado abastecido o posterior");
			return;
		}
		if(Strings.isBlank(productionPlanNameTextbox.getValue())) {
			Clients.showNotification("Ingresar Nombre Plan de Produccion", productionPlanNameTextbox);
			return;
		}
		if(productionPlanDetailList.size() == 0) {
			Clients.showNotification("Ingresar al menos 1 pedido", orderBandbox);
			return;
		}
		String productionPlanName = productionPlanNameTextbox.getText().toUpperCase();
		ProductionPlanStateType productionPlanStateType;
		if(productionPlanStateTypeCombobox.getSelectedIndex() == -1) {
			productionPlanStateType = null;
		} else {
			productionPlanStateType = productionPlanStateTypeCombobox.getSelectedItem().getValue();
		}
		List<MaterialRequirement> materialRequirementList = null;
		if(currentProductionPlan == null) { // es un plan nuevo
			// creamos el nuevo plan
			//currentProductionPlan = new ProductionPlan(productionPlanName);
			currentProductionPlan = createProductionPlan(productionPlanName, productionPlanStateType);
			// cambia el estado de los pedidos
			setProductionPlanDetailStates("Planificado");
			// crea los detalles
			materialRequirementList = createMaterialRequirements(currentProductionPlan);
		}/* else { // se edita un plan
			// si se modificaron los detalles, se elimina el producto y se crea uno nuevo con los detalles actuales
			if(detailsModified()) {
				setProductionPlanDetailStates("Creado");
				productionPlanRepository.delete(currentProductionPlan);
				currentProductionPlan = createProductionPlan(productionPlanName, productionPlanStateType);
				setProductionPlanDetailStates("Planificado");
			} else { // si no se modificaron los detalles se modifica solo el nombre
				currentProductionPlan.setName(productionPlanName);
			}
		}*/
		currentProductionPlan = productionPlanRepository.save(currentProductionPlan);

		// si  es nuevo se crean los detalles
		if(materialRequirementList != null) {
			currentProductionPlan.getMaterialRequirements().addAll(materialRequirementList);
			// crea las ordenes de produccion
			currentProductionPlan.getProductionOrderList().addAll(createProductionOrderList(currentProductionPlan));
			currentProductionPlan = productionPlanRepository.save(currentProductionPlan);
		}
		refreshViewProductionPlan();
		alert("Plan guardado.");
	}
	
	private ProductionPlan createProductionPlan(String productionPlanName, ProductionPlanStateType productionPlanStateType) {
		ProductionPlan productionPlan = new ProductionPlan(productionPlanName);
		for(ProductionPlanDetail each : productionPlanDetailList) {
			// se agregan todas las referencias hacia el nuevo plan
			each.setProductionPlan(productionPlan);
		}
		productionPlan.getPlanDetails().addAll(productionPlanDetailList);
		ProductionPlanState productionPlanState = new ProductionPlanState(productionPlanStateType, new Date());
		productionPlanState = productionPlanStateRepository.save(productionPlanState);
		productionPlan.setState(productionPlanState);
		return productionPlan;
	}

	private boolean detailsModified() {
		// lee el plan de bd y si los detalles son iguales a los del current, se considera que no se modificaron los detalles
		ProductionPlan dbProductionPlan = productionPlanRepository.getOne(currentProductionPlan.getId());
		List<ProductionPlanDetail> dbDetails = dbProductionPlan.getPlanDetails();
		List<ProductionPlanDetail> currentDetails = currentProductionPlan.getPlanDetails();
		if(dbDetails.size() != currentDetails.size()) {
			return true;
		}
		for(ProductionPlanDetail eachDB : dbDetails) {
			// deberian existir 1 por cada detalle
			boolean found = false;
			for(ProductionPlanDetail eachCurrent : dbDetails) {
				if(eachDB.getId()==eachCurrent.getId()) {
					found = true;
				}
			}
			if(found == false) {
				return true;
			}
		}
		return false;
	}

	private void setProductionPlanDetailStates(String stateName) {
		for(ProductionPlanDetail each : productionPlanDetailList) {
			OrderStateType orderStateType = orderStateTypeRepository.findFirstByName(stateName);
			Order order = each.getOrder();
			OrderState state = new OrderState(orderStateType, new Date());
			state = orderStateRepository.save(state);
			order.setState(state);
			orderRepository.save(order);
		}
	}

	private List<ProductionOrderDetail> createProductionOrderDetailList(ProductionOrder productionOrder) {
		List<ProductionOrderDetail> details = new ArrayList<>();
		for(Piece piece : productionOrder.getProduct().getPieces()) {
			List<Process> auxProcessList = piece.getProcesses();
			for(Process process : auxProcessList) {
				// por cada proceso hay que crear un detalle
				Integer units = productionOrder.getUnits();
				Integer quantityPiece = units * piece.getUnits();// cantidad total de la pieza
				Duration timeTotal = process.getTime().multiply(quantityPiece);// tiempo del proceso de las piezas por el total de las piezas
				details.add(new ProductionOrderDetail(productionOrder, process, ProcessState.Pendiente, null, timeTotal, quantityPiece));
			}
		}
		return details;
	}

	private List<ProductionOrderMaterial> createProductionOrderMaterialList(ProductionOrder productionOrder) {
		List<ProductionOrderMaterial> list = new ArrayList<>();
		for(ProductMaterial each : productionOrder.getProduct().getMaterials()) {
			BigDecimal totalQuantity = each.getQuantity().multiply(new BigDecimal(productionOrder.getUnits()));
			ProductionOrderMaterial productionOrderMaterial = new ProductionOrderMaterial(productionOrder, each.getItem(), totalQuantity);
			list.add(productionOrderMaterial);
		}
		return list;
	}

	private List<MaterialRequirement> createMaterialRequirements(ProductionPlan productionPlan) {
		// busca requerimientos de materias primas
		List<MaterialRequirement> list = new ArrayList<MaterialRequirement>();
		List<ProductTotal> productTotalList = getProductTotalList();
		for(ProductTotal productTotal : productTotalList) {
			Product product = productTotal.getProduct();
			for(ProductMaterial productMaterial : product.getMaterials()) {
				Item item = productMaterial.getItem();
				MaterialRequirement auxMaterialRequirement = null;
				for(MaterialRequirement eachMaterialRequirement : list) {// buscamos si la materia prima no se encuentra agregada
					if(item.equals(eachMaterialRequirement.getItem())) {
						auxMaterialRequirement = eachMaterialRequirement;
					}
				}
				if(auxMaterialRequirement != null) {// la materia prima si se encuentra agregada, sumamos sus cantidades
					auxMaterialRequirement.setQuantity(auxMaterialRequirement.getQuantity().add(productMaterial.getQuantity().multiply(new BigDecimal(productTotal.getTotalUnits()))));
				} else {// la materia prima no se encuentra, se la agrega
					list.add(new MaterialRequirement(item, productionPlan, productMaterial.getQuantity().multiply(new BigDecimal(productTotal.getTotalUnits()))));
				}
			}
		}
		return list;
	}

	private List<ProductionOrder> createProductionOrderList(ProductionPlan productionPlan) {
		// busca requerimientos de materias primas
		List<ProductionOrder> list = new ArrayList<ProductionOrder>();
		ProductionOrderStateType stateCreated = productionOrderStateTypeRepository.findFirstByName("Registrada");
		int sequence = 0;
		for(ProductTotal each : getProductTotalList()) {
			sequence += 1;
			ProductionOrderState productionOrderState = new ProductionOrderState(stateCreated, new Date());
			productionOrderState = productionOrderStateRepository.save(productionOrderState);
			ProductionOrder productionOrder = new ProductionOrder(sequence, currentProductionPlan, each.getProduct(), each.getTotalUnits(), productionOrderState);
			//  agrega los detalles
			productionOrder.setDetails(createProductionOrderDetailList(productionOrder));
			// agrega los materiales a las ordenes de produccion
			productionOrder.setProductionOrderMaterials(createProductionOrderMaterialList(productionOrder));
			list.add(productionOrder);
		}
		return list;
	}

	private void addOrder(Order order) {
		productionPlanDetailList.add(new ProductionPlanDetail(currentProductionPlan, order));
		refreshProductionPlanDetailListGrid();
		refreshOrderPopupList();
		refreshOrder();
		refreshProductTotalListbox();
	}

	@Listen("onRemoveOrder = #productionPlanDetailGrid")
	public void doRemoveOrder(ForwardEvent evt) {
		ProductionPlanDetail productionPlanDetail = (ProductionPlanDetail) evt.getData();
		productionPlanDetailList.remove(productionPlanDetail);
		refreshProductionPlanDetailListGrid();
		refreshProductTotalListbox();
		refreshOrderPopupList();
	}

	private void refreshProductionPlanDetailListGrid() {
		productionPlanDetailListModel = new ListModelList<ProductionPlanDetail>(productionPlanDetailList);
		productionPlanDetailGrid.setModel(productionPlanDetailListModel);
	}

	private void refreshProductTotalListbox() {
		productTotalList = getProductTotalList();
		ListModelList<ProductTotal> productTotalListModel = new ListModelList<ProductTotal>(productTotalList);
		productTotalListbox.setModel(productTotalListModel);
	}

	private List<ProductTotal> getProductTotalList() {
		Map<Product, Integer> productTotalMap = new HashMap<Product, Integer>();
		for(ProductionPlanDetail auxProductionPlanDetail : productionPlanDetailList) {
			for(OrderDetail auxOrderDetail : auxProductionPlanDetail.getOrder().getDetails()) {
				Product product = productRepository.findOne(auxOrderDetail.getProduct().getId());
				Integer totalUnits = productTotalMap.get(product);
				productTotalMap.put(product, (totalUnits == null) ? auxOrderDetail.getUnits() : totalUnits + auxOrderDetail.getUnits());
			}
		}
		List<ProductTotal> list = new ArrayList<ProductTotal>();
		for (Map.Entry<Product, Integer> entry : productTotalMap.entrySet()) {
			Product product = entry.getKey();
			Integer totalUnits = entry.getValue();
			ProductTotal productTotal = new ProductTotal(product, totalUnits);
			list.add(productTotal);
		}
		return list;
	}

	private void refreshViewProductionPlan() {
		refreshOrder();
		productionPlanStateTypeListModel = new ListModelList<ProductionPlanStateType>(productionPlanStateTypeRepository.findAll());
		if (currentProductionPlan == null) {// nuevo plan de produccion
			productionPlanCaption.setLabel("Creacion de Plan de Produccion");
			productionPlanStateTypeListModel.addToSelection(productionPlanStateTypeRepository.findFirstByName("Registrado"));
			productionPlanStateTypeCombobox.setModel(productionPlanStateTypeListModel);
			int planNumber = productionPlanRepository.findAll().size() + 1;
			productionPlanNameTextbox.setText("PLAN " + planNumber);
			productionPlanDetailList = new ArrayList<ProductionPlanDetail>();
			saveProductionPlanButton.setDisabled(false);
			deleteProductionPlanButton.setDisabled(true);
			productionPlanStateTypeCombobox.setDisabled(true);
			returnToProductionButton.setDisabled(true);
			returnToRequirementPlanButton.setDisabled(true);
		} else {// se edita plan de produccion
			// se busca de la bd el plan ya que puede ser que el current plan tenga detalles modificados
			currentProductionPlan = productionPlanRepository.findOne(currentProductionPlan.getId());
			productionPlanCaption.setLabel("Edicion de Plan de Produccion");
			ProductionPlanStateType productionPlanStateType = currentProductionPlan.getCurrentStateType();
			if (productionPlanStateType != null) {
				productionPlanStateTypeListModel.addToSelection(productionPlanStateTypeRepository.findOne(productionPlanStateType.getId()));
				productionPlanStateTypeCombobox.setModel(productionPlanStateTypeListModel);
			} else {
				productionPlanStateTypeCombobox.setSelectedIndex(-1);
			}
			if(currentProductionPlan.getName() != null) {
				productionPlanNameTextbox.setText(currentProductionPlan.getName());
			} else {
				productionPlanNameTextbox.setText("");
			}
			productionPlanDetailList = currentProductionPlan.getPlanDetails();
			saveProductionPlanButton.setDisabled(true);// no se permite modificar un plan creado
			deleteProductionPlanButton.setDisabled(false);
			productionPlanStateTypeCombobox.setDisabled(false);
			returnToProductionButton.setDisabled(false);
			returnToRequirementPlanButton.setDisabled(false);
		}
		refreshOrderPopupList();
		refreshProductionPlanDetailListGrid();
		refreshProductTotalListbox();
	}

	@Listen("onClick = #returnButton")
	public void returnButtonClick() {
		Include include = (Include) Selectors.iterable(this.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/production_plan_list.zul");
	}

	private void filter() {
		List<Order> some = new ArrayList<>();
		String nameFilter = orderBandbox.getValue().toLowerCase();
		for(Order each : orderPopupList) {// busca filtrando por varios atributos
			if((each.getClient().getName()+each.getClient().getEmail()+each.getNumber()).toLowerCase().contains(nameFilter) || nameFilter.equals("")) {
				some.add(each);
			}
		}
		orderPopupListModel = new ListModelList<Order>(some);
		orderPopupListbox.setModel(orderPopupListModel);
	}

	@Listen("onChanging = #orderBandbox")
	public void changeFilter(InputEvent event) {
		Bandbox target = (Bandbox)event.getTarget();
		target.setText(event.getValue());
		filter();
	}

	@Listen("onClick = #returnToProductionButton")
	public void returnToProductionButtonClick() {
		Executions.getCurrent().setAttribute("selected_production_plan", currentProductionPlan);
		Include include = (Include) Selectors.iterable(this.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/production_order_list.zul");
	}

	@Listen("onClick = #returnToRequirementPlanButton")
	public void returnToRequirementPlanButtonClick() {
		Executions.getCurrent().setAttribute("selected_production_plan", currentProductionPlan);
		Include include = (Include) Selectors.iterable(this.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/requirement_plan_creation.zul");
	}

	@Listen("onClick = #newProductionPlanButton")
	public void newProductionPlanButtonClick() {
		currentProductionPlan = null;
		refreshProductTotalListbox();
		refreshViewProductionPlan();
	}
}