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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Include;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;

import ar.edu.utn.sigmaproject.domain.Item;
import ar.edu.utn.sigmaproject.domain.MaterialsOrder;
import ar.edu.utn.sigmaproject.domain.MaterialsOrderDetail;
import ar.edu.utn.sigmaproject.service.MaterialsOrderRepository;
import ar.edu.utn.sigmaproject.service.SupplyTypeRepository;
import ar.edu.utn.sigmaproject.service.WoodRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class MaterialsOrderCreationController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Caption materialsOrderCaption;
	@Wire
	Listbox itemPopupListbox;
	@Wire
	Listbox materialsOrderDetailListbox;
	@Wire
	Bandbox materialsBandbox;
	@Wire
	Intbox materialsOrderNumberIntbox;
	@Wire
	Datebox materialsOrderDatebox;
	@Wire
	Intbox materialUnitsIntbox;
	@Wire
	Button resetButton;
	@Wire
	Button saveButton;
	@Wire
	Button deleteButton;
	@Wire
	Button cancelDetailButton;
	@Wire
	Button saveDetailButton;
	@Wire
	Button resetDetailButton;
	@Wire
	Button returnButton;

	// services
	@WireVariable
	private SupplyTypeRepository supplyTypeRepository;
	@WireVariable
	private WoodRepository woodRepository;
	@WireVariable
	private MaterialsOrderRepository materialsOrderRepository;

	// attributes
	private MaterialsOrder currentMaterialsOrder;
	private MaterialsOrderDetail currentMaterialsOrderDetail;
	private Item currentItem;

	// list
	private List<Item> itemPopupList;
	private List<MaterialsOrderDetail> materialsOrderDetailList;

	// list models
	private ListModelList<Item> itemPopupListModel;
	private ListModelList<MaterialsOrderDetail> materialsOrderDetailListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		materialsOrderDetailList = new ArrayList<>();
		materialsOrderDetailListModel = new ListModelList<>(materialsOrderDetailList);
		materialsOrderDetailListbox.setModel(materialsOrderDetailListModel);
		currentMaterialsOrder = (MaterialsOrder) Executions.getCurrent().getAttribute("selected_materials_order");
		currentMaterialsOrderDetail = null;
		currentItem = null;
		refreshViewMaterialsOrder();
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(materialsOrderDetailList.isEmpty()) {
			Clients.showNotification("Debe agregar como minimo 1 material al pedido", materialsBandbox);
			return;
		}
		int materialsOrderNumber = materialsOrderNumberIntbox.intValue();
		Date materialsOrderDate = materialsOrderDatebox.getValue();
		if(currentMaterialsOrder == null) { // es un pedido nuevo
			// creamos el nuevo pedido
			currentMaterialsOrder = new MaterialsOrder(materialsOrderNumber, materialsOrderDate);
		} else { // se edita un pedido
			currentMaterialsOrder.setNumber(materialsOrderNumber);
		}
		for(MaterialsOrderDetail each : materialsOrderDetailList) {
			// si es un pedido nuevo hay que asignarle a todos los detalles la referencia
			if(currentMaterialsOrder.getId() == null) {
				each.setMaterialsOrder(currentMaterialsOrder);
			}
			//each = materialsOrderDetailRepository.save(each);
		}
		currentMaterialsOrder.setDetails(materialsOrderDetailList);
		currentMaterialsOrder = materialsOrderRepository.save(currentMaterialsOrder);
		refreshViewMaterialsOrder();
		alert("Pedido guardado.");
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshViewMaterialsOrder();
	}

	@Listen("onClick = #cancelDetailButton")
	public void cancelDetailButtonClick() {
		currentMaterialsOrderDetail = null;
		refreshViewDetail();
	}

	@Listen("onSelect = #itemPopupListbox")
	public void selectionItemPopupListbox() {
		currentItem = (Item) itemPopupListbox.getSelectedItem().getValue();
		materialsBandbox.setValue(currentItem.getDescription());
		materialsBandbox.close();
		materialUnitsIntbox.setFocus(true);
	}

	@Listen("onOK = #materialUnitsIntbox")
	public void materialUnitsIntboxOnOK() {
		// se ejecuta al presionar la tecla enter dentro del Intbox
		saveDetailButtonClick();
	}

	@Listen("onClick = #saveDetailButton")
	public void saveDetailButtonClick() {
		if(materialUnitsIntbox.getValue()==null || materialUnitsIntbox.getValue().intValue()<=0){
			Clients.showNotification("Ingresar Cantidad del Material", materialUnitsIntbox);
			return;
		}
		if(currentItem == null) {
			Clients.showNotification("Debe seleccionar un Material", materialsBandbox);
			return;
		}
		int materialUnits = materialUnitsIntbox.getValue();
		if(currentMaterialsOrderDetail == null) { // es un detalle nuevo
			currentMaterialsOrderDetail = new MaterialsOrderDetail(currentMaterialsOrder, currentItem, currentItem.getDescription(), new BigDecimal(materialUnits));
			materialsOrderDetailList.add(currentMaterialsOrderDetail);
		} else { // se edita un detalle
			currentMaterialsOrderDetail.setItem(currentItem);
			currentMaterialsOrderDetail.setQuantity(new BigDecimal(materialUnits));
		}
		refreshItemPopupList();// actualizamos el popup
		currentMaterialsOrderDetail = null;
		refreshViewDetail();
	}

	private void refreshItemPopupList() {// el popup se actualiza en base a los detalles del pedido
		itemPopupList = new ArrayList<>();
		itemPopupList.addAll(supplyTypeRepository.findAll());
		itemPopupList.addAll(woodRepository.findAll());
		for(MaterialsOrderDetail materialsOrderDetail : materialsOrderDetailList) {
			itemPopupList.remove(materialsOrderDetail.getItem());
		}
		itemPopupListModel = new ListModelList<>(itemPopupList);
		itemPopupListbox.setModel(itemPopupListModel);
		materialsBandbox.setValue(null);
	}

	private void refreshDetailListbox() {
		materialsOrderDetailListModel = new ListModelList<>(materialsOrderDetailList);
		materialsOrderDetailListbox.setModel(materialsOrderDetailListModel);// actualizamos la vista del order detail
	}

	private void refreshViewMaterialsOrder() {
		if (currentMaterialsOrder == null) {// nuevo pedido
			materialsOrderCaption.setLabel("Creacion de Pedido de Materiales");
			materialsOrderNumberIntbox.setValue(getNewOrderNumber());
			materialsOrderDatebox.setValue(new Date());
			materialsOrderDetailList = new ArrayList<>();
			deleteButton.setDisabled(true);
		} else {// editar pedido
			materialsOrderCaption.setLabel("Edicion de Pedido de Materiales");
			materialsOrderNumberIntbox.setValue(currentMaterialsOrder.getNumber());
			materialsOrderDatebox.setValue(currentMaterialsOrder.getDate());
			materialsOrderDetailList = currentMaterialsOrder.getDetails();
			// TODO si el pedido ya esta recibido se deshabilita todas las modificaciones
		}
		materialsOrderDatebox.setDisabled(true);// nunca se debe poder modificar la fecha de creacion del pedido
		currentMaterialsOrderDetail = null;
		refreshItemPopupList();
		refreshViewDetail();
		
	}

	private Integer getNewOrderNumber() {
		Integer lastValue = 0;
		List<MaterialsOrder> list = materialsOrderRepository.findAll();
		for(MaterialsOrder each : list) {
			if(each.getNumber() > lastValue) {
				lastValue = each.getNumber();
			}
		}
		return lastValue + 1;
	}

	private void refreshViewDetail() {
		if (currentMaterialsOrderDetail == null) {
			// borramos el text del producto  seleccionado
			// deseleccionamos la tabla y borramos la cantidad
			materialsBandbox.setDisabled(false);
			materialsBandbox.setValue("");
			materialUnitsIntbox.setValue(null);
			currentItem = null;
			cancelDetailButton.setDisabled(true);
		} else {
			currentItem = currentMaterialsOrderDetail.getItem();
			materialsBandbox.setDisabled(true);// no se permite modificar el material, solo las unidades
			materialsBandbox.setValue(currentMaterialsOrderDetail.getDescription());
			materialUnitsIntbox.setValue(currentMaterialsOrderDetail.getQuantity().intValue());
			cancelDetailButton.setDisabled(false);
		}
		itemPopupListbox.clearSelection();
		refreshDetailListbox();
	}

	@Listen("onSelect = #materialsOrderDetailListbox")
	public void selectDetail() { // se selecciona un detalle de pedido
		if(materialsOrderDetailListModel.isSelectionEmpty()) {
			//just in case for the no selection
			currentMaterialsOrderDetail = null;
		} else {
			if(currentMaterialsOrderDetail == null) {// permite la seleccion solo si no existe nada seleccionado
				currentMaterialsOrderDetail = materialsOrderDetailListbox.getSelectedItem().getValue();
				currentItem = currentMaterialsOrderDetail.getItem();
				refreshViewDetail();
			}
		}
		materialsOrderDetailListModel.clearSelection();
	}

	@Listen("onClick = #resetDetailButton")
	public void resetDetailButtonClick() {
		refreshViewDetail();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onRemoveDetail = #materialsOrderDetailListbox")
	public void deleteOrderDetail(ForwardEvent evt) {
		final MaterialsOrderDetail materialsOrderDetail = (MaterialsOrderDetail) evt.getData();
		Messagebox.show("Esta seguro que desea eliminar " + materialsOrderDetail.getDescription() + "?", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
			public void onEvent(Event evt) throws InterruptedException {
				if(evt.getName().equals("onOK")) {
					materialsOrderDetailList.remove(materialsOrderDetail);// quitamos el detalle de la lista
					// eliminamos el detalle si estaba seleccionado
					if(currentMaterialsOrderDetail != null && materialsOrderDetail.equals(currentMaterialsOrderDetail)) {
						currentMaterialsOrderDetail = null;// eliminamos el detalle
					}
					refreshItemPopupList();// actualizamos el popup para que aparezca el producto eliminado del detalle
					refreshViewDetail();
					alert("Detalle eliminado.");
				}
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick = #deleteOrderButton")
	public void deleteOrder() {
		if(currentMaterialsOrder != null) {
			Messagebox.show("Esta seguro que desea eliminar el pedido?", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
				public void onEvent(Event evt) throws InterruptedException {
					if(evt.getName().equals("onOK")) {
						materialsOrderRepository.delete(currentMaterialsOrder);// quitamos el detalle de la lista
						currentMaterialsOrder = null;
						refreshViewMaterialsOrder();
						alert("Pedido eliminado.");
					}
				}
			});
		}
	}

	private void filterItems() {
		List<Item> someItems = new ArrayList<>();
		String textFilter = materialsBandbox.getValue().toLowerCase();
		for(Item each : itemPopupList) {
			if(each.getDescription().toLowerCase().contains(textFilter) || textFilter.equals("")) {
				someItems.add(each);
			}
		}
		itemPopupListModel = new ListModelList<>(someItems);
		itemPopupListbox.setModel(itemPopupListModel);
	}

	@Listen("onChanging = #materialsBandbox")
	public void changeFilter(InputEvent event) {
		if(currentItem != null) {
			materialUnitsIntbox.setValue(null);
			currentItem = null;
		}
		Bandbox target = (Bandbox)event.getTarget();
		target.setText(event.getValue());
		filterItems();
	}

	@Listen("onClick = #returnButton")
	public void returnButtonClick() {
		Include include = (Include) Selectors.iterable(this.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/materials_order_list.zul");
	}
}
