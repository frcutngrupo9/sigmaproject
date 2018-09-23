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

import java.math.BigDecimal;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Item;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductMaterial;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public abstract class ProductMaterialController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Button acceptProductMaterialButton;
	@Wire
	Button cancelProductMaterialButton;
	@Wire
	Bandbox materialBandbox;
	@Wire
	Listbox materialPopupListbox;
	@Wire
	Listbox productMaterialListbox;
	@Wire
	Doublebox materialQuantityDoublebox;
	@Wire
	Button saveMaterialButton;
	@Wire
	Button resetMaterialButton;
	@Wire
	Button deleteMaterialButton;
	@Wire
	Button cancelMaterialButton;
	@Wire
	Label totalCostLabel;

	// attributes
	protected Item currentMaterial;
	protected ProductMaterial currentProductMaterial;
	protected Product currentProduct;

	// list
	protected List<ProductMaterial> productMaterialList;
	protected List<Item> materialPopupList;

	// list models
	protected ListModelList<ProductMaterial> productMaterialListModel;
	protected ListModelList<Item> materialPopupListModel;

	protected abstract void refreshView();
	protected abstract void refreshMaterialPopup();
	protected abstract void filterItems();

	@Listen("onOK = #materialPopupListbox")
	public void materialPopupListboxOK() {
		saveMaterialButtonClick();
	}

	@Listen("onSelect = #productMaterialListbox")
	public void productMaterialListboxSelect() {
		if(productMaterialListModel.isSelectionEmpty()){
			//just in case for the no selection
			currentProductMaterial = null;
		} else {
			if(currentProductMaterial == null) {// permite la seleccion solo si no existe nada seleccionado
				currentProductMaterial = productMaterialListbox.getSelectedItem().getValue();
				currentMaterial = currentProductMaterial.getItem();
				refreshView();
			}
		}
		productMaterialListModel.clearSelection();
	}

	@Listen("onClick = #cancelMaterialButton")
	public void cancelMaterialButtonClick() {
		currentProductMaterial = null;
		refreshView();
	}

	@Listen("onClick = #resetMaterialButton")
	public void resetMaterialButtonClick() {
		refreshView();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick = #deleteMaterialButton")
	public void deleteMaterialButtonClick() {
		if(currentProductMaterial != null) {
			Messagebox.show("Esta seguro que desea eliminar " + currentProductMaterial.getItem().getDescription() + "?", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
				public void onEvent(Event evt) throws InterruptedException {
					if (evt.getName().equals("onOK")) {
						productMaterialList.remove(currentProductMaterial);// quitamos de la lista
						currentProductMaterial = null;// eliminamos
						refreshMaterialPopup();// actualizamos el popup para que aparezca vuelva a aparecer el eliminado
						refreshView();
					}
				}
			});
		} 
	}

	@Listen("onClick = #saveMaterialButton")
	public void saveMaterialButtonClick() {
		if(materialQuantityDoublebox.getValue()==null || materialQuantityDoublebox.getValue()<=0) {
			Clients.showNotification("Ingresar Cantidad del Material", materialQuantityDoublebox);
			return;
		}
		if(currentMaterial == null) {
			Clients.showNotification("Debe seleccionar un Material", materialBandbox);
			return;
		}
		double rawMaterialQuantity = materialQuantityDoublebox.getValue();
		if(currentProductMaterial == null) { // es nuevo
			currentProductMaterial = new ProductMaterial(currentProduct, currentMaterial, BigDecimal.valueOf(rawMaterialQuantity));
			productMaterialList.add(currentProductMaterial);
		} else { // se edita
			currentProductMaterial.setItem(currentMaterial);;
			currentProductMaterial.setQuantity(BigDecimal.valueOf(rawMaterialQuantity));
		}
		refreshMaterialPopup();// actualizamos el popup
		currentProductMaterial = null;
		refreshView();
	}

	@Listen("onChanging = #materialBandbox")
	public void changeFilter(InputEvent event) {
		if(currentMaterial != null) {
			materialQuantityDoublebox.setValue(null);
			currentMaterial = null;
		}
		Textbox target = (Bandbox)event.getTarget();
		target.setText(event.getValue());
		filterItems();
	}

	protected void refreshTotalCostLabel() {
		BigDecimal totalCost = getTotalCost();
		totalCostLabel.setValue(totalCost.doubleValue() + "");
	}

	private BigDecimal getTotalCost() {
		BigDecimal totalCost = new BigDecimal("0");
		for(ProductMaterial each : productMaterialList) {
			if(each.getItem().getPrice() != null) {
				totalCost = totalCost.add(each.getItem().getPrice().multiply(each.getQuantity()));
			}
		}
		return totalCost;
	}
}