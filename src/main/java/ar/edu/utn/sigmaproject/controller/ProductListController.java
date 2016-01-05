package ar.edu.utn.sigmaproject.controller;

import java.util.List;

import javax.xml.datatype.Duration;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.service.MeasureUnitService;
import ar.edu.utn.sigmaproject.service.PieceService;
import ar.edu.utn.sigmaproject.service.ProcessService;
import ar.edu.utn.sigmaproject.service.ProcessTypeService;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.impl.MeasureUnitServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.PieceServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProcessServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProcessTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductServiceImpl;

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
    
    // services
    private ProductService productService = new ProductServiceImpl();
    private PieceService pieceService = new PieceServiceImpl();
    private ProcessService processService = new ProcessServiceImpl();
    private ProcessTypeService processTypeService = new ProcessTypeServiceImpl();
	private MeasureUnitService measureUnitService = new MeasureUnitServiceImpl();
    
    // atributes
    
    // list
	
	// list models
	private ListModelList<Product> productListModel;
	private ListModelList<Piece> pieceListModel;
	private ListModelList<Process> processListModel;
	//private String query;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        List<Product> productList = productService.getProductList();
        productListModel = new ListModelList<Product>(productList);
        productListbox.setModel(productListModel);
        
        List<Piece> pieceList = pieceService.getPieceList();
        pieceListModel = new ListModelList<Piece>(pieceList);
        pieceListbox.setModel(pieceListModel);
        
        List<Process> processList = processService.getProcessList();
        processListModel = new ListModelList<Process>(processList);
        processListbox.setModel(processListModel);
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
    }
    
    public String getProductName(int idProduct) {
    	return productService.getProduct(idProduct).getName();
    }
    
    public String getProductNameByPieceId(int idPiece) {
        Piece aux = pieceService.getPiece(idPiece);
        return productService.getProduct(aux.getIdProduct()).getName();
    }
    
    public String getPieceName(int idPiece) {
    	Piece aux = pieceService.getPiece(idPiece);
    	return aux.getName();
    }
    
    public String getProcessTypeName(int idProduct) {
    	return processTypeService.getProcessType(idProduct).getName();
    }
    
    public String getFormatedTime(Duration time) {
    	return String.format("Dias: %d Horas: %d Minutos: %d", time.getDays(), time.getHours(), time.getMinutes());
    }
    
    public String getMeasureUnitName(int idMeasureUnit) {
    	if(measureUnitService.getMeasureUnit(idMeasureUnit) != null) {
    		return measureUnitService.getMeasureUnit(idMeasureUnit).getName();
    	} else {
    		return "[Sin Unidad de Medida]";
    	}
    }
}
