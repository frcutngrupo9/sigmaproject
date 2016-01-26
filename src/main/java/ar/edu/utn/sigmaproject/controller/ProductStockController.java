package ar.edu.utn.sigmaproject.controller;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.service.ProductRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
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
    
    @WireVariable
    private ProductRepository productRepository;
    
    // attributes
    private Product currentProduct;
    
    // list
    private List<Product> productList;
    
    // list models
    private ListModelList<Product> productListModel;
	
	@Override
    public void doAfterCompose(Component comp) throws Exception{
        super.doAfterCompose(comp);
        //SupplyCreationController supplyCreationController = (SupplyCreationController) supplyCreationWindow.getAttribute("supplyCreationWindow$composer"); (codigo que quedo cuando se manejaba una ventana para crear el insumo)
        productList = productRepository.findAll();
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
		}else {// editando o creando
			productExistenceGrid.setVisible(true);
			codeTextBox.setValue(currentProduct.getCode());
			nameTextBox.setValue(currentProduct.getName());
			int stock = currentProduct.getStock();
			int stock_min = currentProduct.getStockMin();
			int stock_repo = currentProduct.getStockRepo();
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
		currentProduct.setStock(stockIntbox.intValue());
		currentProduct.setStockMin(stockMinIntbox.intValue());
		currentProduct.setStockRepo(stockRepoIntbox.intValue());
		productRepository.save(currentProduct);
        productList = productRepository.findAll();
        productListModel = new ListModelList<Product>(productList);
        currentProduct = null;
        refreshView();
    }
}
