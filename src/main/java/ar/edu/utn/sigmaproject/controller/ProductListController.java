package ar.edu.utn.sigmaproject.controller;

import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.service.PieceService;
import ar.edu.utn.sigmaproject.service.ProcessService;
import ar.edu.utn.sigmaproject.service.ProcessTypeService;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.impl.PieceServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProcessServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProcessTypeServiceImpl;

//import ar.edu.utn.sigmaproject.util.SortingPagingHelper;
//import java.util.LinkedHashMap;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.zkoss.zk.ui.select.annotation.WireVariable;
//import org.zkoss.zk.ui.event.Event;
//import org.zkoss.zk.ui.event.EventQueues;
//import org.zkoss.zk.ui.select.annotation.VariableResolver;



import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;

import java.util.List;

public class ProductListController extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;
	
	@Wire
    Textbox searchTextbox;
    @Wire
    Listbox productListbox;
    @Wire
    Paging pager;
    @Wire
    Listbox pieceListbox;
    @Wire
    Listbox processListbox;
    @Wire
	Button newProductButton;
    
    ProductService productListService = new ProductServiceImpl();
	PieceService pieceListService = new PieceServiceImpl();
	ProcessService processListService = new ProcessServiceImpl();
	ProcessTypeService processTypeListService = new ProcessTypeServiceImpl();
	
    ListModelList<Product> productListModel;
    ListModelList<Piece> pieceListModel;
    ListModelList<Process> processListModel;
	//private String query;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        List<Product> productList = productListService.getProductList();
        productListModel = new ListModelList<Product>(productList);
        productListbox.setModel(productListModel);
        
        List<Piece> pieceList = pieceListService.getPieceList();
        pieceListModel = new ListModelList<Piece>(pieceList);
        pieceListbox.setModel(pieceListModel);
        
        List<Process> processList = processListService.getProcessList();
        processListModel = new ListModelList<Process>(processList);
        processListbox.setModel(processListModel);
    }
    
    @Listen("onSelect = #productListbox")
    public void onProductSelect() {
    	Clients.showNotification("Usted hizo click en el Producto: " + ((Product) productListbox.getSelectedItem().getValue()).getName());
    	productListbox.clearSelection();
    	//getSelf().detach();
    }
    
    @Listen("onClick = #searchButton")
    public void search() {
        //query = searchTextbox.getValue();
    }
    
    @Listen("onClick = #newProductButton")
    public void goToNewProduct() {
    	Include include = (Include) Selectors.iterable(productListbox.getPage(), "#mainInclude").iterator().next();
    	include.setSrc("/product_creation.zul");
    	//Executions.sendRedirect("/product_creation.zul");
    }
    
    public String getProductName(int idProduct) {
    	return productListService.getProduct(idProduct).getName();
    }
    
    public String getPieceName(int idPiece) {
    	return pieceListService.getPiece(idPiece).getName();
    }
    
    public String getProcessTypeName(int idProduct) {
    	return processTypeListService.getProcessType(idProduct).getName();
    }
    
}
