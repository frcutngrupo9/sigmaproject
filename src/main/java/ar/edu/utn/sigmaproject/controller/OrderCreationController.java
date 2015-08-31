package ar.edu.utn.sigmaproject.controller;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;

import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.Client;
import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.ClientService;
import ar.edu.utn.sigmaproject.service.OrderService;
import ar.edu.utn.sigmaproject.service.OrderDetailService;
import ar.edu.utn.sigmaproject.service.impl.ProductServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ClientServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderDetailServiceImpl;

public class OrderCreationController extends SelectorComposer<Component>{
    private static final long serialVersionUID = 1L;
    
    @Wire
    Listbox productPopupListbox;
    @Wire
    Listbox orderDetailListbox;
    @Wire
    Bandbox productBandbox;
    @Wire
    Bandbox clientBandbox;
    @Wire
    Listbox clientPopupListbox;
    @Wire
    Datebox orderDateBox;
    @Wire
    Intbox productUnits;
    @Wire
    Button saveOrderDetailButton;
    @Wire
    Button deleteOrderDetailButton;
    @Wire
    Button resetOrderButton;
    @Wire
    Button saveOrderButton;
    @Wire
    Button deleteOrderButton;
    @Wire
    Button resetOrderDetailButton;

    // services
    private ProductService productService = new ProductServiceImpl();
    private ClientService clientService = new ClientServiceImpl();
    private OrderService orderService = new OrderServiceImpl();
    private OrderDetailService orderDetailService = new OrderDetailServiceImpl();
    
    // atributes
    private Order currentOrder;
    private OrderDetail currentOrderDetail;
    private Product currentProduct;
    private Client currentClient;
    
    // list
    private List<Client> clientPopupList;
    private List<Product> productPopupList;
    private List<OrderDetail> orderDetailList;
    
    // list models
    private ListModelList<Product> productPopupListModel;
    private ListModelList<Client> clientPopupListModel;
    private ListModelList<OrderDetail> orderDetailListModel;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        
        productPopupList = productService.getProductList();
        productPopupListModel = new ListModelList<Product>(productPopupList);
        productPopupListbox.setModel(productPopupListModel);
        
        clientPopupList = clientService.getClientList();
        clientPopupListModel = new ListModelList<Client>(clientPopupList);
        clientPopupListbox.setModel(clientPopupListModel);
        
        orderDetailList = new ArrayList<OrderDetail>();
        orderDetailListModel = new ListModelList<OrderDetail>(orderDetailList);
        orderDetailListbox.setModel(orderDetailListModel);
        
