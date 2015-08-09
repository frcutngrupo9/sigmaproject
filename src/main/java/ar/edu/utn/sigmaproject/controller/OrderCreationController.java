package ar.edu.utn.sigmaproject.controller;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.Client;
import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.domain.ProductionPlanDetail;
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
    Listbox productListbox;
    @Wire
    Bandbox productBandbox;
    @Wire
    Bandbox clientBandbox;
    @Wire
    Listbox clientPopupListbox;
    @Wire
    Intbox productUnits;
    @Wire
    Button addProductButton;

    // services
    ProductService productService = new ProductServiceImpl();
    ClientService clientService = new ClientServiceImpl();
    OrderService orderService = new OrderServiceImpl();
    
    // list models
    ListModelList<Product> productListModel;
    ListModelList<Client> clientListModel;
    ListModelList<Order> orderListModel;
    
    // atributes
    private Product selectedProduct;
    private List<Product> productList;
    private List<Client> clientList;
    private List<OrderDetail> orderDetailList;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        productList = productService.getProductList();
        productListModel = new ListModelList<Product>(productList);
        productPopupListbox.setModel(productListModel);
        
        selectedProduct = null;
        
        clientList = clientService.getClientList();
        clientListModel = new ListModelList<Client>(clientList);
        clientPopupListbox.setModel(clientListModel);
    }
    
    @Listen("onSelect = #productPopupListbox")
    public void selectionProductPopupListbox() {
        selectedProduct = (Product) productPopupListbox.getSelectedItem().getValue();
        productBandbox.setValue(selectedProduct.getName());
        productBandbox.close();
    }
    
    @Listen("onSelect = #clientPopupListbox")
    public void selectionClientPopupListbox() {
        Client selectedClient = (Client) clientPopupListbox.getSelectedItem().getValue();
        clientBandbox.setValue(selectedClient.getName());
        clientBandbox.close();
    }
    
}
