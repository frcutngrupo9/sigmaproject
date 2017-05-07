package ar.edu.utn.sigmaproject.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.zkoss.lang.Strings;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.Item;
import ar.edu.utn.sigmaproject.domain.MaterialsOrder;
import ar.edu.utn.sigmaproject.domain.MaterialsOrderDetail;
import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.domain.Wood;
import ar.edu.utn.sigmaproject.service.MaterialsOrderDetailRepository;
import ar.edu.utn.sigmaproject.service.MaterialsOrderRepository;
import ar.edu.utn.sigmaproject.service.SupplyTypeRepository;
import ar.edu.utn.sigmaproject.service.WoodRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class MaterialsReceptionController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Window materialsOrderDeliveryWindow;
	@Wire
	Listbox materialsOrderDetailListbox;
	@Wire
	Intbox materialsOrderNumberIntbox;
	@Wire
	Textbox materialsOrderCreationDateTextbox;
	@Wire
	Datebox receptionDatebox;
	@Wire
	Textbox receiptNumberTextbox;
	@Wire
	Button saveButton;
	@Wire
	Button cancelButton;
	@Wire
	Button resetButton;

	// services
	@WireVariable
	private MaterialsOrderRepository materialsOrderRepository;
	@WireVariable
	private MaterialsOrderDetailRepository materialsOrderDetailRepository;
	@WireVariable
	private SupplyTypeRepository supplyTypeRepository;
	@WireVariable
	private WoodRepository woodRepository;

	// attributes
	private MaterialsOrder currentMaterialsOrder;

	// list
	private List<MaterialsOrderDetail> materialsOrderDetailList;

	// list models
	private ListModelList<MaterialsOrderDetail> materialsOrderDetailListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentMaterialsOrder = (MaterialsOrder) Executions.getCurrent().getArg().get("selected_materials_order");
		if(currentMaterialsOrder == null) {throw new RuntimeException("Materials Order null");}

		materialsOrderDetailList = currentMaterialsOrder.getDetails();
		materialsOrderDetailListModel = new ListModelList<>(materialsOrderDetailList);

		refreshView();
	}

	private void refreshView() {
		materialsOrderDetailListbox.setModel(materialsOrderDetailListModel);
		materialsOrderNumberIntbox.setDisabled(true);
		materialsOrderNumberIntbox.setValue(currentMaterialsOrder.getNumber());
		materialsOrderCreationDateTextbox.setDisabled(true);
		Date date = currentMaterialsOrder.getDate();
		String dateString = "";
		if(date != null) {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			dateString = dateFormat.format(date);
		}
		materialsOrderCreationDateTextbox.setText(dateString);
		receptionDatebox.setValue(null);
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		materialsOrderDeliveryWindow.detach();
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(receptionDatebox.getValue() == null) {
			Clients.showNotification("Debe seleccionar una fecha de recepcion del pedido.", receptionDatebox);
			return;
		}
		if(Strings.isBlank(receiptNumberTextbox.getValue())) {
			Clients.showNotification("Debe Ingresar Numero de Comprobante", receiptNumberTextbox);
			return;
		}
		Date receptionDate = receptionDatebox.getValue();
		String receiptNumber = receiptNumberTextbox.getText();
		currentMaterialsOrder.setDateReception(receptionDate);
		currentMaterialsOrder = materialsOrderRepository.save(currentMaterialsOrder);

		// modifica la cantidad en stock
		for(MaterialsOrderDetail each : currentMaterialsOrder.getDetails()) {
			Item item = each.getItem();
			if(item instanceof SupplyType) {
				SupplyType supplyType = (SupplyType) item;
				supplyType.setStock(supplyType.getStock().add(each.getQuantity()));
				supplyType = supplyTypeRepository.save(supplyType);
			} else if (item instanceof Wood) {
				Wood wood = (Wood) item;
				wood.setStock(wood.getStock().add(each.getQuantity()));
				wood = woodRepository.save(wood);
			}
		}

		EventQueue<Event> eq = EventQueues.lookup("Materials Reception Queue", EventQueues.DESKTOP, true);
		eq.publish(new Event("onMaterialsReception", null, null));
		alert("Recepcion de Materiales Registrada.");
		materialsOrderDeliveryWindow.detach();
	}
}