        currentOrder = (Order) Executions.getCurrent().getAttribute("selected_order");
        currentOrderDetail = null;
        currentProduct = null;
        currentClient = null;
        refreshViewOrder();
    }
    
    @Listen("onSelect = #productPopupListbox")
    public void selectionProductPopupListbox() {
        currentProduct = (Product) productPopupListbox.getSelectedItem().getValue();
        productBandbox.setValue(currentProduct.getName());
        productBandbox.close();
    }
    
    @Listen("onSelect = #clientPopupListbox")
    public void selectionClientPopupListbox() {
    	currentClient = (Client) clientPopupListbox.getSelectedItem().getValue();
        clientBandbox.setValue(currentClient.getName());
        clientBandbox.close();
    }
    
    @Listen("onClick = #saveOrderButton")
    public void saveOrder() {
    	// agregar comprobacion para ver si todavia no se guardo un detalle activo
		if(currentClient == null){
			Clients.showNotification("Seleccionar Cliente", clientBandbox);
			return;
		}
		if(orderDateBox.getValue() == null){
			Clients.showNotification("Debe seleccionar una fecha", orderDateBox);
			return;
		}
		if(currentOrder == null) { // es un pedido nuevo
			// creamos el nuevo pedido
			currentOrder = new Order(null, currentClient.getId(), orderDateBox.getValue());
			currentOrder = orderService.saveOrder(currentOrder); // se obtiene una orden con id agregado
			for(OrderDetail orderDetail : orderDetailList) {
				orderDetail.setIdOrder(currentOrder.getId());// agregamos el id del pedido a todos los detalles y los guardamos
				orderDetailService.saveOrderDetail(orderDetail);
			}
		} else { // se edita un pedido
			currentOrder = orderService.updateOrder(currentOrder);
			for(OrderDetail orderDetail : orderDetailList) {
				// hay que actualizar las que existen y agregar las que no
				OrderDetail aux = orderDetailService.getOrderDetail(orderDetail.getIdOrder(), orderDetail.getIdProduct());
				if(aux == null) {// no existe
					orderDetailService.saveOrderDetail(orderDetail);
				} else {// existe, se actualiza
					orderDetailService.updateOrderDetail(orderDetail);
				}
			}
		}
		currentOrder = null;
		currentOrderDetail = null;
		refreshViewOrder();
		alert("Pedido guardado.");
    }
    
    @Listen("onClick = #resetOrderButton")
    public void resetOrder() {
		refreshViewOrder();
    }
    
    @Listen("onClick = #saveOrderDetailButton")
    public void saveOrderDetail() {
		if(productUnits.getValue()==null || productUnits.getValue().intValue()<=0){
			Clients.showNotification("Ingresar Cantidad del Producto", productUnits);
			return;
		}
		if(currentProduct == null){
			Clients.showNotification("Debe seleccionar un Producto", productBandbox);
			return;
		}
		if(currentOrderDetail == null) { // es un detalle nuevo
			currentOrderDetail = new OrderDetail(null,currentProduct.getId(), productUnits.getValue());
			orderDetailList.add(currentOrderDetail);
		} else { // se edita un detalle
			currentOrderDetail.setIdProduct(currentProduct.getId());
			currentOrderDetail.setUnits(productUnits.getValue());
			updateOrderDetailList(currentOrderDetail);// actualizamos la lista
		}
		removeProductPopup(currentProduct);// sacamos el producto del popup
		currentOrderDetail = null;
		refreshViewOrderDetail();
    }
    
    private void removeProductPopup(Product product) {
    	productPopupList.remove(product);// quitamos el producto de la lista del popup
    	refreshProductPopup();
	}
    
    private void addProductPopup(Product product) {
    	productPopupList.add(product);// agregamos el producto de la lista del popup
    	refreshProductPopup();
	}
    
    private void refreshProductPopup() {
    	productPopupListModel = new ListModelList<Product>(productPopupList);
		productPopupListbox.setModel(productPopupListModel);
	}

	private void refreshViewOrderDetail() {
  		if (currentOrderDetail == null) {
  			// borramos el text del producto  seleccionado
  			// deseleccionamos la tabla y borramos la cantidad
  			productBandbox.setDisabled(false);
  			productBandbox.setValue("");
  			productPopupListbox.clearSelection();
  			productUnits.setText(null);
  			currentProduct = null;
  			deleteOrderDetailButton.setDisabled(true);
  		} else {
  			currentProduct = productService.getProduct(currentOrderDetail.getIdProduct());
  			productBandbox.setDisabled(true);// no se permite modificar el producto solo las unidades
  			productBandbox.setValue(getProductName(currentOrderDetail.getIdProduct()));
  			productPopupListbox.clearSelection();
  			productUnits.setText(currentOrderDetail.getUnits() + "");
  			deleteOrderDetailButton.setDisabled(false);
  		}
  		orderDetailListModel = new ListModelList<OrderDetail>(orderDetailList);
		orderDetailListbox.setModel(orderDetailListModel);// actualizamos la vista del order detail
  	}
    
    private void refreshViewOrder() {
  		if (currentOrder == null) {// nuevo pedido
  			currentClient = null;
  			currentOrderDetail = null;
  			clientBandbox.setValue("");
  	        clientBandbox.close();
  	        orderDateBox.setValue(null);
  	        orderDetailList = new ArrayList<OrderDetail>();
  	        productPopupList = productService.getProductList();
  	        refreshProductPopup();
  	        deleteOrderButton.setDisabled(true);
  		} else {
  			clientBandbox.setValue(getClientName(currentOrder.getIdClient()));
  	        clientBandbox.close();
  	        orderDateBox.setValue(currentOrder.getDate());
  	        orderDetailList = orderDetailService.getOrderDetailList(currentOrder.getId());
  	        deleteOrderButton.setDisabled(false);
  		}
  		refreshViewOrderDetail();
  	}
    
    public String getProductName(int idProduct) {
		Product aux = productService.getProduct(idProduct);
		return aux.getName();
    }
    
    public String getClientName(int idClient) {
    	Client aux = clientService.getClient(idClient);
		return aux.getName();
    }
    
    private  OrderDetail updateOrderDetailList(OrderDetail orderDetail) {
		if(orderDetail.getIdProduct() == null) {
			throw new IllegalArgumentException("can't update a null-id orderDetail");
		} else {
			orderDetail = OrderDetail.clone(orderDetail);
			int size = orderDetailList.size();
			for(int i = 0; i < size; i++) {
				OrderDetail t = orderDetailList.get(i);
				if(t.getIdProduct().equals(orderDetail.getIdProduct())) {
					orderDetailList.set(i, orderDetail);
					return orderDetail;
				}
			}
			throw new RuntimeException("OrderDetail not found " + orderDetail.getIdProduct());
		}
	}
    
    @Listen("onSelect = #orderDetailListbox")
	public void selectOrderDetail() { // se selecciona un detalle de pedido
		if(orderDetailListModel.isSelectionEmpty()){
			//just in case for the no selection
			currentOrderDetail = null;
		} else {
			currentOrderDetail = orderDetailListModel.getSelection().iterator().next();
			currentProduct = productService.getProduct(currentOrderDetail.getIdProduct());
			refreshViewOrderDetail();
		}
	}
    
    @Listen("onClick = #resetOrderDetailButton")
    public void resetOrderDetail() {
  		refreshViewOrderDetail();
  	}
    
    @Listen("onClick = #deleteOrderDetailButton")
    public void deleteOrderDetail() {
  		if(currentOrderDetail != null) {
  			Messagebox.show("Esta seguro que desea eliminar " + getProductName(currentOrderDetail.getIdProduct()) + "?", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
  			    public void onEvent(Event evt) throws InterruptedException {
  			        if (evt.getName().equals("onOK")) {
  			        	addProductPopup(currentProduct);// agregamos el producto al popup
  						orderDetailList.remove(currentOrderDetail);// quitamos el detalle de la lista
  			        	currentOrderDetail = null;// eliminamos el detalle
  			        	refreshViewOrderDetail();
  			            alert("Detalle eliminado.");
  			        }
  			    }
  			});
  		} 
  	}
    
    @Listen("onClick = #deleteOrderButton")
    public void deleteOrder() {
  		if(currentOrder != null) {
  			Messagebox.show("Esta seguro que desea eliminar el pedido?", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
  			    public void onEvent(Event evt) throws InterruptedException {
  			        if (evt.getName().equals("onOK")) {
  						orderService.deleteOrder(currentOrder);// quitamos el detalle de la lista
  			        	currentOrder = null;
  			        	refreshViewOrder();
  			            alert("Pedido eliminado.");
  			        }
  			    }
  			});
  		} 
  	}
    
}
