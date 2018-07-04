/*
 * The MIT License
 *
 * Copyright (C) 2017 SigmaProject.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package ar.edu.utn.sigmaproject.controller;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.domain.OrderStateType;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.service.OrderRepository;
import ar.edu.utn.sigmaproject.service.OrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProductStockController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Listbox productListbox;
	@Wire
	Grid productExistenceGrid;
	@Wire
	Textbox codeTextbox;
	@Wire
	Textbox nameTextbox;
	@Wire
	Intbox stockIntbox;
	@Wire
	Intbox stockMinIntbox;
	@Wire
	Button saveButton;
	@Wire
	Button cancelButton;
	@Wire
	Button resetButton;
	@Wire
	Button newProvisionOrderButton;
	@Wire
	Component orderCreationBlock;
	@Wire
	Listbox orderDetailListbox;
	@Wire
	Intbox orderNumberIntbox;
	@Wire
	Datebox orderNeedDatebox;

	// services
	@WireVariable
	private OrderRepository orderRepository;
	@WireVariable
	private OrderStateTypeRepository orderStateTypeRepository;
	@WireVariable
	private ProductRepository productRepository;

	// attributes
	private Product currentProduct;
	private Order currentOrder;

	// list
	private List<Product> productList;

	// list models
	private ListModelList<Product> productListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		productList = productRepository.findAll();
		productListModel = new ListModelList<>(productList);
		productListbox.setModel(productListModel);
		currentProduct = null;
		refreshView();
		currentOrder = null;
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
			if(currentProduct == null && currentOrder == null) {// si no hay nada editandose
				currentProduct = productListbox.getSelectedItem().getValue();
				refreshView();
			}
		}
		productListModel.clearSelection();
	}

	private void refreshView() {
		productListModel.clearSelection();
		productListbox.setModel(productListModel);// se actualiza la lista
		codeTextbox.setDisabled(true);
		nameTextbox.setDisabled(true);// no se deben poder modificar
		if(currentProduct == null) {// no editando ni creando
			productExistenceGrid.setVisible(false);
			codeTextbox.setValue(null);
			nameTextbox.setValue(null);
			stockIntbox.setValue(null);
			stockMinIntbox.setValue(null);
			saveButton.setDisabled(true);
			cancelButton.setDisabled(true);
			resetButton.setDisabled(true);
			newProvisionOrderButton.setDisabled(false);
		}else {// editando o creando
			productExistenceGrid.setVisible(true);
			codeTextbox.setValue(currentProduct.getCode());
			nameTextbox.setValue(currentProduct.getName());
			int stock = currentProduct.getStock();
			int stock_min = currentProduct.getStockMin();
			stockIntbox.setValue(stock);
			stockMinIntbox.setValue(stock_min);
			saveButton.setDisabled(false);
			cancelButton.setDisabled(false);
			resetButton.setDisabled(false);
			newProvisionOrderButton.setDisabled(true);
		}
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		currentProduct.setStock(stockIntbox.intValue());
		currentProduct.setStockMin(stockMinIntbox.intValue());
		productRepository.save(currentProduct);
		productList = productRepository.findAll();
		productListModel = new ListModelList<>(productList);
		currentProduct = null;
		refreshView();
	}

	@Listen("onEditOrderDetailUnits = #orderDetailListbox")
	public void doEditOrderDetailUnits(ForwardEvent evt) {
		OrderDetail orderDetail = (OrderDetail) evt.getData();// obtenemos el objeto pasado por parametro
		Spinner spinner = (Spinner) evt.getOrigin().getTarget();// obtenemos el elemento web
		orderDetail.setUnits(spinner.getValue());// cargamos al objeto el valor actualizado del elemento web
	}

	public int getQuantityDelivered(Product product) {
		// suma la cantidad entregada del producto
		// busca en los pedidos entregados los que contienen el producto
		OrderStateType stateType = orderStateTypeRepository.findFirstByName("Entregado");
		List<Order> orderList = orderRepository.findByCurrentStateType(stateType);
		int number = 0;
		for(Order each : orderList) {
			for(OrderDetail eachDetail : each.getDetails()) {
				if(eachDetail.getProduct().equals(product)) {
					number += eachDetail.getUnits();
				}
			}
		}
		return number;
	}
}
