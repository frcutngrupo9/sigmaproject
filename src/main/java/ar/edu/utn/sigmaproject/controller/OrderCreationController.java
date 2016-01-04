package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
import org.zkoss.zul.Caption;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Selectbox;

import ar.edu.utn.sigmaproject.domain.MeasureUnit;
import ar.edu.utn.sigmaproject.domain.MeasureUnitType;
import ar.edu.utn.sigmaproject.domain.OrderState;
import ar.edu.utn.sigmaproject.domain.OrderStateType;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.Client;
import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.service.OrderStateService;
import ar.edu.utn.sigmaproject.service.OrderStateTypeService;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.ClientService;
import ar.edu.utn.sigmaproject.service.OrderService;
import ar.edu.utn.sigmaproject.service.OrderDetailService;
import ar.edu.utn.sigmaproject.service.impl.OrderStateServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderStateTypeServiceImpl;
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
    Intbox orderNumber;
    @Wire
    Datebox orderDateBox;
    @Wire
    Datebox orderNeedDateBox;
    @Wire
    Intbox productUnits;
    @Wire
    Doublebox productPrice;
    @Wire
    Button resetOrderButton;
    @Wire
    Button saveOrderButton;
    @Wire
    Button deleteOrderButton;
    @Wire
    Button cancelOrderDetailButton;
    @Wire
    Button saveOrderDetailButton;
    @Wire
    Button deleteOrderDetailButton;
    @Wire
    Button resetOrderDetailButton;
    @Wire
    Selectbox orderStateTypeSelectBox;
    @Wire
    Label orderTotalPriceLabel;
    @Wire
	Caption orderCaption;

    // services
    private ProductService productService = new ProductServiceImpl();
    private ClientService clientService = new ClientServiceImpl();
    private OrderService orderService = new OrderServiceImpl();
    private OrderDetailService orderDetailService = new OrderDetailServiceImpl();
    private OrderStateService orderStateService = new OrderStateServiceImpl();
    private OrderStateTypeService orderStateTypeService = new OrderStateTypeServiceImpl();
    
    // atributes
    private Order currentOrder;
    private OrderDetail currentOrderDetail;
    private OrderState currentOrderState;
    private Product currentProduct;
    private Client currentClient;
    
    // list
    private List<Client> clientPopupList;
    private List<Product> productPopupList;
    private List<OrderStateType> orderStateTypeList;
    private List<OrderDetail> orderDetailList;
    private List<OrderDetail> lateDeleteOrderDetailList;
    
    // list models
    private ListModelList<Product> productPopupListModel;
    private ListModelList<Client> clientPopupListModel;
    private ListModelList<OrderDetail> orderDetailListModel;
    private ListModelList<OrderStateType> orderStateTypeListModel;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        
        clientPopupList = clientService.getClientList();
        clientPopupListModel = new ListModelList<Client>(clientPopupList);
        clientPopupListbox.setModel(clientPopupListModel);
        
        orderDetailList = new ArrayList<OrderDetail>();
        orderDetailListModel = new ListModelList<OrderDetail>(orderDetailList);
        orderDetailListbox.setModel(orderDetailListModel);
        
        lateDeleteOrderDetailList = new ArrayList<OrderDetail>();
        
        orderStateTypeList = orderStateTypeService.getOrderStateTypeList();
        orderStateTypeListModel = new ListModelList<OrderStateType>(orderStateTypeList);
        orderStateTypeSelectBox.setModel(orderStateTypeListModel);
        
        currentOrder = (Order) Executions.getCurrent().getAttribute("selected_order");
        currentOrderDetail = null;
        currentOrderState = null;
        currentProduct = null;
        currentClient = null;
        refreshViewOrder();
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
		
		if(orderDetailList.isEmpty()){
			Clients.showNotification("Debe agregar como minimo 1 producto al pedido", productBandbox);
			return;
		}
		/*
		if(orderNeedDateBox.getValue() == null){
			Clients.showNotification("Debe seleccionar una fecha de  necesidad", orderNeedDateBox);
			return;
		}*/
		int client_id = currentClient.getId();
		int order_number = orderNumber.intValue();
		Date order_date = orderDateBox.getValue();
		Date order_need_date = orderNeedDateBox.getValue();
		int order_state_type_id = orderStateTypeListModel.getElementAt(orderStateTypeSelectBox.getSelectedIndex()).getId();
		if(currentOrder == null) { // es un pedido nuevo
			// creamos el nuevo pedido
			currentOrder = new Order(null, client_id, order_number, order_date, order_need_date);
			currentOrder = orderService.saveOrder(currentOrder, order_state_type_id, orderDetailList);
		} else { // se edita un pedido
			currentOrder.setIdClient(client_id);
			currentOrder.setNeedDate(order_need_date);
			currentOrder.setNumber(order_number);
			currentOrder = orderService.updateOrder(currentOrder, order_state_type_id, orderDetailList, lateDeleteOrderDetailList);
		}
		currentOrder = null;
		currentOrderDetail = null;
		currentOrderState = null;
		refreshViewOrder();
		alert("Pedido guardado.");
    }
    
    @Listen("onClick = #resetOrderButton")
    public void resetOrder() {
		refreshViewOrder();
    }
    
    @Listen("onClick = #cancelOrderDetailButton")
    public void cancelOrderDetail() {
    	currentOrderDetail = null;
    	refreshViewOrderDetail();
    }
    
    @Listen("onSelect = #productPopupListbox")
    public void selectionProductPopupListbox() {
        currentProduct = (Product) productPopupListbox.getSelectedItem().getValue();
        productBandbox.setValue(currentProduct.getName());
        productBandbox.close();
        BigDecimal product_price = currentProduct.getPrice();
        if(product_price != null) {
        	productPrice.setValue(currentProduct.getPrice().doubleValue());
        }
    }
    
    @Listen("onClick = #saveOrderDetailButton")
    public void saveOrderDetail() {
		if(productUnits.getValue()==null || productUnits.getValue().intValue()<=0){
			Clients.showNotification("Ingresar Cantidad del Producto", productUnits);
			return;
		}
		if(currentProduct == null) {
			Clients.showNotification("Debe seleccionar un Producto", productBandbox);
			return;
		}
		// buscamos si el producto no esta en un detalle eliminado
		OrderDetail aux = null;
		for(OrderDetail lateDeleteOrderDetail : lateDeleteOrderDetailList) {
			if(currentProduct.getId().equals(lateDeleteOrderDetail.getIdProduct())) {
				aux = lateDeleteOrderDetail;
			}
		}
		if(aux != null) {
			lateDeleteOrderDetailList.remove(aux);// lo eliminamos de la lista de eliminacion tardia porque el producto sera agregado de nuevo
		}
		int product_id = currentProduct.getId();
		int product_units = productUnits.getValue();
		BigDecimal product_price = new BigDecimal(productPrice.getValue().doubleValue());
		if(currentOrderDetail == null) { // es un detalle nuevo
			if(aux != null) {// si el producto seleccionado ya estaba en un detalle
				aux.setUnits(product_units);
				aux.setPrice(product_price);
				orderDetailList.add(aux);
			} else {
				// se crea un detalle sin id de pedido porque recien se le asignara uno al momento de grabarse definitivamente
				currentOrderDetail = new OrderDetail(null, product_id, product_units, product_price);
				orderDetailList.add(currentOrderDetail);
			}
		} else { // se edita un detalle
			currentOrderDetail.setIdProduct(product_id);
			currentOrderDetail.setUnits(product_units);
			currentOrderDetail.setPrice(product_price);
			updateOrderDetailList(currentOrderDetail);// actualizamos la lista
		}
		refreshProductPopup();// actualizamos el popup
		currentOrderDetail = null;
		refreshViewOrderDetail();
    }
    
    private void refreshProductPopup() {// el popup se actualiza en base a los detalles del pedido
    	productPopupList = productService.getProductList();
    	for(OrderDetail orderDetail : orderDetailList) {
    		Product aux = productService.getProduct(orderDetail.getIdProduct());
    		productPopupList.remove(aux);// sacamos todos los productos del popup
    	}
    	productPopupListModel = new ListModelList<Product>(productPopupList);
		productPopupListbox.setModel(productPopupListModel);
	}
    
    private void refreshOrderDetailListbox() {
    	orderDetailListModel = new ListModelList<OrderDetail>(orderDetailList);
		orderDetailListbox.setModel(orderDetailListModel);// actualizamos la vista del order detail
		orderTotalPriceLabel.setValue("Monto Total: " + getTotalPrice().doubleValue() + " $");
	}

    private void refreshViewOrder() {
  		if (currentOrder == null) {// nuevo pedido
  			orderCaption.setLabel("Creacion de Pedido");
  			orderStateTypeListModel.addToSelection(orderStateTypeService.getOrderStateType("iniciado"));
  			orderStateTypeSelectBox.setModel(orderStateTypeListModel);
  			currentClient = null;
  			clientBandbox.setValue("");
  	        clientBandbox.close();
  	        orderNumber.setValue(null);
  	        orderDateBox.setValue(new Date());
  	        orderNeedDateBox.setValue(null);
  	        orderDetailList = new ArrayList<OrderDetail>();
  	        deleteOrderButton.setDisabled(true);
  	        orderStateTypeSelectBox.setDisabled(true);
  		} else {// editar pedido
  			orderCaption.setLabel("Edicion de Pedido");
  			currentOrderState = orderStateService.getLastOrderState(currentOrder.getId());
  			if(currentOrderState != null) {
  				orderStateTypeListModel.addToSelection(orderStateTypeService.getOrderStateType(currentOrderState.getIdOrderStateType()));
  				orderStateTypeSelectBox.setModel(orderStateTypeListModel);
        	} else {
        		orderStateTypeSelectBox.setSelectedIndex(-1);
        	}
  			currentClient = clientService.getClient(currentOrder.getIdClient());
  			clientBandbox.setValue(currentClient.getName());
  	        clientBandbox.close();
  	        orderNumber.setValue(currentOrder.getNumber());
  	        orderDateBox.setValue(currentOrder.getDate());
  	        orderNeedDateBox.setValue(currentOrder.getNeedDate());
  	        orderDetailList = orderDetailService.getOrderDetailList(currentOrder.getId());
  	        deleteOrderButton.setDisabled(false);
  	        orderStateTypeSelectBox.setDisabled(false);
  		}
  		orderDateBox.setDisabled(true);// nunca se debe poder modificar la fecha de creacion del pedido
  		currentOrderDetail = null;
  		refreshProductPopup();
  		refreshViewOrderDetail();
  	}
    
	private void refreshViewOrderDetail() {
  		if (currentOrderDetail == null) {
  			// borramos el text del producto  seleccionado
  			// deseleccionamos la tabla y borramos la cantidad
  			productBandbox.setDisabled(false);
  			productBandbox.setValue("");
  			productUnits.setValue(null);
  			productPrice.setValue(null);
  			currentProduct = null;
  			deleteOrderDetailButton.setDisabled(true);
  			cancelOrderDetailButton.setDisabled(true);
  		} else {
  			currentProduct = productService.getProduct(currentOrderDetail.getIdProduct());
  			productBandbox.setDisabled(true);// no se permite modificar el producto solo las unidades
  			productBandbox.setValue(getProductName(currentOrderDetail.getIdProduct()));
  			productUnits.setValue(currentOrderDetail.getUnits());
  			if(currentOrderDetail.getPrice() != null) {
  				productPrice.setValue(currentOrderDetail.getPrice().doubleValue());
  			} else {
  				productPrice.setValue(null);
  			}
  			deleteOrderDetailButton.setDisabled(false);
  			cancelOrderDetailButton.setDisabled(false);
  		}
  		productPopupListbox.clearSelection();
  		refreshOrderDetailListbox();
  	}
    
    public String getProductName(int idProduct) {
		Product aux = productService.getProduct(idProduct);
		return aux.getName();
    }
    
    public BigDecimal getSubTotal(int units, BigDecimal price) {
    	return price.multiply(new BigDecimal(units));
    }
    
    public BigDecimal getTotalPrice() {
    	BigDecimal total_price = new BigDecimal("0");
    	if(orderDetailList != null) {
    		for(OrderDetail order_detail : orderDetailList) {
        		if(order_detail.getPrice() != null) {
        			total_price = total_price.add(getSubTotal(order_detail.getUnits(), order_detail.getPrice()));
        		}
        	}
    	}
    	return total_price;
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
  			        	if(currentOrderDetail.getIdOrder() != null) {// si el detalle existe en un pedido
  			        		lateDeleteOrderDetailList.add(currentOrderDetail);// agregamos el detalle a la lista de eliminacion tardia que se realiza al grabar definitivamente
  			        	}
  						orderDetailList.remove(currentOrderDetail);// quitamos el detalle de la lista
  			        	currentOrderDetail = null;// eliminamos el detalle
  			        	refreshProductPopup();// actualizamos el popup para que aparezca el producto eliminado del detalle
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
