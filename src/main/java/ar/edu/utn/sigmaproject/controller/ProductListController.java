package ar.edu.utn.sigmaproject.controller;

import javax.xml.datatype.Duration;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Button;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.service.ProductRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
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
    
    @WireVariable
    ProductRepository productService;
	//ProcessTypeService processTypeService = new ProcessTypeServiceImpl();
	
    ListModelList<Product> productListModel;
    ListModelList<Piece> pieceListModel;
    ListModelList<Process> processListModel;
	//private String query;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
//        List<Product> productList = productService.getProductList();
//        productListModel = new ListModelList<Product>(productList);
//        productListbox.setModel(productListModel);
//        
//        List<Piece> pieceList = pieceService.getPieceList();
//        pieceListModel = new ListModelList<Piece>(pieceList);
//        pieceListbox.setModel(pieceListModel);
//        
//        List<Process> processList = processService.getProcessList();
//        processListModel = new ListModelList<Process>(processList);
//        processListbox.setModel(processListModel);
    }
    
    @Listen("onSelect = #productListbox")
    public void onProductSelect() {
    	//Clients.showNotification("Usted hizo click en el Producto: " + ((Product) productListbox.getSelectedItem().getValue()).getName());
        Executions.getCurrent().setAttribute("selected_product", ((Product) productListbox.getSelectedItem().getValue()));
        //productListbox.clearSelection();
        Include include = (Include) Selectors.iterable(productListbox.getPage(), "#mainInclude").iterator().next();
    	include.setSrc("/product_creation.zul");
    }
    
    @Listen("onClick = #searchButton")
    public void search() {
        //query = searchTextbox.getValue();
    }
    
    @Listen("onClick = #newProductButton")
    public void goToNewProduct() {
    	Executions.getCurrent().setAttribute("selected_product", null);
    	Include include = (Include) Selectors.iterable(productListbox.getPage(), "#mainInclude").iterator().next();
    	include.setSrc("/product_creation.zul");
    	//Executions.sendRedirect("/product_creation.zul");
    }
    
    public String getProductName(int idProduct) {
    	return "";
    	//return productService.getProduct(idProduct).getName();
    }
    
    public String getPieceName(int idPiece) {
    	return "";
    	//Piece aux = pieceService.getPiece(idPiece);
    	//return aux.getName();
    }
    
    public String getProcessTypeName(int idProduct) {
    	return "";
    	//return processTypeService.getProcessType(idProduct).getName();
    }
    
    public String getFormatedTime(Duration time) {
    	return String.format("Dias: %d Horas: %d Minutos: %d", time.getDays(), time.getHours(), time.getMinutes());
    }
    
}
