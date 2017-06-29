package ar.edu.utn.sigmaproject.controller;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.Item;
import ar.edu.utn.sigmaproject.domain.MaterialType;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductMaterial;
import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.service.SupplyTypeRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProductSupplyController extends ProductMaterialController {
	private static final long serialVersionUID = 1L;

	@Wire
	Window productSupplyWindow;

	// services
	@WireVariable
	private SupplyTypeRepository supplyTypeRepository;

	@SuppressWarnings("unchecked")
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		productMaterialList = (List<ProductMaterial>) Executions.getCurrent().getAttribute("supplyList");
		currentProduct = (Product) Executions.getCurrent().getAttribute("currentProduct");
		currentProductMaterial = null;
		currentMaterial = null;
		refreshView();
		refreshMaterialPopup();
	}

	@Listen("onClick = #acceptProductMaterialButton")
	public void acceptProductMaterialButtonClick() {
		EventQueue<Event> eq = EventQueues.lookup("Product Change Queue", EventQueues.DESKTOP, true);
		eq.publish(new Event("onSupplyChange", null, productMaterialList));
		productSupplyWindow.detach();
	}

	@Listen("onClick = #cancelProductMaterialButton")
	public void cancelProductMaterialButtonClick() {
		productSupplyWindow.detach();
	}

	@Listen("onSelect = #materialPopupListbox")
	public void materialPopupListboxSelect() {
		currentMaterial = materialPopupListbox.getSelectedItem().getValue();
		materialBandbox.setValue(currentMaterial.getDescription());
		materialBandbox.close();
		materialQuantityDoublebox.setFocus(true);
	}

	@Override
	protected void refreshView() {
		productMaterialListModel = new ListModelList<ProductMaterial>(productMaterialList);
		productMaterialListbox.setModel(productMaterialListModel);
		if (currentProductMaterial == null) {
			materialBandbox.setDisabled(false);
			materialBandbox.setValue("");
			materialQuantityDoublebox.setValue(null);
			currentMaterial = null;
			deleteMaterialButton.setDisabled(true);
			cancelMaterialButton.setDisabled(true);
		} else {
			currentMaterial = (SupplyType) currentProductMaterial.getItem();
			materialBandbox.setDisabled(true);// no se permite modificar en la edicion
			materialBandbox.setValue(currentMaterial.getDescription());
			materialQuantityDoublebox.setValue(currentProductMaterial.getQuantity().doubleValue());
			deleteMaterialButton.setDisabled(false);
			cancelMaterialButton.setDisabled(false);
		}
	}

	@Override
	protected void refreshMaterialPopup() {// el popup se actualiza en base a la lista
		materialPopupListbox.clearSelection();
		materialPopupList = new ArrayList<Item>();
		materialPopupList.addAll(supplyTypeRepository.findAll());
		for(ProductMaterial supply : productMaterialList) {
			materialPopupList.remove(supplyTypeRepository.findOne(supply.getItem().getId()));// sacamos del popup
		}
		materialPopupListModel = new ListModelList<>(materialPopupList);
		materialPopupListbox.setModel(materialPopupListModel);
	}

	@Override
	protected void filterItems() {
		List<Item> someItems = new ArrayList<>();
		String textFilter = materialBandbox.getValue().toLowerCase();
		for(Item each : materialPopupList) {
			if(each.getDescription().toLowerCase().contains(textFilter) || textFilter.equals("")) {
				someItems.add(each);
			}
		}
		materialPopupListModel = new ListModelList<>(someItems);
		materialPopupListbox.setModel(materialPopupListModel);
	}

	@Override
	protected MaterialType getMaterialType() {
		return MaterialType.Supply;
	}
}
