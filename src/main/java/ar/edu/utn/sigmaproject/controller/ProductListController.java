package ar.edu.utn.sigmaproject.controller;

import java.util.List;

import javax.xml.datatype.Duration;

import ar.edu.utn.sigmaproject.domain.*;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.service.PieceRepository;
import ar.edu.utn.sigmaproject.service.ProcessRepository;
import ar.edu.utn.sigmaproject.service.ProductCategoryRepository;
import ar.edu.utn.sigmaproject.service.ProductRepository;
import org.hibernate.Hibernate;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
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
	@WireVariable
	PieceRepository pieceRepository;

	@WireVariable
	ProcessRepository processRepository;

	@WireVariable
	ProductRepository productRepository;

	@WireVariable
	ProductCategoryRepository productCategoryRepository;

	// atributes
	ProductCategory allProductCategory;

	// list

	// list models
	private ListModelList<Product> productListModel;
	private ListModelList<ProductCategory> productCategoryListModel;
	private ListModelList<Piece> pieceListModel;
	private ListModelList<Process> processListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		List<Product> productList = productRepository.findAll();
		productListModel = new ListModelList<>(productList);
		productGrid.setModel(productListModel);
		//        productListbox.setModel(productListModel);
		List<Piece> pieceList = pieceRepository.findAll();
		pieceListModel = new ListModelList<Piece>(pieceList);
		pieceListbox.setModel(pieceListModel);
		List<Process> processList = processRepository.findAll();
		processListModel = new ListModelList<>(processList);
		processListbox.setModel(processListModel);

		List<ProductCategory> productCategoryList = productCategoryRepository.findAll();
		productCategoryListModel = new ListModelList<>(productCategoryList);
		allProductCategory = new ProductCategory("Todas");
		productCategoryListModel.add(0, allProductCategory);
		productCategoryRadiogroup.setModel(productCategoryListModel);
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

	public String getFormatedTime(Duration time) {
		return String.format("Dias: %d Horas: %d Minutos: %d", time.getDays(), time.getHours(), time.getMinutes());
	}

	public String getMeasureUnitName(MeasureUnit measureUnit) {
		if(measureUnit != null) {
			return measureUnit.getName();
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

	@Listen("onCheck = #productCategoryRadiogroup")
	public void selectCategory(CheckEvent event) {
		ProductCategory selectedProductCategory = null;
		if (productCategoryRadiogroup.getSelectedItem() != null) {
			selectedProductCategory = productCategoryRadiogroup.getSelectedItem().getValue();
			String productCategoryName = selectedProductCategory.getName();
			if (selectedProductCategory == allProductCategory) {
				productListModel = new ListModelList<>(productRepository.findAll());
			} else if (!productCategoryName.equals("Ninguna")) {
				// avoid LazyInitializationException
				selectedProductCategory = productCategoryRepository.getOne(selectedProductCategory.getId());
				productListModel = new ListModelList<>(selectedProductCategory.getProducts());
			} else {
				productListModel = new ListModelList<>();
			}
		}
		productGrid.setModel(productListModel);
	}
}
