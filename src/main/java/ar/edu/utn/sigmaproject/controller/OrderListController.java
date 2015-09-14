package ar.edu.utn.sigmaproject.controller;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Client;
import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.service.ClientService;
import ar.edu.utn.sigmaproject.service.OrderDetailService;
import ar.edu.utn.sigmaproject.service.OrderService;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.impl.ClientServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderDetailServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductServiceImpl;

public class OrderListController extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;
	
	@Wire
    Textbox searchTextbox;
    @Wire
    Listbox orderListbox;
    @Wire
	Button newOrderButton;
    @Wire
    Listbox orderDetailListbox;
    
    // services
    private OrderService orderService = new OrderServiceImpl();
    private OrderDetailService orderDetailService = new OrderDetailServiceImpl();
    private ClientService clientService = new ClientServiceImpl();
    private ProductService productService = new ProductServiceImpl();
    
    // list
    private List<Order> orderList;
    private List<OrderDetail> orderDetailList;
    
    // list models
    private ListModelList<Order> orderListModel;
    private ListModelList<OrderDetail> orderDetailListModel;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        orderList = orderService.getOrderList();
        orderListModel = new ListModelList<Order>(orderList);
        orderListbox.setModel(orderListModel);
        orderDetailList = orderDetailService.getOrderDetailList();
        orderDetailListModel = new ListModelList<OrderDetail>(orderDetailList);
        orderDetailListbox.setModel(orderDetailListModel);
    }
    
    @Listen("onSelect = #orderListbox")
    public void onOrderSelect() {
    	Executions.getCurrent().setAttribute("selected_order", ((Order) orderListbox.getSelectedItem().getValue()));
        Include include = (Include) Selectors.iterable(orderListbox.getPage(), "#mainInclude").iterator().next();
    	include.setSrc("/order_creation.zul");
    }
    
    @Listen("onClick = #newOrderButton")
    public void goToOrderCreation() {
    	Executions.getCurrent().setAttribute("selected_order", null);
    	Include include = (Include) Selectors.iterable(orderListbox.getPage(), "#mainInclude").iterator().next();
    	include.setSrc("/order_creation.zul");
    }
    
    public String getClientName(int idClient) {
    	return clientService.getClient(idClient).getName();
    }
    
    public String getProductName(int idProduct) {
    	return productService.getProduct(idProduct).getName();
    }
    
    public String quantityOfDetail(int idOrder) {
    	return orderDetailService.getOrderDetailList(idOrder).size() + "";
    }
}
