package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductExistence;
import ar.edu.utn.sigmaproject.service.ClientService;
import ar.edu.utn.sigmaproject.service.ProductExistenceService;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.impl.ClientServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductExistenceServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductServiceImpl;

public class ProductStockController extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;
	
	@Wire
    Textbox searchTextbox;
	@Wire
    Listbox productListbox;
	@Wire
    Grid productExistenceGrid;
	@Wire
	Textbox codeTextBox;
	@Wire
	Textbox nameTextBox;
	@Wire
	Intbox stockIntbox;
	@Wire
	Intbox stockMinIntbox;
	@Wire
	Intbox stockRepoIntbox;
	@Wire
    Button saveButton;
	@Wire
    Button cancelButton;
    @Wire
    Button resetButton;
    @Wire
    Button newProvisionOrderButton;
    @Wire
	Component orderCreationBlock;
    @Wire
    Grid orderDetailGrid;
    @Wire
    Intbox orderNumber;
    @Wire
    Datebox orderNeedDateBox;
    
    // services
    private ProductExistenceService productExistenceService = new ProductExistenceServiceImpl();
    private ProductService productService = new ProductServiceImpl();
    private ClientService clientService = new ClientServiceImpl();
    
    // atributes
    private ProductExistence currentProductExistence;
    private Product currentProduct;
    private Order currentOrder;
    
    // list
    private List<Product> productList;
    private List<OrderDetail> orderDetailList;
    
    // list models
    private ListModelList<Product> productListModel;
    private ListModelList<OrderDetail> orderDetailListModel;
	
	@Override
    public void doAfterCompose(Component comp) throws Exception{
        super.doAfterCompose(comp);
        //SupplyCreationController supplyCreationController = (SupplyCreationController) supplyCreationWindow.getAttribute("supplyCreationWindow$composer"); (codigo que quedo cuando se manejaba una ventana para crear el insumo)
        productList = productService.getProductList();
        productListModel = new ListModelList<Product>(productList);
        productListbox.setModel(productListModel);
        currentProduct = null;
        refreshView();
        
        currentOrder = null;
        refreshViewOrder();
    }
	
	@Listen("onClick = #searchButton")
    public void search() {
    }
	
	@Listen("onClick = #cancelButton")
    public void cancelButtonClick() {
		currentProduct = null;
        refreshView();
    }
    
    @Listen("onClick = #resetButton")
    public void resetButtonClick() {
        refreshView();
    }
    
    @Listen("onSelect = #productListbox")
    public void doListBoxSelect() {
        if(productListModel.isSelectionEmpty()) {
            //just in case for the no selection
        	currentProduct = null;
        } else {
        	if(currentProduct == null) {// si no hay nada editandose
        		currentProduct = productListModel.getSelection().iterator().next();
        	}
        }
        refreshView();
    }
	
	private void refreshView() {
		productListModel.clearSelection();
		productListbox.setModel(productListModel);// se actualiza la lista
		codeTextBox.setDisabled(true);
		nameTextBox.setDisabled(true);// no se deben poder modificar
        if(currentProduct == null) {// no editando ni creando
        	productExistenceGrid.setVisible(false);
        	codeTextBox.setValue(null);
        	nameTextBox.setValue(null);
        	stockIntbox.setValue(null);
        	stockMinIntbox.setValue(null);
        	stockRepoIntbox.setValue(null);
			saveButton.setDisabled(true);
			cancelButton.setDisabled(true);
			resetButton.setDisabled(true);
			newProvisionOrderButton.setDisabled(false);
			currentProductExistence = null;
		}else {// editando o creando
			productExistenceGrid.setVisible(true);
			codeTextBox.setValue(currentProduct.getCode());
			nameTextBox.setValue(currentProduct.getName());
			int stock = 0;
			int stock_min = 0;
			int stock_repo = 0;
			currentProductExistence = productExistenceService.getProductExistence(currentProduct.getId());
			if(currentProductExistence != null) {
				stock = currentProductExistence.getStock();
				stock_min = currentProductExistence.getStockMin();
				stock_repo = currentProductExistence.getStockRepo();
			} else {
				currentProductExistence = new ProductExistence(null, 0, 0, 0);// no existe, se crea uno con id nulo para saber que hacer al guardar
			}
			stockIntbox.setValue(stock);
			stockMinIntbox.setValue(stock_min);
			stockRepoIntbox.setValue(stock_repo);
			saveButton.setDisabled(false);
			cancelButton.setDisabled(false);
			resetButton.setDisabled(false);
			newProvisionOrderButton.setDisabled(true);
		}
    }
	
	@Listen("onClick = #saveButton")
    public void saveButtonClick() {
		currentProductExistence.setStock(stockIntbox.intValue());
		currentProductExistence.setStockMin(stockMinIntbox.intValue());
		currentProductExistence.setStockRepo(stockRepoIntbox.intValue());
        if(currentProductExistence.getIdProduct() == null) {
        	// es nuevo
        	currentProductExistence.setIdProduct(currentProduct.getId());
        	currentProductExistence = productExistenceService.saveProductExistence(currentProductExistence);
        } else {
            // es una edicion
        	currentProductExistence = productExistenceService.updateProductExistence(currentProductExistence);
        }
        productList = productService.getProductList();
        productListModel = new ListModelList<Product>(productList);
        currentProduct = null;
        refreshView();
    }
	
	public String getProductStock(int idProduct) {
		Integer value = 0;
		ProductExistence aux = productExistenceService.getProductExistence(idProduct);
		if(aux != null) {
			value = aux.getStock();
		}
    	return value + "";
    }
	
	public String getProductStockMin(int idProduct) {
		Integer value = 0;
		ProductExistence aux = productExistenceService.getProductExistence(idProduct);
		if(aux != null) {
			value = aux.getStockMin();
		}
    	return value + "";
    }
	
	public String getProductStockRepo(int idProduct) {
		Integer value = 0;
		ProductExistence aux = productExistenceService.getProductExistence(idProduct);
		if(aux != null) {
			value = aux.getStockRepo();
		}
    	return value + "";
    }
	
	// creacion de pedido de auto abastecimiento
	@Listen("onClick = #newProvisionOrderButton")
    public void newProvisionOrder() {
		Integer order_client_id = null;// deberia ser el id del cliente auto abastecimiento
		Integer order_number = null;
		Date order_date = new Date();
		Date order_need_date = null;
		currentOrder = new Order(null, order_client_id, order_number, order_date, order_need_date);
		
		refreshViewOrder();
    }
	
	@Listen("onClick = #resetOrderButton")
    public void resetOrder() {
		refreshViewOrder();
    }
	
	@Listen("onClick = #cancelOrderButton")
    public void cancelOrder() {
		currentOrder = null;
		refreshViewOrder();
    }
	
	@Listen("onClick = #saveOrderButton")
    public void saveOrder() {
		for(OrderDetail each:orderDetailList) {
			System.out.println("prod: " + each.getIdProduct() + ", units: " + each.getUnits());
			// por lo visto no se modifican los elementos de la lista
		}
    }
	
	private void refreshViewOrder() {
		if(currentOrder == null) {
			orderCreationBlock.setVisible(false);
			orderDetailList = null;
		} else {
			orderCreationBlock.setVisible(true);
			orderNumber.setValue(currentOrder.getNumber());
			orderNeedDateBox.setValue(null);
			
			orderDetailList = new ArrayList<OrderDetail>();
			List<ProductExistence> list_product_existence_complete = productExistenceService.getProductExistenceList();
			for(ProductExistence each:list_product_existence_complete) {
				int units_to_repo = each.getStockRepo() - each.getStock();// si esta resta da un valor positivo quiere decir que el valor de reposicion esta por arriba del stock, por lo tanto ese valor es el necesario
				if(units_to_repo > 0) {
					orderDetailList.add(new OrderDetail(null, each.getIdProduct(), units_to_repo, new BigDecimal("0")));
				}
			}
	        orderDetailListModel = new ListModelList<OrderDetail>(orderDetailList);
	        orderDetailGrid.setModel(orderDetailListModel);
		}
		
	}
	
	public String getProductName(int idProduct) {
		Product aux = productService.getProduct(idProduct);
		return aux.getName();
    }
	
	public String getProductCode(int idProduct) {
		Product aux = productService.getProduct(idProduct);
		return aux.getCode();
    }
	
}
