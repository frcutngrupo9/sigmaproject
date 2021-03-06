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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Include;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.Client;
import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.domain.OrderState;
import ar.edu.utn.sigmaproject.domain.OrderStateType;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.service.ClientRepository;
import ar.edu.utn.sigmaproject.service.OrderRepository;
import ar.edu.utn.sigmaproject.service.OrderStateRepository;
import ar.edu.utn.sigmaproject.service.OrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;

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
	Button resetOrderDetailButton;
	@Wire
	Combobox orderStateTypeCombobox;
	@Wire
	Label orderTotalPriceLabel;
	@Wire
	Caption orderCaption;
	@Wire
	Button returnButton;
	@Wire
	Button jasperReportButton;
	@Wire
	Button deliveryNoteReportButton;

	// services
	@WireVariable
	private ProductRepository productRepository;
	@WireVariable
	private ClientRepository clientRepository;
	@WireVariable
	private OrderRepository orderRepository;
	@WireVariable
	private OrderStateRepository orderStateRepository;
	@WireVariable
	private OrderStateTypeRepository orderStateTypeRepository;
	@WireVariable
	private ProductionPlanRepository productionPlanRepository;

	// attributes
	@SuppressWarnings("rawtypes")
	private EventQueue eq;
	private Order currentOrder;
	private OrderDetail currentOrderDetail;
	private Product currentProduct;
	private Client currentClient;
	private boolean detailModified;

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

		orderStateTypeList = orderStateTypeRepository.findAll();
		orderStateTypeListModel = new ListModelList<OrderStateType>(orderStateTypeList);
		orderStateTypeCombobox.setModel(orderStateTypeListModel);

		currentOrder = (Order) Executions.getCurrent().getAttribute("selected_order");
		currentOrderDetail = null;
		currentProduct = null;
		currentClient = null;
		createListener();
		refreshViewOrder();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createListener() {
		// listener para cuando se agregue un cliente
		eq = EventQueues.lookup("Client Update Queue", EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener() {
			public void onEvent(Event event) throws Exception {
				if(event.getName().equals("onClientUpdate")) {
					Client clientCreated = (Client) event.getData();
					currentClient = clientCreated;
					clientPopupList = clientRepository.findAll();
					clientPopupListModel = new ListModelList<Client>(clientPopupList);
					clientPopupListbox.setModel(clientPopupListModel);
					clientBandbox.setValue(clientCreated.getName());
					clientBandbox.close();
				}
			}
		});
	}

	@Listen("onClick = #newClientButton")
	public void newClientButtonClick() {
		final Window win = (Window) Executions.createComponents("/client_creation.zul", null, null);
		win.setSizable(false);
		win.setPosition("center");
		win.doModal();
	}

	@Listen("onSelect = #clientPopupListbox")
	public void selectionClientPopupListbox() {
		currentClient = (Client) clientPopupListbox.getSelectedItem().getValue();
		clientBandbox.setValue(currentClient.getName());
		clientBandbox.close();
	}

	@Listen("onClick = #saveOrderButton")
	public void saveOrderButtonClick() {
		// agregar comprobacion para ver si todavia no se guardo un detalle activo
		if(detailModified == true) {
			alert("Existe un detalle sin guardar.");
			return;
		}
		if(currentClient == null) {
			Clients.showNotification("Seleccionar Cliente", clientBandbox);
			return;
		}
		if(orderDetailList.isEmpty()) {
			Clients.showNotification("Debe agregar como minimo 1 producto al pedido", productBandbox);
			return;
		}
		if(orderNeedDatebox.getValue() == null) {
			Clients.showNotification("Debe seleccionar una fecha de necesidad", orderNeedDatebox);
			return;
		}
		int order_number = orderNumberIntbox.intValue();
		Date order_date = orderDatebox.getValue();
		Date order_need_date = orderNeedDatebox.getValue();
		OrderStateType orderStateType;
		if(orderStateTypeCombobox.getSelectedIndex() != -1) {
			orderStateType = orderStateTypeCombobox.getSelectedItem().getValue();
		} else {
			orderStateType = null;
		}
		boolean isDeliverAvailable = true;
		if(currentOrder == null) { // es un pedido nuevo
			// creamos el nuevo pedido
			currentOrder = new Order(currentClient, order_number, order_date, order_need_date);
			// se hace q los detalles referencien al nuevo pedido
			for(OrderDetail orderDetail : orderDetailList) {
				orderDetail.setOrder(currentOrder);
			}
			currentOrder.getDetails().addAll(orderDetailList);
			// si existe stock suficiente para satisfacer el pedido y no hay pedidos finalizados que requieran ese stock, se informa y se realiza la entrega directamente
			for(OrderDetail orderDetail : orderDetailList) {
				if(!existStockAvailable(orderDetail.getProduct(), orderDetail.getUnits())) {
					isDeliverAvailable = false;
					break;
				}
			}
		} else { // se edita un pedido
			isDeliverAvailable = false;
			currentOrder.setClient(currentClient);
			currentOrder.setNeedDate(order_need_date);
			currentOrder.setNumber(order_number);
			currentOrder.setDate(order_date);
		}
		if(isDeliverAvailable) {
			// se pregunta si se quiere entregar el pedido directamente ya que existe stock disponible
			showMessagebox(orderStateType);
		} else {
			saveOrder(orderStateType);
		}
	}
	
	@Listen("onChanging = #productBandbox, #productUnitsIntbox, #productPriceDoublebox")
	public void pieceDetaisOnChanging() {
		detailModified = true;
	}

	private void saveOrder(OrderStateType orderStateType) {
		OrderState orderState = new OrderState(orderStateType, new Date());
		orderState = orderStateRepository.save(orderState);
		currentOrder.setState(orderState);
		currentOrder = orderRepository.save(currentOrder);
		currentOrder = orderRepository.findOne(currentOrder.getId());// se recupera de la bd para que tenga los detalles actualizados
		refreshViewOrder();
		alert("Pedido guardado.");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void showMessagebox(final OrderStateType currentOrderStateType) {
		// pregunta si se quiere asignar igual estando ocupado
		Messagebox.show("Existe stock disponible para realizar la entrega inmediata, desea entregar el stock existente?", "Confirmar", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
			public void onEvent(Event evt) throws InterruptedException {
				if (evt.getName().equals("onOK")) {
					/*
					// modifica la cantidad en stock
					for(OrderDetail each : currentOrder.getDetails()) {
						Product product = each.getProduct();
						product.setStock(product.getStock() - each.getUnits());
						product = productRepository.save(product);
					}
					 */
					saveOrder(orderStateTypeRepository.findFirstByName("Finalizado"));
				} else {
					saveOrder(currentOrderStateType);
				}
			}
		});
	}

	private boolean existStockAvailable(Product product, Integer units) {
		// si para el producto y la cantidad existe stock que no posee un pedido correlativo finalizado, se devuelve verdadero
		int quantityFinished = 0;
		OrderStateType stateType = orderStateTypeRepository.findFirstByName("Finalizado");
		List<Order> orderList = orderRepository.findByCurrentStateType(stateType);
		for(Order each : orderList) {
			for(OrderDetail eachDetail : each.getDetails()) {
				if(eachDetail.getProduct().getId() == product.getId()) {
					quantityFinished += eachDetail.getUnits();
				}
			}
		}
		int stockAvailable = product.getStock() - quantityFinished;
		if(stockAvailable >= units) {
			return true;
		}
		return false;
	}

	@Listen("onClick = #resetOrderButton")
	public void resetOrder() {
		refreshViewOrder();
		filterClients();
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
		saveOrderDetailButtonClick();
	}

	@Listen("onClick = #saveOrderDetailButton")
	public void saveOrderDetailButtonClick() {
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
			currentOrderDetail = new OrderDetail(currentOrder, currentProduct, productUnits, productPrice);
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
			productPopupList.remove(productRepository.findOne(orderDetail.getProduct().getId()));// sacamos todos los productos del popup
		}
		productPopupListModel = new ListModelList<Product>(productPopupList);
		productPopupListbox.setModel(productPopupListModel);
		productBandbox.setValue(null);
	}

	private void refreshOrderDetailListbox() {
		BigDecimal totalPrice = getTotalPrice();
		String totalPriceValue = "";
		if(!totalPrice.equals(new BigDecimal("0"))) {
			totalPriceValue = "Importe Total: " + totalPrice.doubleValue() + " $";
		}
		orderTotalPriceLabel.setValue(totalPriceValue);
		orderDetailListModel = new ListModelList<OrderDetail>(orderDetailList);
		orderDetailListbox.setModel(orderDetailListModel);// actualizamos la vista del order detail
	}

	private void refreshViewOrder() {
		if(currentOrder == null) {// nuevo pedido
			orderCaption.setLabel("Creacion de Pedido");
			orderStateTypeListModel.addToSelection(orderStateTypeRepository.findFirstByName("Creado"));
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
			deliveryNoteReportButton.setDisabled(true);
		} else {// editar pedido
			orderCaption.setLabel("Edicion de Pedido");
			OrderStateType orderCurrentStateType = currentOrder.getCurrentStateType();
			if(orderCurrentStateType != null) {
				orderStateTypeList = orderStateTypeRepository.findAll();
				orderStateTypeListModel = new ListModelList<OrderStateType>(orderStateTypeList);
				orderStateTypeListModel.addToSelection(orderStateTypeRepository.findOne(orderCurrentStateType.getId()));
				orderStateTypeCombobox.setModel(orderStateTypeListModel);
				// solo se puede grabar si esta en estado Creado o Cancelado
				OrderStateType stateTypeCreado = orderStateTypeRepository.findFirstByName("Creado");
				OrderStateType stateTypeCancelado = orderStateTypeRepository.findFirstByName("Cancelado");
				if(!orderStateTypeRepository.findOne(orderCurrentStateType.getId()).equals(stateTypeCreado) && !orderStateTypeRepository.findOne(orderCurrentStateType.getId()).equals(stateTypeCancelado)) {
					saveOrderButton.setDisabled(true);
					deleteOrderButton.setDisabled(true);
				} else {
					saveOrderButton.setDisabled(false);
					deleteOrderButton.setDisabled(false);
				}
				if(orderCurrentStateType.getName().equalsIgnoreCase("Entregado")) {
					deliveryNoteReportButton.setDisabled(false);
				} else {
					deliveryNoteReportButton.setDisabled(true);
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
		orderDatebox.setDisabled(false);// no se debe poder modificar la fecha de creacion del pedido (activado para pruebas)
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
		detailModified = false;
		if(currentOrderDetail == null) {
			// borramos el text del producto  seleccionado
			// deseleccionamos la tabla y borramos la cantidad
			productBandbox.setDisabled(false);
			productBandbox.setValue("");
			productUnitsIntbox.setValue(null);
			productPriceDoublebox.setValue(null);
			currentProduct = null;
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
			cancelOrderDetailButton.setDisabled(false);
		}
		productPopupListbox.clearSelection();
		refreshOrderDetailListbox();
	}

	public BigDecimal getSubTotal(int units, BigDecimal price) {
		return price.multiply(new BigDecimal(units)).setScale(2, RoundingMode.HALF_UP);
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

	@Listen("onSelect = #orderDetailListbox")
	public void selectOrderDetail() { // se selecciona un detalle de pedido
		if(orderDetailListModel.isSelectionEmpty()) {
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
	@Listen("onRemoveDetail = #orderDetailListbox")
	public void deleteOrderDetail(ForwardEvent evt) {
		final OrderDetail orderDetail = (OrderDetail)evt.getData();
		Messagebox.show("Esta seguro que desea eliminar " + orderDetail.getProduct().getName() + "?", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
			public void onEvent(Event evt) throws InterruptedException {
				if(evt.getName().equals("onOK")) {
					orderDetailList.remove(orderDetail);// quitamos el detalle de la lista
					// eliminamos el detalle si estaba seleccionado
					if(currentOrderDetail != null && orderDetail.equals(currentOrderDetail)) {
						currentOrderDetail = null;// eliminamos el detalle
					}
					refreshProductPopup();// actualizamos el popup para que aparezca el producto eliminado del detalle
					refreshViewOrderDetail();
					alert("Detalle eliminado.");
				}
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick = #deleteOrderButton")
	public void deleteOrder() {
		if(currentOrder != null) {
			if(productionPlanRepository.findByPlanDetailsOrder(currentOrder) != null) {
				Messagebox.show("No se puede eliminar, el pedido se encuentra asignado a un plan de produccion.", "Informacion", Messagebox.OK, Messagebox.ERROR);
				return;
			}
			Messagebox.show("Esta seguro que desea eliminar el pedido?", "Confirmar Eliminacion", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
				public void onEvent(Event evt) throws InterruptedException {
					if(evt.getName().equals("onOK")) {
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
		String nameFilter = productBandbox.getValue().toLowerCase();
		for(Product each : productPopupList) {
			if(each.getName().toLowerCase().contains(nameFilter) || nameFilter.equals("")) {
				someProducts.add(each);
			}
		}
		productPopupListModel = new ListModelList<Product>(someProducts);
		productPopupListbox.setModel(productPopupListModel);
	}

	@Listen("onChanging = #productBandbox")
	public void changeFilterProducts(InputEvent event) {
		if(currentProduct != null) {
			productPriceDoublebox.setValue(null);
			currentProduct = null;
		}
		Bandbox target = (Bandbox)event.getTarget();
		target.setText(event.getValue());
		filterProducts();
	}

	@Listen("onClick = #returnButton")
	public void returnButtonClick() {
		Include include = (Include) Selectors.iterable(this.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/order_list.zul");
	}

	private void filterClients() {
		List<Client> someClients = new ArrayList<>();
		String nameFilter = clientBandbox.getValue().toLowerCase();
		for(Client each : clientPopupList) {// busca filtrando por mail y nombre
			if((each.getName()+each.getEmail()).toLowerCase().contains(nameFilter) || nameFilter.equals("")) {
				someClients.add(each);
			}
		}
		clientPopupListModel = new ListModelList<Client>(someClients);
		clientPopupListbox.setModel(clientPopupListModel);
	}

	@Listen("onChanging = #clientBandbox")
	public void changeFilter(InputEvent event) {
		if(currentClient != null) {
			currentClient = null;
		}
		Bandbox target = (Bandbox)event.getTarget();
		target.setText(event.getValue());
		filterClients();
	}

	@Listen("onClick = #newOrderButton")
	public void newOrderButtonClick() {
		currentOrder = null;
		refreshViewOrder();
		filterClients();
	}

	public double totalPrice(Order order) {
		List<OrderDetail> order_detail_list = order.getDetails();
		BigDecimal total_price = BigDecimal.ZERO;
		for(OrderDetail order_detail : order_detail_list) {
			if(order_detail.getPrice() != null) {
				total_price = total_price.add(getSubTotal(order_detail.getUnits(), order_detail.getPrice()));
			}
		}
		return total_price.doubleValue();
	}

	@Listen("onClick = #deliveryNoteReportButton")
	public void deliveryNoteReportButtonClick() {
		loadDeliveryNoteJasperreport();
	}

	private void loadDeliveryNoteJasperreport() {
		Map<String, Object> parameters = getParameters();
		parameters.put("orderDate", currentOrder.getDateDelivery());

		Executions.getCurrent().setAttribute("jr_datasource", new OrderReportDataSource(currentOrder.getDetails()));
		Executions.getCurrent().setAttribute("return_page_name", "order_creation");
		Map<String, Object> returnParameters = new HashMap<String, Object>();
		returnParameters.put("selected_order", currentOrder);
		Executions.getCurrent().setAttribute("return_parameters", returnParameters);
		Executions.getCurrent().setAttribute("report_src_name", "delivery_note");
		Executions.getCurrent().setAttribute("report_parameters", parameters);
		Window window = (Window)Executions.createComponents("/report_selection_modal.zul", null, null);
		window.doModal();
	}
	
	private Map<String, Object> getParameters() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("orderClientName", currentOrder.getClient().getName());
		parameters.put("orderClientAddress", currentOrder.getClient().getAddress());
		parameters.put("orderClientPhone", currentOrder.getClient().getPhone());
		parameters.put("orderClientDetail", currentOrder.getClient().getDetails());
		parameters.put("orderNumber", currentOrder.getId().intValue());
		return parameters;
	}

	@Listen("onClick = #jasperReportButton")
	public void jasperReportButtonClick() {
		if(currentOrder == null) {
			Clients.showNotification("No se guardo el pedido");
		} else {
			loadJasperreport();
		}
	}

	private void loadJasperreport() {
		Map<String, Object> parameters = getParameters();
		parameters.put("orderDate", currentOrder.getDate());
		parameters.put("orderTotalPrice", totalPrice(currentOrder));

		Executions.getCurrent().setAttribute("jr_datasource", new OrderReportDataSource(currentOrder.getDetails()));
		Executions.getCurrent().setAttribute("return_page_name", "order_creation");
		Map<String, Object> returnParameters = new HashMap<String, Object>();
		returnParameters.put("selected_order", currentOrder);
		Executions.getCurrent().setAttribute("return_parameters", returnParameters);
		Executions.getCurrent().setAttribute("report_src_name", "order");
		//Executions.getCurrent().setAttribute("report_type", type);
		Executions.getCurrent().setAttribute("report_parameters", parameters);
		//		Include include = (Include) Selectors.iterable(this.getPage(), "#mainInclude").iterator().next();
		//		include.setSrc("/report_viewer.zul");
		Window window = (Window)Executions.createComponents("/report_selection_modal.zul", null, null);
		window.doModal();
	}
}

class OrderReportDataSource implements JRDataSource {

	private List<OrderDetail> orderDetailList = new ArrayList<OrderDetail>();
	private int index = -1;

	public OrderReportDataSource(List<OrderDetail> orderDetailList) {
		this.orderDetailList.addAll(orderDetailList);
	}

	public boolean next() throws JRException {
		index++;
		return (index < orderDetailList.size());
	}

	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		if ("client_name".equals(fieldName)) {
			value = orderDetailList.get(index).getOrder().getClient().getName();
		} else if ("product_code".equals(fieldName)) {
			value = orderDetailList.get(index).getProduct().getCode();
		} else if ("product_name".equals(fieldName)) {
			value = orderDetailList.get(index).getProduct().getDescription();
		} else if ("product_units".equals(fieldName)) {
			value = orderDetailList.get(index).getUnits();
		} else if ("unit_price".equals(fieldName)) {
			value = orderDetailList.get(index).getPrice().doubleValue();
		} else if ("subtotal".equals(fieldName)) {
			value = orderDetailList.get(index).getSubTotal().doubleValue();
		}
		return value;
	}
}