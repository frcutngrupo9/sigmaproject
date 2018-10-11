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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.domain.OrderState;
import ar.edu.utn.sigmaproject.domain.OrderStateType;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.service.OrderRepository;
import ar.edu.utn.sigmaproject.service.OrderStateRepository;
import ar.edu.utn.sigmaproject.service.OrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProductDeliveryController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Window productDeliveryWindow;
	@Wire
	Listbox orderDetailListbox;
	@Wire
	Intbox orderNumberIntbox;
	@Wire
	Textbox clientNameTextbox;
	@Wire
	Textbox needDateTextbox;
	@Wire
	Datebox deliveryDatebox;
	@Wire
	Textbox numberBillTextbox;
	@Wire
	Button saveButton;
	@Wire
	Button cancelButton;
	@Wire
	Button resetButton;

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
	private ProductionOrderRepository productionOrderRepository;

	// attributes
	private Order currentOrder;

	// list
	private List<OrderDetail> orderDetailList;

	// list models
	private ListModelList<OrderDetail> orderDetailListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentOrder = (Order) Executions.getCurrent().getArg().get("selected_order");
		if(currentOrder == null) {throw new RuntimeException("Order null");}
		orderDetailList = currentOrder.getDetails();
		orderDetailListModel = new ListModelList<>(orderDetailList);
		refreshView();
	}

	private void refreshView() {
		orderDetailListbox.setModel(orderDetailListModel);
		orderNumberIntbox.setDisabled(true);
		orderNumberIntbox.setValue(currentOrder.getNumber());
		clientNameTextbox.setDisabled(true);
		clientNameTextbox.setText(currentOrder.getClient().getName());
		needDateTextbox.setDisabled(true);
		Date needDate = currentOrder.getNeedDate();
		String needDateString = "";
		if(needDate != null) {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			needDateString = dateFormat.format(needDate);
		}
		needDateTextbox.setText(needDateString);
		deliveryDatebox.setValue(new Date());
		numberBillTextbox.setValue(null);
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		productDeliveryWindow.detach();
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(deliveryDatebox.getValue() == null) {
			Clients.showNotification("Debe seleccionar una fecha de entrega del pedido.", deliveryDatebox);
			return;
		}
		if(numberBillTextbox.getValue() == null || numberBillTextbox.getValue().equals("")) {
			Clients.showNotification("Debe ingresar un numero de factura.", numberBillTextbox);
			return;
		}
		Date deliveryDate = deliveryDatebox.getValue();
		String numberBill = numberBillTextbox.getValue();
		OrderStateType stateTypeEntregado = orderStateTypeRepository.findFirstByName("Entregado");
		OrderState orderState = new OrderState(stateTypeEntregado, deliveryDate);
		orderState = orderStateRepository.save(orderState);
		currentOrder.setState(orderState);
		currentOrder.setNumberBill(numberBill);
		currentOrder = orderRepository.save(currentOrder);
		// modifica la cantidad en stock
		for(OrderDetail each : currentOrder.getDetails()) {
			Product product = each.getProduct();
			product.setStock(product.getStock() - each.getUnits());
			product = productRepository.save(product);
		}
		EventQueue<Event> eq = EventQueues.lookup("Product Delivery Queue", EventQueues.DESKTOP, true);
		eq.publish(new Event("onProductDelivery", null, null));
		alert("Entrega de Producto Registrada.");
		productDeliveryWindow.detach();
	}

	public String getFormattedDateStart(OrderDetail orderDetail) {
		// devuelve la fecha de inicio real de la produccion
		ProductionOrder productionOrder = getCorrelativeProductionOrder(currentOrder, orderDetail.getProduct());// busca la orden de produccion que tiene el mismo producto del detalle
		if(productionOrder != null) {
			Date date = productionOrder.getDateStartReal();
			if(date != null) {
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				return dateFormat.format(date);
			}
		}
		return "";
	}

	public String getFormattedDateFinish(OrderDetail orderDetail) {
		// devuelve la fecha de finalizacion real de la produccion
		ProductionOrder productionOrder = getCorrelativeProductionOrder(currentOrder, orderDetail.getProduct());// busca la orden de produccion que tiene el mismo producto del detalle
		if(productionOrder != null) {
			Date date = productionOrder.getDateFinishReal();
			if(date != null) {
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				return dateFormat.format(date);
			}
		}
		return "";
	}

	private ProductionOrder getCorrelativeProductionOrder(Order order, Product product) {
		product = productRepository.findOne(product.getId());
		order = orderRepository.findOne(order.getId());
		ProductionPlan productionPlan = productionPlanRepository.findByPlanDetailsOrder(order); 
		ProductionOrder productionOrder = productionOrderRepository.findByProductionPlanAndProduct(productionPlan, product);
		return productionOrder;
	}

	public String getPlanName(OrderDetail orderDetail) {
		ProductionOrder productionOrder = getCorrelativeProductionOrder(currentOrder, orderDetail.getProduct());// busca la orden de produccion que tiene el mismo producto del detalle
		if(productionOrder != null) {
			return productionOrder.getProductionPlan().getName();
		}
		return "";
	}
}
