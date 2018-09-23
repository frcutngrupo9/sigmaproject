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

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.service.ProductRepository;

public class ProductFilterController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Window productFilterWindow;
	@Wire
	Button acceptButton;
	@Wire
	Button cancelButton;
	@Wire
	Listbox chosenListbox;
	@Wire
	Listbox candidateListbox;
	@Wire
	Button removeAllButton;
	@Wire
	Button chooseAllButton;

	@WireVariable
	private ProductRepository productRepository;

	private List<Product> chosenProductList;
	private List<Product> candidateProductList;
	private List<Product> possibleProductList;

	@SuppressWarnings("unchecked")
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		List<Product> possibleProductListParameter = (List<Product>) Executions.getCurrent().getAttribute("possibleProductList");
		if(possibleProductListParameter != null) {
			possibleProductList = possibleProductListParameter;
		} else {
			possibleProductList = productRepository.findAll();
		}
		candidateProductList = possibleProductList;
		chosenProductList = new ArrayList<Product>();
		List<Product> chosenProductListAttribute = (List<Product>) Executions.getCurrent().getAttribute("filterProductList");
		if(chosenProductListAttribute != null) {
			chosenProductList = chosenProductListAttribute;
			candidateProductList = removeProducts(chosenProductListAttribute);
		}
		refreshView();
	}

	private List<Product> removeProducts(List<Product> chosenProductListAttribute) {
		List<Product> list = possibleProductList;
		for(Product each : chosenProductListAttribute) {
			list.remove(each);
		}
		return list;
	}

	private void refreshView() {
		chosenListbox.setModel(new ListModelList<>(chosenProductList));
		candidateListbox.setModel(new ListModelList<>(candidateProductList));
	}

	@Listen("onClick = #removeAllButton")
	public void removeAllButtonOnClick() {
		if(!chosenProductList.isEmpty()) {
			candidateProductList = possibleProductList;
			chosenProductList = new ArrayList<Product>();
			refreshView();
		}
	}

	@Listen("onClick = #chooseAllButton")
	public void chooseAllButtonOnClick() {
		chosenProductList = possibleProductList;
		candidateProductList = new ArrayList<Product>();
		refreshView();
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonOnClick() {
		productFilterWindow.detach();
	}

	@Listen("onSelect = #candidateListbox")
	public void candidateListboxOnSelect() {
		Object selectedItemValue = candidateListbox.getSelectedItem().getValue();
		if(selectedItemValue != null) {
			Product currentProduct = (Product) selectedItemValue;
			chosenProductList.add(currentProduct);
			candidateProductList.remove(currentProduct);
			refreshView();
		}
	}

	@Listen("onSelect = #chosenListbox")
	public void chosenListboxOnSelect() {
		Object selectedItemValue = chosenListbox.getSelectedItem().getValue();
		if(selectedItemValue != null) {
			Product currentProduct = (Product) selectedItemValue;
			candidateProductList.add(0, currentProduct);
			chosenProductList.remove(currentProduct);
			refreshView();
		}
	}
	
	@Listen("onClick = #acceptButton")
	public void acceptButtonOnClick() {
		if(chosenProductList.isEmpty()) {
			Clients.alert("Debe seleccionar al menos 1 producto.");
			return;
		}
		// envia la lista a la ventana principal
		EventQueue<Event> eq = EventQueues.lookup("Product Filter Change Queue", EventQueues.DESKTOP, true);
		eq.publish(new Event("onChosenProductListChange", null, chosenProductList));
		productFilterWindow.detach();
	}
}
