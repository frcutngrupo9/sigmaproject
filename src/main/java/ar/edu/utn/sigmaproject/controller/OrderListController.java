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
import java.util.Collections;
import java.util.Comparator;
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
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.domain.OrderState;
import ar.edu.utn.sigmaproject.domain.OrderStateType;
import ar.edu.utn.sigmaproject.service.OrderRepository;
import ar.edu.utn.sigmaproject.service.OrderStateRepository;
import ar.edu.utn.sigmaproject.service.OrderStateTypeRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class OrderListController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Grid orderGrid;
	@Wire
	Button newOrderButton;

	// services
	@WireVariable
	private OrderRepository orderRepository;
	@WireVariable
	private OrderStateRepository orderStateRepository;
	@WireVariable
	private OrderStateTypeRepository orderStateTypeRepository;

	// list
	private List<Order> orderList;

	// list models
	private ListModelList<Order> orderListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		// se crea un listener para cuando se actualice el estado de algun pedido a entregado
		createProductDeliveryListener();
		refreshView();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createProductDeliveryListener() {
		EventQueue<Event> eq = EventQueues.lookup("Product Delivery Queue", EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener() {
			public void onEvent(Event event) throws Exception {
				if(event.getName().equals("onProductDelivery")) {
					refreshView();
				}
			}
		});
	}

	private void refreshView() {
		orderList = orderRepository.findAll();
		sortOrdersByDate();
		orderListModel = new ListModelList<Order>(orderList);
		orderGrid.setModel(orderListModel);
	}

	public void sortOrdersByDate() {
		Comparator<Order> comp = new Comparator<Order>() {
			@Override
			public int compare(Order a, Order b) {
				return b.getDate().compareTo(a.getDate());
			}
		};
		Collections.sort(orderList, comp);
	}

	@Listen("onClick = #newOrderButton")
	public void goToOrderCreation() {
		Executions.getCurrent().setAttribute("selected_order", null);
		Include include = (Include) Selectors.iterable(orderGrid.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/order_creation.zul");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onCancelOrder = #orderGrid")
	public void doCancelOrder(final ForwardEvent ForwEvt) {
		Messagebox.show("Esta seguro que quiere cancelar el pedido?", "Confirmar Cancelacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
			public void onEvent(Event evt) throws InterruptedException {
				if (evt.getName().equals("onOK")) {
					Order order = (Order) ForwEvt.getData();
					if (order.getCurrentStateType().getName().equals("Cancelado")) {
						alert("No se puede cancelar un Pedido ya cancelado.");
					} else {
						OrderStateType orderStateType = orderStateTypeRepository.findFirstByName("Cancelado");
						OrderState orderState = new OrderState(orderStateType, new Date());
						orderState = orderStateRepository.save(orderState);
						order.setState(orderState);
						orderRepository.save(order);// grabamos el estado del pedido
						refreshList();
						alert("Pedido cancelado.");
					}
				}
			}
		});
	}

	@Listen("onEditOrder = #orderGrid")
	public void doEditOrder(ForwardEvent evt) {
		Order order = (Order) evt.getData();
		Executions.getCurrent().setAttribute("selected_order", order);
		Include include = (Include) Selectors.iterable(evt.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/order_creation.zul");
	}

	@Listen("onDeliverOrder = #orderGrid")
	public void doDeliverOrder(ForwardEvent evt) {
		Order order = (Order) evt.getData();
		final HashMap<String, Order> map = new HashMap<String, Order>();
		map.put("selected_order", order);
		Window window = (Window)Executions.createComponents("/product_delivery.zul", null, map);
		window.doModal();
	}

	private void refreshList() {
		orderList = orderRepository.findAll();
		orderListModel = new ListModelList<Order>(orderList);
		orderGrid.setModel(orderListModel);
	}

	public boolean isStateCancel(Order order) {
		return getStateName(order).equals("Cancelado");
	}

	public boolean isStateNotFinished(Order order) {
		return !getStateName(order).equals("Finalizado");
	}

	public boolean isStateDelivered(Order order) {
		return getStateName(order).equals("Entregado");
	}

	private String getStateName(Order order) {
		return order.getCurrentStateType().getName();
	}

	public double totalPrice(Order order) {
		List<OrderDetail> order_detail_list = order.getDetails();
		BigDecimal total_price = BigDecimal.ZERO;
		for(OrderDetail order_detail : order_detail_list) {
			if(order_detail.getPrice() != null) {
				total_price = total_price.add(getSubTotal(order_detail.getUnits(), order_detail.getPrice()));
			}
		}
		return total_price.doubleValue();
	}

	public BigDecimal getSubTotal(int units, BigDecimal price) {
		return price.multiply(new BigDecimal(units));
	}
}
