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

import ar.edu.utn.sigmaproject.domain.Item;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.StockMovement;
import ar.edu.utn.sigmaproject.domain.StockMovementDetail;
import ar.edu.utn.sigmaproject.domain.StockMovementType;
import ar.edu.utn.sigmaproject.service.SearchableRepository;
import ar.edu.utn.sigmaproject.service.StockMovementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import javax.swing.event.ChangeEvent;
import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public abstract class StockMovementCreationController extends SelectorComposer<Component> {

	@WireVariable
	private StockMovementRepository stockMovementRepository;

	@Wire("#included #stockMovementCaption")
	private Caption stockMovementCaption;

	@Wire("#included #datebox")
	private Datebox datebox;

	@Wire("#included #typeRadiogroup")
	private Radiogroup typeRadiogroup;

	@Wire("#included #detailEditHbox")
	private Hbox detailEditHbox;

	@Wire("#included #itemCombobox")
	private Combobox itemCombobox;

	@Wire("#included #itemQuantityIntbox")
	private Intbox itemQuantityIntbox;

	@Wire("#included #stockMovementListbox")
	private Listbox stockMovementListbox;

	@Wire("#included #saveStockMovementButton")
	private Button saveStockMovementButton;

	@Wire("#included #returnButton")
	private Button returnButton;

	protected StockMovement currentStockMovement;

	private StockMovementDetail selectedDetail;

	protected abstract SearchableRepository<? extends Item, Long> getItemRepository();
	protected abstract StockMovementType getStockMovementType();

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentStockMovement = (StockMovement) Executions.getCurrent().getAttribute("selected_stock_movement");
		if (currentStockMovement == null) {
			currentStockMovement = new StockMovement();
			currentStockMovement.setType(getStockMovementType());
			currentStockMovement.setDate(new Date());
			selectedDetail = new StockMovementDetail();
		}
		setupInterface();
	}

	@Listen("onClick = #included #saveStockMovementDetailButton; onOK = #included #itemQuantityIntbox")
	public void doSaveStockMovementDetailButton() {
		if (selectedDetail.getItem() == null) {
			Clients.showNotification("Seleccione un Item", itemCombobox);
			return;
		}
		if (itemQuantityIntbox.intValue() <= 0) {
			Clients.showNotification("Ingresar Cantidad", itemQuantityIntbox);
			return;
		}
		selectedDetail.setDescription(selectedDetail.getItem().getDescription());
		if (selectedDetail.getStockMovement() == null) {
			currentStockMovement.getDetails().add(selectedDetail);
			selectedDetail.setStockMovement(currentStockMovement);
			for (StockMovementDetail detail : currentStockMovement.getDetails()) {
				if (detail.getItem().equals(selectedDetail.getItem()) && detail != selectedDetail) {
					selectedDetail = detail;
					break;
				}
			}
			selectedDetail.setQuantity(selectedDetail.getQuantity().add(BigDecimal.valueOf(itemQuantityIntbox.getValue())));
		} else {
			selectedDetail.setQuantity(BigDecimal.valueOf(itemQuantityIntbox.getValue()));
		}
		selectedDetail = new StockMovementDetail();
		clearFilteredItems();
		refreshSelectedDetail();
		refreshDetailsList();
		itemCombobox.setFocus(true);
	}

	@Listen("onClick = #included #saveStockMovementButton")
	public void doSave() {
		if (datebox.getValue() == null || datebox.getValue().compareTo(new Date()) > 0) {
			Clients.showNotification("Por favor ingrese una fecha", datebox);
			return;
		}
		if (currentStockMovement.getDetails().size() == 0) {
			Clients.showNotification("Por favor, ingrese al menos un detalle", detailEditHbox);
			return;
		}
		List<StockMovementDetail> invalidDetails = new ArrayList<>();
		if (updateItemsStock(invalidDetails)) {
			stockMovementRepository.save(currentStockMovement);
			setupInterface();
		} else {
			// there are invalid details, show an error on each
			Map<String, Object> arguments = new HashMap<>();
			arguments.put("invalidDetails", new ListModelList<>(invalidDetails));
			String template = "/stock_movement_missing_stock.zul";
			Window window = (Window)Executions.createComponents(template, null, arguments);
			window.doModal();
		}
	}

	/**
	 * Tries to update the stock of the stock movement's details
	 * @return true if successful, false if there was at least one invalid detail
	 */
	protected abstract boolean updateItemsStock(List<StockMovementDetail> invalidDetails);

	public abstract void doReturn();

	@Listen("onClick = #included #clearStockMovementDetailButton")
	public void doClearStockMovementDetailButton() {
		clearSelectedDetailInput();
	}

	@Listen("onClick = #included #cancelStockMovementDetailButton")
	public void doCancelStockMovementDetailButton() {
		selectedDetail = null;
		clearFilteredItems();
		refreshSelectedDetail();
	}

	@Listen("onSelect = #included #stockMovementListbox")
	public void onStockMovementDetailsSelect() {
		if (stockMovementListbox.getSelectedItem() != null) {
			selectedDetail = stockMovementListbox.getSelectedItem().getValue();
			filterItems(selectedDetail.getDescription());
		}
		refreshSelectedDetail();
	}

	@Listen("onRemoveDetail = #included #stockMovementListbox")
	public void doRemoveDetail(ForwardEvent evt) {
		StockMovementDetail stockMovementDetail = (StockMovementDetail) evt.getData();
		currentStockMovement.getDetails().remove(stockMovementDetail);
		if (stockMovementDetail == selectedDetail) {
			selectedDetail = new StockMovementDetail();
			clearFilteredItems();
			refreshSelectedDetail();
		}
		refreshDetailsList();
	}

	@Listen("onCheck = #included #typeRadiogroup")
	public void doChangeStockMovementType() {
		currentStockMovement.setSign((short) (typeRadiogroup.getSelectedItem().getId().equals("in") ? 1 : -1));
	}

	@Listen("onChanging = #included #itemCombobox")
	public void onItemChanging(InputEvent event) {
		filterItems(event.getValue());
	}

	@Listen("onSelect = #included #itemCombobox")
	public void onItemSelect(SelectEvent event) {
		Iterator iterator = event.getSelectedObjects().iterator();
		if (iterator.hasNext()) {
			Item item = (Item)iterator.next();
			selectedDetail.setItem(item);
		}
	}

	@Listen("onOK = #included #itemCombobox")
	public void onItemcomboboxOK() {
		itemQuantityIntbox.setFocus(true);
	}

	protected void setupInterface() {
		boolean isEditable = currentStockMovement.getId() == null;
		saveStockMovementButton.setVisible(isEditable);
		datebox.setDisabled(!isEditable);
		for (Radio radio : typeRadiogroup.getItems()) {
			radio.setDisabled(!isEditable);
		}
		datebox.setValue(currentStockMovement.getDate());
		detailEditHbox.setVisible(isEditable);
		for (Radio radio : typeRadiogroup.getItems()) {
			if (radio.getId().equals("in") && currentStockMovement.getSign() == 1) {
				typeRadiogroup.setSelectedItem(radio);
				break;
			} else if (radio.getId().equals("out") && currentStockMovement.getSign() == -1) {
				typeRadiogroup.setSelectedItem(radio);
				break;
			}
		}
		clearFilteredItems();
		refreshDetailsList();
	}

	protected void filterItems(String searchString) {
		List<Item> someItems = new ArrayList<>();
		String nameFilter = searchString.toLowerCase();
		for(Item each : getItemRepository().findAll()) {
			if(each.getDescription().toLowerCase().contains(nameFilter) || nameFilter.equals("")) {
				someItems.add(each);
			}
		}
		itemCombobox.setModel(new ListModelList<>(someItems));
	}

	protected void clearFilteredItems() {
		itemCombobox.setModel(new ListModelList<>());
	}

	protected void refreshDetailsList() {
		stockMovementListbox.setModel(new ListModelArray<>(currentStockMovement.getDetails()));
	}

	protected void refreshSelectedDetail() {
		if (selectedDetail != null) {
			itemCombobox.setValue(selectedDetail.getDescription());
			if (selectedDetail.getQuantity().intValue() > 0) {
				itemQuantityIntbox.setValue(selectedDetail.getQuantity().intValue());				
			} else {
				itemQuantityIntbox.setText("");
			}
		} else {
			clearSelectedDetailInput();
		}
	}

	protected void clearSelectedDetailInput() {
		itemCombobox.setValue("");
		itemQuantityIntbox.setText("");
		if (selectedDetail != null) {
			selectedDetail.setItem(null);
			selectedDetail.setDescription("");
			selectedDetail.setQuantity(BigDecimal.ZERO);
		}
	}

}
