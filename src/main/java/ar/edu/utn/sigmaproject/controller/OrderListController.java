package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.domain.OrderState;
import ar.edu.utn.sigmaproject.domain.OrderStateType;
import ar.edu.utn.sigmaproject.service.ClientService;
import ar.edu.utn.sigmaproject.service.OrderDetailService;
import ar.edu.utn.sigmaproject.service.OrderService;
import ar.edu.utn.sigmaproject.service.OrderStateService;
import ar.edu.utn.sigmaproject.service.OrderStateTypeService;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.impl.ClientServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderDetailServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderStateServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderStateTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductServiceImpl;

public class OrderListController extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Grid orderGrid;
	@Wire
	Button newOrderButton;

	// services
	private OrderService orderService = new OrderServiceImpl();
	private OrderDetailService orderDetailService = new OrderDetailServiceImpl();
	private ClientService clientService = new ClientServiceImpl();
	private ProductService productService = new ProductServiceImpl();
	private OrderStateService orderStateService = new OrderStateServiceImpl();
	private OrderStateTypeService orderStateTypeService = new OrderStateTypeServiceImpl();

	// list
	private List<Order> orderList;

	// list models
	private ListModelList<Order> orderListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		orderList = orderService.getOrderList();
		orderListModel = new ListModelList<Order>(orderList);
		orderGrid.setModel(orderListModel);
	}
	/*
    @Listen("onSelect = #orderGrid")
    public void onOrderSelect() {
    	Executions.getCurrent().setAttribute("selected_order", ((Order) orderListbox.getSelectedItem().getValue()));
        Include include = (Include) Selectors.iterable(orderListbox.getPage(), "#mainInclude").iterator().next();
    	include.setSrc("/order_creation.zul");
    }*/

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
					int idOrder = (Integer) ForwEvt.getData();
					if(isStateCancel(idOrder)) {
						alert("No se puede cancelar un Pedido ya cancelado.");
					} else {
						OrderStateTypeService orderStateTypeService = new OrderStateTypeServiceImpl();
						OrderStateType order_state_type = orderStateTypeService.getOrderStateType("cancelado");
						OrderState aux = new OrderState(idOrder, order_state_type.getId(), new Date());
						OrderStateService orderStateService = new OrderStateServiceImpl();
						orderStateService.saveOrderState(aux);// grabamos el estado del pedido
						refreshList();
						alert("Pedido cancelado.");
					}
				}
			}
		});

	}

	@Listen("onEditOrder = #orderGrid")
	public void doEditOrder(ForwardEvent evt) {
		int idOrder = (Integer) evt.getData();
		Executions.getCurrent().setAttribute("selected_order", orderService.getOrder(idOrder));
		Include include = (Include) Selectors.iterable(evt.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/order_creation.zul");
	}

	private void refreshList() {
		orderList = orderService.getOrderList();
		orderListModel = new ListModelList<Order>(orderList);
		orderGrid.setModel(orderListModel);
	}

	public String getClientName(int idClient) {
		return clientService.getClient(idClient).getName();
	}

	public String getProductName(int idProduct) {
		return productService.getProduct(idProduct).getName();
	}

	public String getProductCode(int idProduct) {
		return productService.getProduct(idProduct).getCode();
	}

	public String quantityOfDetail(int idOrder) {
		return orderDetailService.getOrderDetailList(idOrder).size() + "";
	}

	public ListModel<OrderDetail> getOrderDetails(int idOrder) {
		return new ListModelList<OrderDetail>(orderDetailService.getOrderDetailList(idOrder));
	}

	public String getStateName(int idOrder) {
		OrderState lastState = orderStateService.getLastOrderState(idOrder);
		if(lastState != null) {
			OrderStateType aux = orderStateTypeService.getOrderStateType(lastState.getIdOrderStateType());
			return aux.getName();
		} else {
			return "[sin estado]";
		}
	}

	public double totalPrice(int idOrder) {
		List<OrderDetail> order_detail_list = orderDetailService.getOrderDetailList(idOrder);
		BigDecimal total_price = new BigDecimal("0");
		for(OrderDetail order_detail : order_detail_list) {
			if(order_detail.getPrice() != null) {
				total_price = total_price.add(getSubTotal(order_detail.getUnits(), order_detail.getPrice()));
			}
		}
		return total_price.doubleValue();
	}

	public boolean isStateCancel(int idOrder) {
		OrderState last_order_state = orderStateService.getLastOrderState(idOrder);
		OrderStateType cancel_state_type = orderStateTypeService.getOrderStateType("cancelado");
		if(last_order_state.getIdOrderStateType().equals(cancel_state_type.getId())) {// si el ultimo tiene estado cancelado
			return true;
		} else {
			return false;
		}
	}

	public BigDecimal getSubTotal(int units, BigDecimal price) {
		return price.multiply(new BigDecimal(units));
	}
}
