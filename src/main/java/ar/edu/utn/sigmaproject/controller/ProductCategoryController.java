package ar.edu.utn.sigmaproject.controller;

import ar.edu.utn.sigmaproject.domain.ProductCategory;
import ar.edu.utn.sigmaproject.service.ProductCategoryRepository;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.List;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProductCategoryController extends SelectorComposer<Component> {

	@Wire
	Textbox nameTextbox;

	@Wire
	Grid productCategoryGrid;

	@Wire
	Listbox productCategoryListbox;

	@Wire
	Button newButton;

	@Wire
	Button saveButton;

	@Wire
	Button cancelButton;

	@Wire
	Button resetButton;

	@Wire
	Button deleteButton;

	// services
	@WireVariable
	ProductCategoryRepository productCategoryRepository;

	// attributes
	private ProductCategory currentProductCategory;

	// list
	private List<ProductCategory> productCategoryList;

	// list models
	private ListModelList<ProductCategory> productCategoryListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		productCategoryList = productCategoryRepository.findAll();
		productCategoryListModel = new ListModelList<>(productCategoryList);
		productCategoryListbox.setModel(productCategoryListModel);
		currentProductCategory = null;

		refreshView();
	}

	@Listen("onClick = #searchButton")
	public void search() {

	}

	@Listen("onClick = #newButton")
	public void newButtonClick() {
		currentProductCategory = null;
		refreshView();
		productCategoryGrid.setVisible(true);
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(Strings.isBlank(nameTextbox.getText())){
			Clients.showNotification("Debe ingresar un nombre", nameTextbox);
			return;
		}
		String name = nameTextbox.getText();
		if(currentProductCategory == null) {
			// es un nuevo insumo
			currentProductCategory = new ProductCategory(name);
		} else {
			// es una edicion
			currentProductCategory.setName(name);
		}
		currentProductCategory = productCategoryRepository.save(currentProductCategory);
		productCategoryList = productCategoryRepository.findAll();
		productCategoryListModel = new ListModelList<>(productCategoryList);
		currentProductCategory = null;
		refreshView();
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		currentProductCategory = null;
		refreshView();
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
	}

	@Listen("onClick = #deleteButton")
	public void deleteButtonClick() {
		productCategoryRepository.delete(currentProductCategory);
		productCategoryListModel.remove(currentProductCategory);
		currentProductCategory = null;
		refreshView();
	}

	@Listen("onSelect = #workerListbox")
	public void doListBoxSelect() {
		if(productCategoryListModel.isSelectionEmpty()) {
			//just in case for the no selection
			currentProductCategory = null;
		} else {
			if (currentProductCategory == null) {// si no hay nada editandose
				currentProductCategory = productCategoryListbox.getSelectedItem().getValue();
				refreshView();
			}
		}
		productCategoryListModel.clearSelection();
	}

	private void refreshView() {
		productCategoryListModel.clearSelection();
		productCategoryListbox.setModel(productCategoryListModel);// se actualiza la lista
		saveButton.setDisabled(false);
		cancelButton.setDisabled(false);
		newButton.setDisabled(false);
		if (currentProductCategory == null) {// creando
			productCategoryGrid.setVisible(false);
			nameTextbox.setValue(null);
			deleteButton.setDisabled(true);
			resetButton.setDisabled(true);// al crear, el boton new cumple la misma funcion q el reset
		} else {// editando
			productCategoryGrid.setVisible(true);
			nameTextbox.setValue(currentProductCategory.getName());
			deleteButton.setDisabled(false);
			resetButton.setDisabled(false);
		}
	}

}
