package ar.edu.utn.sigmaproject.controller;

import java.util.List;

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductExistence;
import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.service.ProductExistenceService;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.impl.ProductExistenceServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductServiceImpl;

public class ProductStockController extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;
	
	@Wire
    Textbox searchTextbox;
	@Wire
    Listbox productListbox;
	@Wire
    Button newButton;
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
    
    // services
    private ProductExistenceService productExistenceService = new ProductExistenceServiceImpl();
    private ProductService productService = new ProductServiceImpl();
    
    // atributes
    private ProductExistence currentProductExistence;
    private Product currentProduct;
    
    // list
    private List<Product> productList;
    
    // list models
    private ListModelList<Product> productListModel;
	
	@Override
    public void doAfterCompose(Component comp) throws Exception{
        super.doAfterCompose(comp);
        //SupplyCreationController supplyCreationController = (SupplyCreationController) supplyCreationWindow.getAttribute("supplyCreationWindow$composer"); (codigo que quedo cuando se manejaba una ventana para crear el insumo)
        productList = productService.getProductList();
        productListModel = new ListModelList<Product>(productList);
        productListbox.setModel(productListModel);
        currentProduct = null;
        
        refreshView();
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
			newButton.setDisabled(false);
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
			newButton.setDisabled(true);
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
}
