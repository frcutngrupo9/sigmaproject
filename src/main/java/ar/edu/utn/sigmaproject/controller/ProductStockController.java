package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductExistence;
import ar.edu.utn.sigmaproject.service.ClientService;
import ar.edu.utn.sigmaproject.service.OrderDetailService;
import ar.edu.utn.sigmaproject.service.OrderService;
import ar.edu.utn.sigmaproject.service.OrderStateService;
import ar.edu.utn.sigmaproject.service.OrderStateTypeService;
import ar.edu.utn.sigmaproject.service.ProductExistenceService;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.impl.ClientServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderDetailServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderStateServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderStateTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductExistenceServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductServiceImpl;

public class ProductStockController extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Listbox productListbox;
	@Wire
	Grid productExistenceGrid;
	@Wire
	Textbox codeTextbox;
	@Wire
	Textbox nameTextbox;
	@Wire
	Intbox stockIntbox;
	@Wire
	Intbox stockMinIntbox;
	@Wire
	Intbox stockRepoIntbox;
	@Wire
	Button saveButton;
	@Wire
	Button cancelButton;
	@Wire
	Button resetButton;
	@Wire
	Button newProvisionOrderButton;
	@Wire
	Component orderCreationBlock;
	@Wire
	Listbox orderDetailListbox;
	@Wire
	Intbox orderNumberIntbox;
	@Wire
	Datebox orderNeedDatebox;

	// services
	private ProductExistenceService productExistenceService = new ProductExistenceServiceImpl();
	private ProductService productService = new ProductServiceImpl();
	private ClientService clientService = new ClientServiceImpl();
	private OrderService orderService = new OrderServiceImpl();
	private OrderDetailService orderDetailService = new OrderDetailServiceImpl();
	private OrderStateService orderStateService = new OrderStateServiceImpl();
	private OrderStateTypeService orderStateTypeService = new OrderStateTypeServiceImpl();

	// attributes
	private ProductExistence currentProductExistence;
	private Product currentProduct;
	private Order currentOrder;

	// list
	private List<Product> productList;
	private List<OrderDetail> orderDetailList;

	// list models
	private ListModelList<Product> productListModel;
	private ListModelList<OrderDetail> orderDetailListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		productList = productService.getProductList();
		productListModel = new ListModelList<Product>(productList);
		productListbox.setModel(productListModel);
		currentProduct = null;
		refreshView();

		currentOrder = null;
		refreshViewOrder();
	}

	@Listen("onClick = #searchButton")
	public void search() {
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		currentProduct = null;
		refreshView();
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
	}

	@Listen("onSelect = #productListbox")
	public void doListBoxSelect() {
		if(productListModel.isSelectionEmpty()) {
			//just in case for the no selection
			currentProduct = null;
		} else {
			if(currentProduct == null && currentOrder == null) {// si no hay nada editandose
				currentProduct = productListbox.getSelectedItem().getValue();
				refreshView();
			}
		}
		productListModel.clearSelection();
	}

	private void refreshView() {
		productListModel.clearSelection();
		productListbox.setModel(productListModel);// se actualiza la lista
		codeTextbox.setDisabled(true);
		nameTextbox.setDisabled(true);// no se deben poder modificar
		if(currentProduct == null) {// no editando ni creando
			productExistenceGrid.setVisible(false);
			codeTextbox.setValue(null);
			nameTextbox.setValue(null);
			stockIntbox.setValue(null);
			stockMinIntbox.setValue(null);
			stockRepoIntbox.setValue(null);
			saveButton.setDisabled(true);
			cancelButton.setDisabled(true);
			resetButton.setDisabled(true);
			newProvisionOrderButton.setDisabled(false);
			currentProductExistence = null;
		}else {// editando o creando
			productExistenceGrid.setVisible(true);
			codeTextbox.setValue(currentProduct.getCode());
			nameTextbox.setValue(currentProduct.getName());
			int stock = 0;
			int stock_min = 0;
			int stock_repo = 0;
			currentProductExistence = productExistenceService.getProductExistence(currentProduct.getId());
			if(currentProductExistence != null) {
				stock = currentProductExistence.getStock();
				stock_min = currentProductExistence.getStockMin();
				stock_repo = currentProductExistence.getStockRepo();
			} else {
				currentProductExistence = new ProductExistence(null, 0, 0, 0);// no existe, se crea uno con id nulo para saber que hacer al guardar
			}
			stockIntbox.setValue(stock);
			stockMinIntbox.setValue(stock_min);
			stockRepoIntbox.setValue(stock_repo);
			saveButton.setDisabled(false);
			cancelButton.setDisabled(false);
			resetButton.setDisabled(false);
			newProvisionOrderButton.setDisabled(true);
		}
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		currentProductExistence.setStock(stockIntbox.intValue());
		currentProductExistence.setStockMin(stockMinIntbox.intValue());
		currentProductExistence.setStockRepo(stockRepoIntbox.intValue());
		if(currentProductExistence.getIdProduct() == null) {
			// es nuevo
			currentProductExistence.setIdProduct(currentProduct.getId());
			currentProductExistence = productExistenceService.saveProductExistence(currentProductExistence);
		} else {
			// es una edicion
			currentProductExistence = productExistenceService.updateProductExistence(currentProductExistence);
		}
		productList = productService.getProductList();
		productListModel = new ListModelList<Product>(productList);
		currentProduct = null;
		refreshView();
	}

	public String getProductStock(int idProduct) {
		Integer value = 0;
		ProductExistence aux = productExistenceService.getProductExistence(idProduct);
		if(aux != null) {
			value = aux.getStock();
		}
		return value + "";
	}

	public String getProductStockMin(int idProduct) {
		Integer value = 0;
		ProductExistence aux = productExistenceService.getProductExistence(idProduct);
		if(aux != null) {
			value = aux.getStockMin();
		}
		return value + "";
	}

	public String getProductStockRepo(int idProduct) {
		Integer value = 0;
		ProductExistence aux = productExistenceService.getProductExistence(idProduct);
		if(aux != null) {
			value = aux.getStockRepo();
		}
		return value + "";
	}

	// creacion de pedido de auto abastecimiento
	@Listen("onClick = #newProvisionOrderButton")
	public void newProvisionOrder() {
		Integer order_client_id = clientService.getClient("Auto Abastecimiento").getId();// deberia ser el id del cliente auto abastecimiento
		Integer order_number = null;
		Date order_date = new Date();
		Date order_need_date = null;
		currentOrder = new Order(null, order_client_id, order_number, order_date, order_need_date);

		refreshViewOrder();
	}

	@Listen("onClick = #resetOrderButton")
	public void resetOrder() {
		refreshViewOrder();
	}

	@Listen("onClick = #cancelOrderButton")
	public void cancelOrder() {
		currentOrder = null;
		refreshViewOrder();
	}

	@Listen("onClick = #saveOrderButton")
	public void saveOrder() {
		int order_number = orderNumberIntbox.intValue();
		Date order_need_date = orderNeedDatebox.getValue();
		int order_state_type_id = orderStateTypeService.getOrderStateType("iniciado").getId();
		currentOrder.setNumber(order_number);
		currentOrder.setNeedDate(order_need_date);
		currentOrder = orderService.saveOrder(currentOrder, order_state_type_id, orderDetailList);
		Clients.showNotification("Pedido guardado");
		currentOrder = null;
		refreshViewOrder();
	}

	private void refreshViewOrder() {
		if(currentOrder == null) {
			orderCreationBlock.setVisible(false);
			orderDetailList = null;
			newProvisionOrderButton.setDisabled(false);
		} else {
			int id_order_state_type_finished = orderStateTypeService.getOrderStateType("finalizado").getId();
			int id_order_state_type_canceleded = orderStateTypeService.getOrderStateType("cancelado").getId();
			List<Order> provisionOrderList = orderService.getOrderListByClientId(clientService.getClient("Auto Abastecimiento").getId());// obtenemos la lista de los pedidos de autoabastecimiento
			// creamos una lista donde guardaremos todos los detalles de los pedidos de autoabastecimiento sumando su cantidad si el producto se repite
			List<OrderDetail> completeProvisionOrderDetailList = new ArrayList<OrderDetail>();
			for(Order eachProvisionOrder:provisionOrderList) {
				// por cada pedido debemos verificar que no este en estado finalizado o cancelado
				int id_last_order_state_type = orderStateService.getLastOrderState(eachProvisionOrder.getId()).getIdOrderStateType();
				if(id_last_order_state_type != id_order_state_type_finished && id_last_order_state_type != id_order_state_type_canceleded) {
					List<OrderDetail> provisionOrderDetailList = orderDetailService.getOrderDetailList(eachProvisionOrder.getId());
					for(OrderDetail eachProvisionOrderDetail:provisionOrderDetailList) {
						boolean is_in_list = false;
						for(OrderDetail eachCompleteProvisionOrderDetail:completeProvisionOrderDetailList) {
							if(eachCompleteProvisionOrderDetail.getIdProduct().equals(eachProvisionOrderDetail.getIdProduct())) {
								// si el mismo producto aparece en varios pedidos de reposicion hay que sumar sus cantidades y guardarlas en la lista completa de detalles de pedido de autoabasteciemiento
								eachCompleteProvisionOrderDetail.setUnits(eachCompleteProvisionOrderDetail.getUnits() + eachProvisionOrderDetail.getUnits());
								is_in_list = true;
							}
						}
						if(is_in_list == false) {
							// si el producto no esta en la lista completa se lo agrega
							completeProvisionOrderDetailList.add(eachProvisionOrderDetail);
						}
					}
				}
			}
			// usamos la lista completa para asegurarnos que no se agreguen productos que ya tengan un pedido de autoabastecimiento activo o para que su cantidad sea la necesaria para llegar al stock
			orderDetailList = new ArrayList<OrderDetail>();
			List<ProductExistence> list_product_existence_complete = productExistenceService.getProductExistenceList();
			for(ProductExistence eachProductExistence:list_product_existence_complete) {
				int units_to_repo = eachProductExistence.getStockRepo() - eachProductExistence.getStock();// si esta resta da un valor positivo quiere decir que el valor de reposicion esta por arriba del stock, por lo tanto ese valor es el necesario
				if(units_to_repo > 0) {
					// debemos recorrer los productos de los pedidos de autoabastecimiento para verificar si alguno es igual al producto que esta con bajo stock
					int units_existing = 0;
					for(OrderDetail eachCompleteProvisionOrderDetail:completeProvisionOrderDetailList) {// debemos buscar si este producto no tiene actualmente un pedido de auto abastecimiento sin finalizar
						if(eachCompleteProvisionOrderDetail.getIdProduct().equals(eachProductExistence.getIdProduct())) {
							units_existing = eachCompleteProvisionOrderDetail.getUnits();
						}
					}
					// debemos revisar si la cantidad pedida en este detalle es mayor o igual a la necesitada en stock, en caso de ser asi, este producto no debe ser agregado al pedido de auto abastecimiento, caso contrario, se debe agregar el producto y la cantidad sera la diferencia entre lo que se necesita y lo que hay ya pedido
					units_to_repo = units_to_repo - units_existing;
					if(units_to_repo > 0) {
						orderDetailList.add(new OrderDetail(null, eachProductExistence.getIdProduct(), units_to_repo, new BigDecimal("0")));
					}
				}
			}
			// si la orderDetailList esta vacia quiere decir que no hay productos que necesiten un pedido de autoasbastecimiento, por lo tanto se informa y se cancela la creacion
			if(orderDetailList.isEmpty()) {
				Clients.showNotification("No existen productos con stock bajo que necesiten un pedido");
				orderCreationBlock.setVisible(false);
				orderDetailList = null;
				newProvisionOrderButton.setDisabled(false);
			} else {
				orderCreationBlock.setVisible(true);
				orderNumberIntbox.setValue(currentOrder.getNumber());
				orderNeedDatebox.setValue(null);
				orderDetailListModel = new ListModelList<OrderDetail>(orderDetailList);
				orderDetailListbox.setModel(orderDetailListModel);
				newProvisionOrderButton.setDisabled(true);
			}

		}

	}

	public String getProductName(int idProduct) {
		Product aux = productService.getProduct(idProduct);
		return aux.getName();
	}

	public String getProductCode(int idProduct) {
		Product aux = productService.getProduct(idProduct);
		return aux.getCode();
	}

	@Listen("onEditOrderDetailUnits = #orderDetailListbox")
	public void doEditOrderDetailUnits(ForwardEvent evt) {
		OrderDetail orderDetail = (OrderDetail) evt.getData();// obtenemos el objeto pasado por parametro
		Spinner spinner = (Spinner) evt.getOrigin().getTarget();// obtenemos el elemento web
		orderDetail.setUnits(spinner.getValue());// cargamos al objeto el valor actualizado del elemento web
	}

}
