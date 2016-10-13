package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Client;
import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.domain.OrderState;
import ar.edu.utn.sigmaproject.domain.OrderStateType;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.service.ClientRepository;
import ar.edu.utn.sigmaproject.service.OrderDetailRepository;
import ar.edu.utn.sigmaproject.service.OrderRepository;
import ar.edu.utn.sigmaproject.service.OrderStateRepository;
import ar.edu.utn.sigmaproject.service.OrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class OrderCreationController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Listbox productPopupListbox;
	@Wire
	Listbox orderDetailListbox;
	@Wire
	Bandbox productBandbox;
	@Wire
	Bandbox clientBandbox;
	@Wire
	Listbox clientPopupListbox;
	@Wire
	Intbox orderNumberIntbox;
	@Wire
	Datebox orderDatebox;
	@Wire
	Datebox orderNeedDatebox;
	@Wire
	Intbox productUnitsIntbox;
	@Wire
	Doublebox productPriceDoublebox;
	@Wire
	Button resetOrderButton;
	@Wire
	Button saveOrderButton;
	@Wire
	Button deleteOrderButton;
	@Wire
	Button cancelOrderDetailButton;
	@Wire
	Button saveOrderDetailButton;
	@Wire
	Button deleteOrderDetailButton;
	@Wire
	Button resetOrderDetailButton;
	@Wire
	Combobox orderStateTypeCombobox;
	@Wire
	Label orderTotalPriceLabel;
	@Wire
	Caption orderCaption;
	@Wire
	Textbox productNameFilterTextbox;

	// services
	@WireVariable
	private ProductRepository productRepository;
	@WireVariable
	private ClientRepository clientRepository;
	@WireVariable
	private OrderRepository orderRepository;
	@WireVariable
	private OrderDetailRepository orderDetailRepository;
	@WireVariable
	private OrderStateRepository orderStateRepository;
	@WireVariable
	private OrderStateTypeRepository orderStateTypeRepository;

	// attributes
	private Order currentOrder;
	private OrderDetail currentOrderDetail;
	private Product currentProduct;
	private Client currentClient;

	// list
	private List<Client> clientPopupList;
	private List<Product> productPopupList;
	private List<OrderStateType> orderStateTypeList;
	private List<OrderDetail> orderDetailList;

	// list models
	private ListModelList<Product> productPopupListModel;
	private ListModelList<Client> clientPopupListModel;
	private ListModelList<OrderDetail> orderDetailListModel;
	private ListModelList<OrderStateType> orderStateTypeListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		clientPopupList = clientRepository.findAll();
		clientPopupListModel = new ListModelList<Client>(clientPopupList);
		clientPopupListbox.setModel(clientPopupListModel);

		orderDetailList = new ArrayList<OrderDetail>();
		orderDetailListModel = new ListModelList<OrderDetail>(orderDetailList);
		orderDetailListbox.setModel(orderDetailListModel);

		orderStateTypeList = orderStateTypeRepository.findAll();
		orderStateTypeListModel = new ListModelList<OrderStateType>(orderStateTypeList);
		orderStateTypeCombobox.setModel(orderStateTypeListModel);

		currentOrder = (Order) Executions.getCurrent().getAttribute("selected_order");
		currentOrderDetail = null;
		currentProduct = null;
		currentClient = null;
		refreshViewOrder();
	}
	@Listen("onSelect = #clientPopupListbox")
	public void selectionClientPopupListbox() {
		currentClient = (Client) clientPopupListbox.getSelectedItem().getValue();
		clientBandbox.setValue(currentClient.getName());
		clientBandbox.close();
	}

	@Listen("onClick = #saveOrderButton")
	public void saveOrder() {
		// TODO agregar comprobacion para ver si todavia no se guardo un detalle activo
		if(currentClient == null) {
			Clients.showNotification("Seleccionar Cliente", clientBandbox);
			return;
		}

		if(orderDetailList.isEmpty()) {
			Clients.showNotification("Debe agregar como minimo 1 producto al pedido", productBandbox);
			return;
		}
		/*
		if(orderNeedDateBox.getValue() == null){
			Clients.showNotification("Debe seleccionar una fecha de  necesidad", orderNeedDateBox);
			return;
		}*/
		int order_number = orderNumberIntbox.intValue();
		Date order_date = orderDatebox.getValue();
		Date order_need_date = orderNeedDatebox.getValue();
		OrderStateType orderStateType;
		if(orderStateTypeCombobox.getSelectedIndex() != -1) {
			orderStateType = orderStateTypeCombobox.getSelectedItem().getValue();
		} else {
			orderStateType = null;
		}

		if(currentOrder == null) { // es un pedido nuevo
			// creamos el nuevo pedido
			currentOrder = new Order(currentClient, order_number, order_date, order_need_date);
		} else { // se edita un pedido
			currentOrder.setClient(currentClient);
			currentOrder.setNeedDate(order_need_date);
			currentOrder.setNumber(order_number);
		}
		for(OrderDetail each : orderDetailList) {
			each = orderDetailRepository.save(each);
		}
		currentOrder.setDetails(orderDetailList);
		OrderState orderState = new OrderState(orderStateType, new Date());
		orderState = orderStateRepository.save(orderState);
		currentOrder.setState(orderState);
		currentOrder = orderRepository.save(currentOrder);
		refreshViewOrder();
		alert("Pedido guardado.");
	}

	@Listen("onClick = #resetOrderButton")
	public void resetOrder() {
		refreshViewOrder();
	}

	@Listen("onClick = #cancelOrderDetailButton")
	public void cancelOrderDetail() {
		currentOrderDetail = null;
		refreshViewOrderDetail();
	}

	@Listen("onSelect = #productPopupListbox")
	public void selectionProductPopupListbox() {
		currentProduct = (Product) productPopupListbox.getSelectedItem().getValue();
		productBandbox.setValue(currentProduct.getName());
		productBandbox.close();
		BigDecimal product_price = currentProduct.getPrice();
		if(product_price != null) {
			productPriceDoublebox.setValue(currentProduct.getPrice().doubleValue());
		}
		productUnitsIntbox.setFocus(true);
	}

	@Listen("onOK = #productUnitsIntbox")
	public void productUnitsIntboxOnOK() {
		// se ejecuta al presionar la tecla enter dentro del Intbox
		saveOrderDetail();
	}

	@Listen("onClick = #saveOrderDetailButton")
	public void saveOrderDetail() {
		if(productUnitsIntbox.getValue()==null || productUnitsIntbox.getValue().intValue()<=0){
			Clients.showNotification("Ingresar Cantidad del Producto", productUnitsIntbox);
			return;
		}
		if(currentProduct == null) {
			Clients.showNotification("Debe seleccionar un Producto", productBandbox);
			return;
		}
		int productUnits = productUnitsIntbox.getValue();
		BigDecimal productPrice = new BigDecimal(productPriceDoublebox.getValue().doubleValue());
		if(currentOrderDetail == null) { // es un detalle nuevo
			currentOrderDetail = new OrderDetail(currentProduct, productUnits, productPrice);
			orderDetailList.add(currentOrderDetail);
		} else { // se edita un detalle
			currentOrderDetail.setProduct(currentProduct);
			currentOrderDetail.setUnits(productUnits);
			currentOrderDetail.setPrice(productPrice);
		}
		refreshProductPopup();// actualizamos el popup
		currentOrderDetail = null;
		refreshViewOrderDetail();
	}

	private void refreshProductPopup() {// el popup se actualiza en base a los detalles del pedido
		productPopupList = productRepository.findAll();
		for(OrderDetail orderDetail : orderDetailList) {
			productPopupList.remove(orderDetail.getProduct());// sacamos todos los productos del popup
		}
		productPopupListModel = new ListModelList<Product>(productPopupList);
		productPopupListbox.setModel(productPopupListModel);
		productNameFilterTextbox.setValue(null);
	}

	private void refreshOrderDetailListbox() {
		orderDetailListModel = new ListModelList<OrderDetail>(orderDetailList);
		orderDetailListbox.setModel(orderDetailListModel);// actualizamos la vista del order detail
		orderTotalPriceLabel.setValue("Monto Total: " + getTotalPrice().doubleValue() + " $");
	}

	private void refreshViewOrder() {
		if (currentOrder == null) {// nuevo pedido
			orderCaption.setLabel("Creacion de Pedido");
			orderStateTypeListModel.addToSelection(orderStateTypeRepository.findFirstByName("Iniciado"));
			orderStateTypeCombobox.setModel(orderStateTypeListModel);
			currentClient = null;
			clientBandbox.setValue("");
			clientBandbox.close();
			orderNumberIntbox.setValue(getNewOrderNumber());
			orderDatebox.setValue(new Date());
			orderNeedDatebox.setValue(null);
			orderDetailList = new ArrayList<OrderDetail>();
			deleteOrderButton.setDisabled(true);
			orderStateTypeCombobox.setDisabled(true);
		} else {// editar pedido
			orderCaption.setLabel("Edicion de Pedido");
			OrderStateType orderCurrentStateType = currentOrder.getCurrentStateType();
			if (orderCurrentStateType != null) {
				orderStateTypeListModel.addToSelection(orderCurrentStateType);
				orderStateTypeCombobox.setModel(orderStateTypeListModel);
				// solo se puede grabar si esta en estado Iniciado o Cancelado
				OrderStateType stateTypeIniciado = orderStateTypeRepository.findFirstByName("Iniciado");
				OrderStateType stateTypeCancelado = orderStateTypeRepository.findFirstByName("Cancelado");
				if(!orderCurrentStateType.equals(stateTypeIniciado) && !orderCurrentStateType.equals(stateTypeCancelado)) {
					saveOrderButton.setDisabled(true);
					deleteOrderButton.setDisabled(true);
				} else {
					saveOrderButton.setDisabled(false);
					deleteOrderButton.setDisabled(false);
				}
			} else {
				orderStateTypeCombobox.setSelectedIndex(-1);
			}
			currentClient = currentOrder.getClient();
			clientBandbox.setValue(currentClient.getName());
			clientBandbox.close();
			orderNumberIntbox.setValue(currentOrder.getNumber());
			orderDatebox.setValue(currentOrder.getDate());
			orderNeedDatebox.setValue(currentOrder.getNeedDate());
			orderDetailList = currentOrder.getDetails();
			orderStateTypeCombobox.setDisabled(false);
		}
		orderDatebox.setDisabled(true);// nunca se debe poder modificar la fecha de creacion del pedido
		currentOrderDetail = null;
		refreshProductPopup();
		refreshViewOrderDetail();
	}

	private Integer getNewOrderNumber() {
		Integer lastValue = 0;
		List<Order> list = orderRepository.findAll();
		for(Order each : list) {
			if(each.getNumber() > lastValue) {
				lastValue = each.getNumber();
			}
		}
		return lastValue + 1;
	}

	private void refreshViewOrderDetail() {
		if (currentOrderDetail == null) {
			// borramos el text del producto  seleccionado
			// deseleccionamos la tabla y borramos la cantidad
			productBandbox.setDisabled(false);
			productBandbox.setValue("");
			productUnitsIntbox.setValue(null);
			productPriceDoublebox.setValue(null);
			currentProduct = null;
			deleteOrderDetailButton.setDisabled(true);
			cancelOrderDetailButton.setDisabled(true);
		} else {
			currentProduct = currentOrderDetail.getProduct();
			productBandbox.setDisabled(true);// no se permite modificar el producto solo las unidades
			productBandbox.setValue(currentOrderDetail.getProduct().getName());
			productUnitsIntbox.setValue(currentOrderDetail.getUnits());
			if(currentOrderDetail.getPrice() != null) {
				productPriceDoublebox.setValue(currentOrderDetail.getPrice().doubleValue());
			} else {
				productPriceDoublebox.setValue(null);
			}
			deleteOrderDetailButton.setDisabled(false);
			cancelOrderDetailButton.setDisabled(false);
		}
		productPopupListbox.clearSelection();
		refreshOrderDetailListbox();
	}

	public BigDecimal getSubTotal(int units, BigDecimal price) {
		return price.multiply(new BigDecimal(units));
	}

	public BigDecimal getTotalPrice() {
		BigDecimal total_price = new BigDecimal("0");
		if(orderDetailList != null) {
			for(OrderDetail order_detail : orderDetailList) {
				if(order_detail.getPrice() != null) {
					total_price = total_price.add(getSubTotal(order_detail.getUnits(), order_detail.getPrice()));
				}
			}
		}
		return total_price;
	}

	public String getClientName(Client client) {
		return client.getName();
	}

	@Listen("onSelect = #orderDetailListbox")
	public void selectOrderDetail() { // se selecciona un detalle de pedido
		if(orderDetailListModel.isSelectionEmpty()){
			//just in case for the no selection
			currentOrderDetail = null;
		} else {
			if(currentOrderDetail == null) {// permite la seleccion solo si no existe nada seleccionado
				currentOrderDetail = orderDetailListbox.getSelectedItem().getValue();
				currentProduct = currentOrderDetail.getProduct();
				refreshViewOrderDetail();
			}
		}
		orderDetailListModel.clearSelection();
	}

	@Listen("onClick = #resetOrderDetailButton")
	public void resetOrderDetail() {
		refreshViewOrderDetail();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick = #deleteOrderDetailButton")
	public void deleteOrderDetail() {
		if(currentOrderDetail != null) {
			Messagebox.show("Esta seguro que desea eliminar " + currentOrderDetail.getProduct().getName() + "?", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
				public void onEvent(Event evt) throws InterruptedException {
					if (evt.getName().equals("onOK")) {
						orderDetailList.remove(currentOrderDetail);// quitamos el detalle de la lista
						currentOrderDetail = null;// eliminamos el detalle
						refreshProductPopup();// actualizamos el popup para que aparezca el producto eliminado del detalle
						refreshViewOrderDetail();
						alert("Detalle eliminado.");
					}
				}
			});
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick = #deleteOrderButton")
	public void deleteOrder() {
		if(currentOrder != null) {
			Messagebox.show("Esta seguro que desea eliminar el pedido?", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
				public void onEvent(Event evt) throws InterruptedException {
					if (evt.getName().equals("onOK")) {
						orderRepository.delete(currentOrder);// quitamos el detalle de la lista
						currentOrder = null;
						refreshViewOrder();
						alert("Pedido eliminado.");
					}
				}
			});
		}
	}

	private void filterProducts() {
		List<Product> someProducts = new ArrayList<>();
		String nameFilter = productNameFilterTextbox.getValue().toLowerCase();
		for(Product each : productPopupList) {
			if(each.getName().toLowerCase().contains(nameFilter) || nameFilter.equals("")) {
				someProducts.add(each);
			}
		}
		productPopupListModel = new ListModelList<Product>(someProducts);
		productPopupListbox.setModel(productPopupListModel);
	}

	@Listen("onChanging = #productNameFilterTextbox")
	public void changeFilter(InputEvent event) {
		Textbox target = (Textbox)event.getTarget();
		target.setText(event.getValue());
		filterProducts();
	}

}
