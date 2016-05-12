package ar.edu.utn.sigmaproject.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.Duration;

import ar.edu.utn.sigmaproject.domain.*;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.service.*;
import ar.edu.utn.sigmaproject.util.SortingPagingHelper;
import ar.edu.utn.sigmaproject.util.SortingPagingHelperDelegate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

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
	ProductRepository productRepository;

	@WireVariable
	ProductCategoryRepository productCategoryRepository;

	// atributes
	ProductCategory allProductCategory;
	ProductCategory selectedProductCategory;
	SortingPagingHelper<Product> sortingPagingHelper;

	// list models
	private ListModel<ProductCategory> productCategoryListModel;

	private static final int PRODUCT_CATEGORY_ALL_INDEX = 0;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		Map<Integer, String> sortProperties = new HashMap<>();
		sortProperties.put(0, "name");
		sortingPagingHelper = new SortingPagingHelper<>(productRepository, productGrid, searchButton, searchTextbox, pager, sortProperties, this);

		List<ProductCategory> productCategoryList = productCategoryRepository.findAll();
		productCategoryListModel = new ListModelList<>(productCategoryList);
		allProductCategory = new ProductCategory("Todas");
		((ListModelList)productCategoryListModel).add(PRODUCT_CATEGORY_ALL_INDEX, allProductCategory);
		productCategoryRadiogroup.setModel(productCategoryListModel);
		productCategoryRadiogroup.onInitRender(null);
		productCategoryRadiogroup.setSelectedIndex(PRODUCT_CATEGORY_ALL_INDEX);
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

	@Listen("onCheck = #productCategoryRadiogroup")
	public void selectCategory(CheckEvent event) {
		if (productCategoryRadiogroup.getSelectedItem() != null) {
			selectedProductCategory = productCategoryRadiogroup.getSelectedItem().getValue();
			if (selectedProductCategory != allProductCategory) {
				searchTextbox.setText("");
			}
		}
		sortingPagingHelper.reset();
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
