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

import ar.edu.utn.sigmaproject.domain.MeasureUnit;
import ar.edu.utn.sigmaproject.domain.RawMaterial;
import ar.edu.utn.sigmaproject.domain.Wood;
import ar.edu.utn.sigmaproject.service.WoodRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProductRawMaterialController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Window productRawMaterialWindow;
	@Wire
	Button acceptRawMaterialListButton;
	@Wire
	Button cancelRawMaterialListButton;
	@Wire
	Bandbox rawMaterialTypeBandbox;
	@Wire
	Listbox rawMaterialTypePopupListbox;
	@Wire
	Listbox rawMaterialListbox;
	@Wire
	Doublebox rawMaterialQuantityDoublebox;
	@Wire
	Button saveRawMaterialButton;
	@Wire
	Button resetRawMaterialButton;
	@Wire
	Button deleteRawMaterialButton;
	@Wire
	Button cancelRawMaterialButton;

	// services
	@WireVariable
	private WoodRepository woodRepository;

	// attributes
	private RawMaterial currentRawMaterial;
	private Wood currentWood;

	// list
	private List<RawMaterial> rawMaterialList;
	private List<Wood> rawMaterialTypePopupList;

	// list models
	private ListModelList<RawMaterial> rawMaterialListModel;
	private ListModelList<Wood> rawMaterialTypePopupListModel;

	@SuppressWarnings("unchecked")
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		rawMaterialList = (List<RawMaterial>) Executions.getCurrent().getAttribute("rawMaterialList");

		currentRawMaterial = null;
		currentWood = null;

		refreshViewRawMaterial();
		refreshRawMaterialTypePopup();
	}

	@Listen("onClick = #acceptRawMaterialListButton")
	public void acceptRawMaterialListButtonClick() {
		EventQueue<Event> eq = EventQueues.lookup("Product Change Queue", EventQueues.DESKTOP, true);
		eq.publish(new Event("onRawMaterialChange", null, rawMaterialList));
		productRawMaterialWindow.detach();
	}

	@Listen("onClick = #cancelRawMaterialListButton")
	public void cancelRawMaterialListButtonClick() {
		productRawMaterialWindow.detach();
	}

	private void refreshViewRawMaterial() {
		rawMaterialListModel = new ListModelList<>(rawMaterialList);
		rawMaterialListbox.setModel(rawMaterialListModel);
		if (currentRawMaterial == null) {
			// borramos el text de la materia prima
			// deseleccionamos la tabla y borramos la cantidad
			rawMaterialTypeBandbox.setDisabled(false);
			rawMaterialTypeBandbox.setValue("");
			rawMaterialQuantityDoublebox.setValue(null);
			currentWood = null;
			deleteRawMaterialButton.setDisabled(true);
			cancelRawMaterialButton.setDisabled(true);
		} else {
			currentWood = currentRawMaterial.getWood();
			rawMaterialTypeBandbox.setDisabled(true);// no se permite modificar en la edicion
			rawMaterialTypeBandbox.setValue(currentWood.getName());
			rawMaterialQuantityDoublebox.setValue(currentRawMaterial.getQuantity().doubleValue());
			deleteRawMaterialButton.setDisabled(false);
			cancelRawMaterialButton.setDisabled(false);
		}
	}

	private void refreshRawMaterialTypePopup() {// el popup se actualiza en base a la lista
		rawMaterialTypePopupListbox.clearSelection();
		rawMaterialTypePopupList = woodRepository.findAll();
		for(RawMaterial rawMaterial : rawMaterialList) {
			rawMaterialTypePopupList.remove(woodRepository.findOne(rawMaterial.getWood().getId()));// sacamos del popup
		}
		rawMaterialTypePopupListModel = new ListModelList<>(rawMaterialTypePopupList);
		rawMaterialTypePopupListbox.setModel(rawMaterialTypePopupListModel);
	}

	@Listen("onSelect = #rawMaterialTypePopupListbox")
	public void selectionRawMaterialTypePopupListbox() {
		currentWood = (Wood) rawMaterialTypePopupListbox.getSelectedItem().getValue();
		rawMaterialTypeBandbox.setValue(currentWood.getName());
		rawMaterialTypeBandbox.close();
		rawMaterialQuantityDoublebox.setFocus(true);
	}

	@Listen("onOK = #rawMaterialQuantityDoublebox")
	public void rawMaterialQuantityDoubleboxOnOK() {
		saveRawMaterial();
	}

	@Listen("onSelect = #rawMaterialListbox")
	public void selectRawMaterial() {
		if(rawMaterialListModel.isSelectionEmpty()){
			//just in case for the no selection
			currentRawMaterial = null;
		} else {
			if(currentRawMaterial == null) {// permite la seleccion solo si no existe nada seleccionado
				currentRawMaterial = rawMaterialListbox.getSelectedItem().getValue();
				currentWood = currentRawMaterial.getWood();
				refreshViewRawMaterial();
			}
		}
		rawMaterialListModel.clearSelection();
	}

	@Listen("onClick = #cancelRawMaterialButton")
	public void cancelRawMaterial() {
		currentRawMaterial = null;
		refreshViewRawMaterial();
	}

	@Listen("onClick = #resetRawMaterialButton")
	public void resetRawMaterial() {
		refreshViewRawMaterial();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick = #deleteRawMaterialButton")
	public void deleteRawMaterial() {
		if(currentRawMaterial != null) {
			Messagebox.show("Esta seguro que desea eliminar " + currentRawMaterial.getWood().getName() + "?", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
				public void onEvent(Event evt) throws InterruptedException {
					if (evt.getName().equals("onOK")) {
						rawMaterialList.remove(currentRawMaterial);// quitamos de la lista
						currentRawMaterial = null;// eliminamos
						refreshRawMaterialTypePopup();// actualizamos el popup para que aparezca vuelva a aparecer el eliminado
						refreshViewRawMaterial();
					}
				}
			});
		} 
	}

	@Listen("onClick = #saveRawMaterialButton")
	public void saveRawMaterial() {
		if(rawMaterialQuantityDoublebox.getValue()==null || rawMaterialQuantityDoublebox.getValue()<=0) {
			Clients.showNotification("Ingresar Cantidad de la Materia Prima", rawMaterialQuantityDoublebox);
			return;
		}
		if(currentWood == null) {
			Clients.showNotification("Debe seleccionar una Materia Prima", rawMaterialTypeBandbox);
			return;
		}
		double rawMaterialQuantity = rawMaterialQuantityDoublebox.getValue();
		if(currentRawMaterial == null) { // es nuevo
			currentRawMaterial = new RawMaterial(currentWood, BigDecimal.valueOf(rawMaterialQuantity));
			rawMaterialList.add(currentRawMaterial);
		} else { // se edita
			currentRawMaterial.setWood(currentWood);;
			currentRawMaterial.setQuantity(BigDecimal.valueOf(rawMaterialQuantity));
		}
		refreshRawMaterialTypePopup();// actualizamos el popup
		currentRawMaterial = null;
		refreshViewRawMaterial();
	}

	public String getMeasureUnitName(MeasureUnit measureUnit) {
		if (measureUnit != null) {
			return measureUnit.getName();
		} else {
			return "[Sin Unidad de Medida]";
		}
	}
	
	private void filterItems() {
		List<Wood> someItems = new ArrayList<>();
		String textFilter = rawMaterialTypeBandbox.getValue().toLowerCase();
		for(Wood each : rawMaterialTypePopupList) {
			if((each.getFormattedMeasure()+each.getName()).toLowerCase().contains(textFilter) || textFilter.equals("")) {
				someItems.add(each);
			}
		}
		rawMaterialTypePopupListModel = new ListModelList<>(someItems);
		rawMaterialTypePopupListbox.setModel(rawMaterialTypePopupListModel);
	}

	@Listen("onChanging = #rawMaterialTypeBandbox")
	public void changeFilter(InputEvent event) {
		if(currentWood != null) {
			rawMaterialQuantityDoublebox.setValue(null);
			currentWood = null;
		}
		Textbox target = (Bandbox)event.getTarget();
		target.setText(event.getValue());
		filterItems();
	}
}
