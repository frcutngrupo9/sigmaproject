package ar.edu.utn.sigmaproject.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.MeasureUnit;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductCategory;
import ar.edu.utn.sigmaproject.service.ProductCategoryRepository;
import ar.edu.utn.sigmaproject.service.ProductRepository;
import ar.edu.utn.sigmaproject.util.RepositoryHelper;
import ar.edu.utn.sigmaproject.util.SortingPagingHelper;
import ar.edu.utn.sigmaproject.util.SortingPagingHelperDelegate;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProductListController extends SelectorComposer<Component> implements SortingPagingHelperDelegate<Product> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Button searchButton;
	@Wire
	Grid productGrid;
	@Wire
	Paging pager;
	@Wire
	Radiogroup productCategoryRadiogroup;

	// services
	@WireVariable
	private ProductRepository productRepository;
	@WireVariable
	private ProductCategoryRepository productCategoryRepository;

	// atributes
	private ProductCategory allProductCategory;
	private ProductCategory selectedProductCategory;
	private static final int PRODUCT_CATEGORY_ALL_INDEX = 0;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		Map<Integer, String> sortProperties = new HashMap<>();
		sortProperties.put(0, "name");
		new SortingPagingHelper<>(productRepository, productGrid, searchButton, searchTextbox, pager, sortProperties, this);

		List<ProductCategory> productCategoryList = productCategoryRepository.findAll();
		if(productCategoryList.isEmpty()) {
			new RepositoryHelper().generateProductCategory(productCategoryRepository);
			productCategoryList = productCategoryRepository.findAll();
		}
		new ListModelList<>(productCategoryList);
	}

	@Listen("onClick = #newProductButton")
	public void goToNewProduct() {
		Executions.getCurrent().setAttribute("selected_product", null);
		Include include = (Include) Selectors.iterable(productGrid.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/product_creation.zul");
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

	@Override
	public Page<Product> getPage(PageRequest pageRequest) {
		Page<Product> page;
		if (searchTextbox.getText().isEmpty()) {
			if (selectedProductCategory == allProductCategory || selectedProductCategory == null) {
				page = productRepository.findAll(pageRequest);
			} else {
				page = productRepository.findAllByCategory(selectedProductCategory, pageRequest);
			}
		} else {
			page = productRepository.findAll(searchTextbox.getText(), pageRequest);
			if (selectedProductCategory != allProductCategory) {
				selectedProductCategory = allProductCategory;
				productCategoryRadiogroup.setSelectedIndex(PRODUCT_CATEGORY_ALL_INDEX);
			}
		}
		return page;
	}
}
