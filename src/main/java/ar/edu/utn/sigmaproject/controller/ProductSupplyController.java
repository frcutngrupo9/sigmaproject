package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.MaterialType;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductMaterial;
import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.service.SupplyTypeRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProductSupplyController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Window productSupplyWindow;
	@Wire
	Button acceptSupplyListButton;
	@Wire
	Button cancelSupplyListButton;
	@Wire
	Bandbox supplyTypeBandbox;
	@Wire
	Listbox supplyTypePopupListbox;
	@Wire
	Listbox supplyListbox;
	@Wire
	Doublebox supplyQuantityDoublebox;
	@Wire
	Button saveSupplyButton;
	@Wire
	Button resetSupplyButton;
	@Wire
	Button deleteSupplyButton;
	@Wire
	Button cancelSupplyButton;

	// services
	@WireVariable
	private SupplyTypeRepository supplyTypeRepository;

	// attributes
	private ProductMaterial currentSupply;
	private SupplyType currentSupplyType;
	private Product currentProduct;

	// list
	private List<ProductMaterial> supplyList;
	private List<SupplyType> supplyTypePopupList;

	// list models
	private ListModelList<ProductMaterial> supplyListModel;
	private ListModelList<SupplyType> supplyTypePopupListModel;

	@SuppressWarnings("unchecked")
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		supplyList = (List<ProductMaterial>) Executions.getCurrent().getAttribute("supplyList");
		currentProduct = (Product) Executions.getCurrent().getAttribute("currentProduct");
		currentSupply = null;
		currentSupplyType = null;

		refreshSupplyTypePopup();
		refreshViewSupply();
	}

	@Listen("onClick = #acceptSupplyListButton")
	public void acceptSupplyListButtonClick() {
		EventQueue<Event> eq = EventQueues.lookup("Product Change Queue", EventQueues.DESKTOP, true);
		eq.publish(new Event("onSupplyChange", null, supplyList));
		productSupplyWindow.detach();
	}

	@Listen("onClick = #cancelSupplyListButton")
	public void cancelSupplyListButtonClick() {
		productSupplyWindow.detach();
	}

	private void refreshViewSupply() {
		supplyListModel = new ListModelList<ProductMaterial>(supplyList);
		supplyListbox.setModel(supplyListModel);
		if (currentSupply == null) {
			supplyTypeBandbox.setDisabled(false);
			supplyTypeBandbox.setValue("");
			supplyQuantityDoublebox.setValue(null);
			currentSupplyType = null;
			deleteSupplyButton.setDisabled(true);
			cancelSupplyButton.setDisabled(true);
		} else {
			currentSupplyType = (SupplyType) currentSupply.getItem();
			supplyTypeBandbox.setDisabled(true);// no se permite modificar en la edicion
			supplyTypeBandbox.setValue(currentSupplyType.getDescription());
			supplyQuantityDoublebox.setValue(currentSupply.getQuantity().doubleValue());
			deleteSupplyButton.setDisabled(false);
			cancelSupplyButton.setDisabled(false);
		}
	}

	private void refreshSupplyTypePopup() {// el popup se actualiza en base a la lista
		supplyTypePopupListbox.clearSelection();
		supplyTypePopupList = supplyTypeRepository.findAll();
		for(ProductMaterial supply : supplyList) {
			supplyTypePopupList.remove(supplyTypeRepository.findOne(supply.getItem().getId()));// sacamos del popup
		}
		supplyTypePopupListModel = new ListModelList<SupplyType>(supplyTypePopupList);
		supplyTypePopupListbox.setModel(supplyTypePopupListModel);
	}

	@Listen("onSelect = #supplyTypePopupListbox")
	public void selectionSupplyTypePopupListbox() {
		currentSupplyType = supplyTypePopupListbox.getSelectedItem().getValue();
		supplyTypeBandbox.setValue(currentSupplyType.getDescription());
		supplyTypeBandbox.close();
		supplyQuantityDoublebox.setFocus(true);
	}

	@Listen("onOK = #supplyQuantityDoublebox")
	public void supplyQuantityDoubleboxOnOK() {
		saveSupply();
	}

	@Listen("onSelect = #supplyListbox")
	public void selectSupply() {
		if(supplyListModel.isSelectionEmpty()){
			//just in case for the no selection
			currentSupply = null;
		} else {
			if(currentSupply == null) {// permite la seleccion solo si no existe nada seleccionado
				currentSupply = supplyListbox.getSelectedItem().getValue();
				currentSupplyType = (SupplyType) currentSupply.getItem();
				refreshViewSupply();
			}
		}
		supplyListModel.clearSelection();
	}

	@Listen("onClick = #cancelSupplyButton")
	public void cancelSupply() {
		currentSupply = null;
		refreshViewSupply();
	}

	@Listen("onClick = #resetSupplyButton")
	public void resetSupply() {
		refreshViewSupply();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick = #deleteSupplyButton")
	public void deleteSupply() {
		if(currentSupply != null) {
			Messagebox.show("Esta seguro que desea eliminar " + currentSupply.getItem().getDescription() + "?", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
				public void onEvent(Event evt) throws InterruptedException {
					if (evt.getName().equals("onOK")) {
						supplyList.remove(currentSupply);// quitamos de la lista
						currentSupply = null;// eliminamos
						refreshSupplyTypePopup();// actualizamos el popup para que aparezca el insumo eliminado
						refreshViewSupply();
					}
				}
			});
		} 
	}

	@Listen("onClick = #saveSupplyButton")
	public void saveSupply() {
		if(supplyQuantityDoublebox.getValue()==null || supplyQuantityDoublebox.getValue()<=0) {
			Clients.showNotification("Ingresar Cantidad del Insumo", supplyQuantityDoublebox);
			return;
		}
		if(currentSupplyType == null) {
			Clients.showNotification("Debe seleccionar un Insumo", supplyTypeBandbox);
			return;
		}
		double supplyQuantity = supplyQuantityDoublebox.getValue();
		if(currentSupply == null) { // es nuevo
			currentSupply = new ProductMaterial(currentProduct, MaterialType.Wood, currentSupplyType, BigDecimal.valueOf(supplyQuantity));
			supplyList.add(currentSupply);
		} else { // se edita
			currentSupply.setItem(currentSupplyType);
			currentSupply.setQuantity(BigDecimal.valueOf(supplyQuantity));
		}
		refreshSupplyTypePopup();// actualizamos el popup
		currentSupply = null;
		refreshViewSupply();
	}

	private void filterItems() {
		List<SupplyType> someItems = new ArrayList<>();
		String textFilter = supplyTypeBandbox.getValue().toLowerCase();
		for(SupplyType each : supplyTypePopupList) {
			if(each.getDescription().toLowerCase().contains(textFilter) || textFilter.equals("")) {
				someItems.add(each);
			}
		}
		supplyTypePopupListModel = new ListModelList<>(someItems);
		supplyTypePopupListbox.setModel(supplyTypePopupListModel);
	}

	@Listen("onChanging = #supplyTypeBandbox")
	public void changeFilter(InputEvent event) {
		if(currentSupplyType != null) {
			supplyQuantityDoublebox.setValue(null);
			currentSupplyType = null;
		}
		Textbox target = (Bandbox)event.getTarget();
		target.setText(event.getValue());
		filterItems();
	}
}
