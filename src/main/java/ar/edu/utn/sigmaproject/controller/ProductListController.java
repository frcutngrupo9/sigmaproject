package ar.edu.utn.sigmaproject.controller;

import java.util.List;

import javax.xml.datatype.Duration;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductCategory;
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
	//    @Wire
	//    Listbox productListbox;
	//    @Wire
	//    Paging pager;
	@Wire
	Listbox pieceListbox;
	@Wire
	Listbox processListbox;
	@Wire
	Button newProductButton;
	@Wire
	Grid productGrid;
	@Wire
	Radiogroup productCategoryRadiogroup;

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

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		List<Product> productList = productService.getProductList();
		productListModel = new ListModelList<Product>(productList);
		productGrid.setModel(productListModel);
		//        productListbox.setModel(productListModel);
		List<Piece> pieceList = pieceService.getCompletePieceList();
		pieceListModel = new ListModelList<Piece>(pieceList);
		pieceListbox.setModel(pieceListModel);
		List<Process> processList = processService.getCompleteProcessList();
		processListModel = new ListModelList<Process>(processList);
		processListbox.setModel(processListModel);

	}

	//    @Listen("onSelect = #productListbox")
	//    public void onProductSelect() {
	//    	//Clients.showNotification("Usted hizo click en el Producto: " + ((Product) productListbox.getSelectedItem().getValue()).getName());
	//        Executions.getCurrent().setAttribute("selected_product", ((Product) productListbox.getSelectedItem().getValue()));
	//        Include include = (Include) Selectors.iterable(productListbox.getPage(), "#mainInclude").iterator().next();
	//    	include.setSrc("/product_creation.zul");
	//    }

	@Listen("onClick = #newProductButton")
	public void goToNewProduct() {
		Executions.getCurrent().setAttribute("selected_product", null);
		Include include = (Include) Selectors.iterable(productGrid.getPage(), "#mainInclude").iterator().next();
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

	@Listen("onEditProduct = #productGrid")
	public void doEditProduct(ForwardEvent evt) {
		Product product = (Product) evt.getData();
		Executions.getCurrent().setAttribute("selected_product", product);
		Include include = (Include) Selectors.iterable(evt.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/product_creation.zul");
	}

	public ListModel<Piece> getProductPieces(int idProduct) {
		return new ListModelList<Piece>(pieceService.getPieceList(idProduct));
	}

	public ListModel<Process> getPieceProcesses(int idPiece) {
		return new ListModelList<Process>(processService.getProcessList(idPiece));
	}

	@Listen("onCheck = #productCategoryRadiogroup")
	public void selectCategory(CheckEvent event) {
		String selectedProductCategoryString = ((Radio) event.getTarget()).getLabel();
		ProductCategory selectedProductCategory = null;
		if(selectedProductCategoryString.equals("Armario")) {
			selectedProductCategory = ProductCategory.Armario;
		}
		if(selectedProductCategoryString.equals("Biblioteca")) {
			selectedProductCategory = ProductCategory.Biblioteca;
		}
		if(selectedProductCategoryString.equals("Comoda")) {
			selectedProductCategory = ProductCategory.Comoda;
		}
		if(selectedProductCategoryString.equals("Cajonera")) {
			selectedProductCategory = ProductCategory.Cajonera;
		}
		if(selectedProductCategoryString.equals("Cama")) {
			selectedProductCategory = ProductCategory.Cama;
		}
		if(selectedProductCategoryString.equals("Mesa")) {
			selectedProductCategory = ProductCategory.Mesa;
		}
		if(selectedProductCategoryString.equals("Silla")) {
			selectedProductCategory = ProductCategory.Silla;
		}
		if(selectedProductCategoryString.equals("Sillon")) {
			selectedProductCategory = ProductCategory.Sillon;
		}
		if (selectedProductCategoryString.equals("Todas")) {
			productListModel = new ListModelList<Product>(productService.getProductList());
		} else {
			productListModel = new ListModelList<Product>(productService.getProductListByCategory(selectedProductCategory));
		}
		productGrid.setModel(productListModel);
	}
}
