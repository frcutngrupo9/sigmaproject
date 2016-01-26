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
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
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

import ar.edu.utn.sigmaproject.domain.OrderState;
import ar.edu.utn.sigmaproject.domain.OrderStateType;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.Client;
import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.service.OrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductRepository;
import ar.edu.utn.sigmaproject.service.ClientRepository;
import ar.edu.utn.sigmaproject.service.OrderRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
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
    @WireVariable
    private ProductRepository productRepository;
    
    @WireVariable
    private ClientRepository clientRepository;
    
    @WireVariable
    private OrderRepository orderRepository;
    
    @WireVariable
    private OrderStateTypeRepository orderStateTypeRepository;
    
    // attributes
    private Order currentOrder;
    private OrderDetail currentOrderDetail;
    private Product currentProduct;
    private Client currentClient;
    
    // list
    private List<Client> clientPopupList;
    private List<Product> productPopupList;
    private List<OrderStateType> orderStateTypeList;
    private List<OrderDetail> orderDetailList;
    
    // list models
    private ListModelList<Product> productPopupListModel;
    private ListModelList<Client> clientPopupListModel;
    private ListModelList<OrderDetail> orderDetailListModel;
    private ListModelList<OrderStateType> orderStateTypeListModel;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        
        clientPopupList = clientRepository.findAll();
        clientPopupListModel = new ListModelList<Client>(clientPopupList);
        clientPopupListbox.setModel(clientPopupListModel);
        
        orderDetailList = new ArrayList<OrderDetail>();
        orderDetailListModel = new ListModelList<OrderDetail>(orderDetailList);
        orderDetailListbox.setModel(orderDetailListModel);
        
        orderStateTypeList = orderStateTypeRepository.findAll();
        orderStateTypeListModel = new ListModelList<OrderStateType>(orderStateTypeList);
        orderStateTypeSelectBox.setModel(orderStateTypeListModel);
        
        currentOrder = (Order) Executions.getCurrent().getAttribute("selected_order");
        currentOrderDetail = null;
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
		/*
		if(orderNeedDateBox.getValue() == null){
			Clients.showNotification("Debe seleccionar una fecha de  necesidad", orderNeedDateBox);
			return;
		}*/
		int order_number = orderNumber.intValue();
		Date order_date = orderDateBox.getValue();
		Date order_need_date = orderNeedDateBox.getValue();
		OrderStateType orderStateType = orderStateTypeListModel.getElementAt(orderStateTypeSelectBox.getSelectedIndex());
		if(currentOrder == null) { // es un pedido nuevo
			// creamos el nuevo pedido
			currentOrder = new Order(currentClient, order_number, order_date, order_need_date);
			currentOrder.setDetails(orderDetailList);
			currentOrder.getStates().add(new OrderState(currentOrder, orderStateType, new Date()));
			currentOrder.setCurrentStateType(orderStateType);
		} else { // se edita un pedido
			currentOrder.setClient(currentClient);
			currentOrder.setNeedDate(order_need_date);
			currentOrder.setNumber(order_number);
			currentOrder.setDetails(orderDetailList);
			if (!currentOrder.getCurrentStateType().equals(orderStateType)) {
				currentOrder.getStates().add(new OrderState(currentOrder, orderStateType, new Date()));
				currentOrder.setCurrentStateType(orderStateType);
			}
		}
		orderRepository.save(currentOrder);
		currentOrder = null;
		currentOrderDetail = null;
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
		int product_units = productUnits.getValue();
		BigDecimal product_price = new BigDecimal(productPrice.getValue().doubleValue());
		if(currentOrderDetail == null) { // es un detalle nuevo
			// se crea un detalle sin id de pedido porque recien se le asignara uno al momento de grabarse definitivamente
			currentOrderDetail = new OrderDetail(currentOrder, currentProduct, product_units, product_price);
			orderDetailList.add(currentOrderDetail);
		} else { // se edita un detalle
			currentOrderDetail.setProduct(currentProduct);
			currentOrderDetail.setUnits(product_units);
			currentOrderDetail.setPrice(product_price);
		}
		refreshProductPopup();// actualizamos el popup
		currentOrderDetail = null;
		refreshViewOrderDetail();
    }
    
    private void refreshProductPopup() {// el popup se actualiza en base a los detalles del pedido
    	productPopupList = productRepository.findAll();
    	for(OrderDetail orderDetail : orderDetailList) {
    		Product aux = orderDetail.getProduct();
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
  			orderStateTypeListModel.addToSelection(orderStateTypeRepository.findByName("iniciado"));
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
  			if (currentOrder.getCurrentStateType() != null) {
  				orderStateTypeListModel.addToSelection(currentOrder.getCurrentStateType());
  				orderStateTypeSelectBox.setModel(orderStateTypeListModel);
        	} else {
        		orderStateTypeSelectBox.setSelectedIndex(-1);
        	}
  			currentClient = currentOrder.getClient();
  			clientBandbox.setValue(currentClient.getName());
  	        clientBandbox.close();
  	        orderNumber.setValue(currentOrder.getNumber());
  	        orderDateBox.setValue(currentOrder.getDate());
  	        orderNeedDateBox.setValue(currentOrder.getNeedDate());
  	        orderDetailList = currentOrder.getDetails();
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
  			currentProduct = currentOrderDetail.getProduct();
  			productBandbox.setDisabled(true);// no se permite modificar el producto solo las unidades
  			productBandbox.setValue(currentOrderDetail.getProduct().getName());
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
    
    public String getClientName(Client client) {
		return client.getName();
    }
    
    @Listen("onSelect = #orderDetailListbox")
	public void selectOrderDetail() { // se selecciona un detalle de pedido
		if(orderDetailListModel.isSelectionEmpty()){
			//just in case for the no selection
			currentOrderDetail = null;
		} else {
			currentOrderDetail = orderDetailListModel.getSelection().iterator().next();
			currentProduct = currentOrderDetail.getProduct();
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
  			Messagebox.show("Esta seguro que desea eliminar " + currentOrderDetail.getProduct().getName() + "?", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener<Event>() {
  			    public void onEvent(Event evt) throws InterruptedException {
  			        if (evt.getName().equals("onOK")) {
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
  			Messagebox.show("Esta seguro que desea eliminar el pedido?", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener<Event>() {
  			    public void onEvent(Event evt) throws InterruptedException {
  			        if (evt.getName().equals("onOK")) {
  						orderRepository.delete(currentOrder);// quitamos el detalle de la lista
  			        	currentOrder = null;
  			        	refreshViewOrder();
  			            alert("Pedido eliminado.");
  			        }
  			    }
  			});
  		} 
  	}
    
}
