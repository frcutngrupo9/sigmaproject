package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
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
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.Supply;
import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.service.ProductRepository;
import ar.edu.utn.sigmaproject.service.SupplyRepository;
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
	private ProductRepository productRepository;
	@WireVariable
	private SupplyRepository supplyRepository;
	@WireVariable
	private SupplyTypeRepository supplyTypeRepository;

	// attributes
	private Product currentProduct;
	private Supply currentSupply;
	private SupplyType currentSupplyType;

	// list
	private List<Supply> supplyList;
	private List<SupplyType> supplyTypePopupList;

	// list models
	private ListModelList<Supply> supplyListModel;
	private ListModelList<SupplyType> supplyTypePopupListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		
		currentProduct = (Product) Executions.getCurrent().getAttribute("selected_product");
		if(currentProduct == null) {
			alert("Product null");
			supplyList = new ArrayList<>();
		} else {
			supplyList = currentProduct.getSupplies();
		}
		
		currentSupply = null;
		currentSupplyType = null;
		
		refreshSupplyTypePopup();
		refreshViewSupply();
	}
	
	@Listen("onClick = #acceptSupplyListButton")
	public void acceptSupplyListButtonClick() {
		if(currentProduct != null) {
			for(Supply each : supplyList) {
				each = supplyRepository.save(each);
			}
			currentProduct.setSupplies(supplyList);
			currentProduct = productRepository.save(currentProduct);
		}
		EventQueue<Event> eq = EventQueues.lookup("Product Change Queue", EventQueues.DESKTOP, true);
		eq.publish(new Event("onProductChange", null, currentProduct));
		productSupplyWindow.detach();
	}

	@Listen("onClick = #cancelSupplyListButton")
	public void cancelSupplyListButtonClick() {
		productSupplyWindow.detach();
	}
	
	private void refreshViewSupply() {
		supplyListModel = new ListModelList<Supply>(supplyList);
		supplyListbox.setModel(supplyListModel);
		if (currentSupply == null) {
			supplyTypeBandbox.setDisabled(false);
			supplyTypeBandbox.setValue("");
			supplyQuantityDoublebox.setValue(null);
			currentSupplyType = null;
			deleteSupplyButton.setDisabled(true);
			cancelSupplyButton.setDisabled(true);
		} else {
			currentSupplyType = currentSupply.getSupplyType();
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
		for(Supply supply : supplyList) {
			supplyTypePopupList.remove(supply.getSupplyType());// sacamos del popup
		}
		supplyTypePopupListModel = new ListModelList<SupplyType>(supplyTypePopupList);
		supplyTypePopupListbox.setModel(supplyTypePopupListModel);
	}

	@Listen("onSelect = #supplyTypePopupListbox")
	public void selectionSupplyTypePopupListbox() {
		currentSupplyType = supplyTypePopupListbox.getSelectedItem().getValue();
		supplyTypeBandbox.setValue(currentSupplyType.getDescription());
		supplyTypeBandbox.close();
	}

	@Listen("onSelect = #supplyListbox")
	public void selectSupply() {
		if(supplyListModel.isSelectionEmpty()){
			//just in case for the no selection
			currentSupply = null;
		} else {
			if(currentSupply == null) {// permite la seleccion solo si no existe nada seleccionado
				currentSupply = supplyListbox.getSelectedItem().getValue();
				currentSupplyType = currentSupply.getSupplyType();
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
			Messagebox.show("Esta seguro que desea eliminar " + currentSupply.getSupplyType().getDescription() + "?", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
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
			currentSupply = new Supply(currentSupplyType, BigDecimal.valueOf(supplyQuantity));
			supplyList.add(currentSupply);
		} else { // se edita
			currentSupply.setSupplyType(currentSupplyType);;
			currentSupply.setQuantity(BigDecimal.valueOf(supplyQuantity));
		}
		refreshSupplyTypePopup();// actualizamos el popup
		currentSupply = null;
		refreshViewSupply();
	}
}
