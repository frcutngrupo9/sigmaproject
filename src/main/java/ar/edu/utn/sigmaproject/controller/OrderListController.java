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
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.domain.OrderState;
import ar.edu.utn.sigmaproject.domain.OrderStateType;
import ar.edu.utn.sigmaproject.service.ClientRepository;
import ar.edu.utn.sigmaproject.service.OrderRepository;
import ar.edu.utn.sigmaproject.service.OrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductRepository;

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
	private ClientRepository clientRepository;
	@WireVariable
	private ProductRepository productSRepository;
	@WireVariable
	private OrderStateTypeRepository orderStateTypeRepository;

	// list
	private List<Order> orderList;

	// list models
	private ListModelList<Order> orderListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		orderList = orderRepository.findAll();
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
					Order order = (Order) ForwEvt.getData();
					if (order.getCurrentStateType().getName().equals("Cancelado")) {
						alert("No se puede cancelar un Pedido ya cancelado.");
					} else {
						OrderStateType orderStateType = orderStateTypeRepository.findFirstByName("Cancelado");
						order.setState(new OrderState(orderStateType, new Date()));
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

	private void refreshList() {
		orderList = orderRepository.findAll();
		orderListModel = new ListModelList<Order>(orderList);
		orderGrid.setModel(orderListModel);
	}

	public boolean isStateCancel(Order order) {
		return getStateName(order).equals("Cancelado");
	}

	public String getStateName(Order order) {
		OrderStateType currentStateType = order.getCurrentStateType();
		if(currentStateType != null) {
			return currentStateType.getName();
		} else {
			return "[sin estado]";
		}
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
