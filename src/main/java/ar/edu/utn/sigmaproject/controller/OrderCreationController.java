package ar.edu.utn.sigmaproject.controller;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
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
    Intbox productUnits;
    @Wire
    Button saveOrderDetailButton;
    @Wire
    Button deleteOrderDetailButton;
    @Wire
    Button resetOrderDetailButton;

    // services
    ProductService productService = new ProductServiceImpl();
    ClientService clientService = new ClientServiceImpl();
    OrderService orderService = new OrderServiceImpl();
    
    // list models
    ListModelList<Product> productPopupListModel;
    ListModelList<Client> clientListModel;
    ListModelList<Order> orderListModel;
    ListModelList<OrderDetail> orderDetailListModel;
    
    // atributes
    private Order currentOrder;
    private OrderDetail currentOrderDetail;
    private Product currentProduct;
    private Client currentClient;
    
    private List<Product> productPopupList;
    private List<OrderDetail> orderDetailList;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        
        productPopupList = productService.getProductList();
        productPopupListModel = new ListModelList<Product>(productPopupList);
        productPopupListbox.setModel(productPopupListModel);
        
        List<Client> clientList = clientService.getClientList();
        clientListModel = new ListModelList<Client>(clientList);
        clientPopupListbox.setModel(clientListModel);
        
        orderDetailList = new ArrayList<OrderDetail>();
        orderDetailListModel = new ListModelList<OrderDetail>(orderDetailList);
        orderDetailListbox.setModel(orderDetailListModel);
        
        currentOrder = (Order) Executions.getCurrent().getAttribute("selected_order");
        currentOrderDetail = null;
        currentProduct = null;
        currentClient = null;
        refreshView();
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
    
    @Listen("onClick = #saveOrderDetailButton")
    public void saveOrderDetail() {
		if(productUnits.getValue()==null || productUnits.getValue().intValue()<=0){
			Clients.showNotification("Ingresar Cantidad del Producto",productUnits);
			return;
		}
		productPopupListModel.remove(currentProduct);// quitamos el producto de la lista del popup
		productPopupListbox.setModel(productPopupListModel);
		currentOrderDetail = new OrderDetail(null,currentProduct.getId(),productUnits.getValue());
		orderDetailList.add(currentOrderDetail);
		orderDetailListModel.add(currentOrderDetail);
		orderDetailListbox.setModel(orderDetailListModel);// actualizamos la vista con el nuevo order detail
		refreshViewOrderDetail();
    }
    
    private void refreshViewOrderDetail() {
  		if (currentOrderDetail == null) {
  			// borramos el text del producto  seleccionado
  			// deseleccionamos la tabla y borramos la cantidad
  			productBandbox.setValue("");
  			productPopupListbox.clearSelection();
  			productUnits.setText(null);
  			currentProduct = null;
  		} else {
  			// lo contrario
  			productBandbox.setValue(getProductName(currentOrderDetail.getIdProduct()));
  			productPopupListbox.clearSelection();
  			productUnits.setText(null);
  			currentProduct = null;
  		}
  	}
    
    private void refreshView() {
  		if (currentOrder == null) {
  			clientBandbox.setValue("");
  	        clientBandbox.close();
  	        productBandbox.setValue("");
  	        productBandbox.close();
  		} else {
  			clientBandbox.setValue(getClientName(currentOrder.getIdClient()));
  	        clientBandbox.close();
  	        productBandbox.setValue("");
  	        productBandbox.close();
  		}
  		currentOrderDetail = null;// se empieza sin OrderDetail seleccionado
  	}
    
    public String getProductName(int idProduct) {
		Product aux = productService.getProduct(idProduct);
		return aux.getName();
    }
    
    public String getClientName(int idClient) {
    	Client aux = clientService.getClient(idClient);
		return aux.getName();
    }
    
}
