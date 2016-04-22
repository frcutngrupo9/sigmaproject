package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.*;
import ar.edu.utn.sigmaproject.service.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
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
	@WireVariable
	ClientRepository clientRepository;

	@WireVariable
	OrderRepository orderRepository;

	@WireVariable
	OrderStateTypeRepository orderStateTypeRepository;

	@WireVariable
	ProductRepository productRepository;

	@WireVariable
	ProductExistenceRepository productExistenceRepository;

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
		productList = productRepository.findAll();
		productListModel = new ListModelList<>(productList);
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
			currentProductExistence = currentProduct.getProductExistence();
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
		if (currentProductExistence.getId() == null) {
			// es nuevo
			currentProductExistence.setProduct(currentProduct);
		}
		productExistenceRepository.save(currentProductExistence);
		productList = productRepository.findAll();
		productListModel = new ListModelList<>(productList);
		currentProduct = null;
		refreshView();
	}

	// creacion de pedido de auto abastecimiento
	@Listen("onClick = #newProvisionOrderButton")
	public void newProvisionOrder() {
		Client client = clientRepository.findByName("Auto Abastecimiento");// deberia ser el id del cliente auto abastecimiento
		Date order_date = new Date();
		currentOrder = new Order(client, null, order_date, null);

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
		OrderStateType orderStateType = orderStateTypeRepository.findByName("iniciado");
		currentOrder.setNumber(order_number);
		currentOrder.setNeedDate(order_need_date);
		currentOrder.setCurrentStateType(orderStateType);
		currentOrder.setDetails(orderDetailList);
		currentOrder = orderRepository.save(currentOrder);
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
			OrderStateType orderStateTypeFinished = orderStateTypeRepository.findByName("finalizado");
			OrderStateType orderStateTypeCanceleded = orderStateTypeRepository.findByName("cancelado");
			List<Order> provisionOrderList = orderRepository.findByClient(clientRepository.findByName("Auto Abastecimiento"));// obtenemos la lista de los pedidos de autoabastecimiento
			// creamos una lista donde guardaremos todos los detalles de los pedidos de autoabastecimiento sumando su cantidad si el producto se repite
			List<OrderDetail> completeProvisionOrderDetailList = new ArrayList<OrderDetail>();
			for(Order eachProvisionOrder:provisionOrderList) {
				// por cada pedido debemos verificar que no este en estado finalizado o cancelado
				OrderStateType orderStateType = eachProvisionOrder.getCurrentStateType();
				if(!orderStateType.equals(orderStateTypeFinished) && !orderStateType.equals(orderStateTypeCanceleded)) {
					for(OrderDetail eachProvisionOrderDetail:eachProvisionOrder.getDetails()) {
						boolean is_in_list = false;
						for(OrderDetail eachCompleteProvisionOrderDetail:completeProvisionOrderDetailList) {
							if(eachCompleteProvisionOrderDetail.getProduct().equals(eachProvisionOrderDetail.getProduct())) {
								// si el mismo producto aparece en varios pedidos de reposicion hay que sumar sus cantidades y guardarlas en la lista completa de detalles de pedido de autoabasteciemiento
								eachCompleteProvisionOrderDetail.setUnits(eachCompleteProvisionOrderDetail.getUnits() + eachProvisionOrderDetail.getUnits());
								is_in_list = true;
							}
						}
						if(!is_in_list) {
							// si el producto no esta en la lista completa se lo agrega
							completeProvisionOrderDetailList.add(eachProvisionOrderDetail);
						}
					}
				}
			}
			// usamos la lista completa para asegurarnos que no se agreguen productos que ya tengan un pedido de autoabastecimiento activo o para que su cantidad sea la necesaria para llegar al stock
			orderDetailList = new ArrayList<>();
			List<ProductExistence> list_product_existence_complete = productExistenceRepository.findAll();
			for(ProductExistence eachProductExistence:list_product_existence_complete) {
				int units_to_repo = eachProductExistence.getStockRepo() - eachProductExistence.getStock();// si esta resta da un valor positivo quiere decir que el valor de reposicion esta por arriba del stock, por lo tanto ese valor es el necesario
				if(units_to_repo > 0) {
					// debemos recorrer los productos de los pedidos de autoabastecimiento para verificar si alguno es igual al producto que esta con bajo stock
					int units_existing = 0;
					for(OrderDetail eachCompleteProvisionOrderDetail:completeProvisionOrderDetailList) {// debemos buscar si este producto no tiene actualmente un pedido de auto abastecimiento sin finalizar
						if(eachCompleteProvisionOrderDetail.getProduct().equals(eachProductExistence.getProduct())) {
							units_existing = eachCompleteProvisionOrderDetail.getUnits();
						}
					}
					// debemos revisar si la cantidad pedida en este detalle es mayor o igual a la necesitada en stock, en caso de ser asi, este producto no debe ser agregado al pedido de auto abastecimiento, caso contrario, se debe agregar el producto y la cantidad sera la diferencia entre lo que se necesita y lo que hay ya pedido
					units_to_repo = units_to_repo - units_existing;
					if(units_to_repo > 0) {
						orderDetailList.add(new OrderDetail(null, eachProductExistence.getProduct(), units_to_repo, new BigDecimal("0")));
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

	@Listen("onEditOrderDetailUnits = #orderDetailListbox")
	public void doEditOrderDetailUnits(ForwardEvent evt) {
		OrderDetail orderDetail = (OrderDetail) evt.getData();// obtenemos el objeto pasado por parametro
		Spinner spinner = (Spinner) evt.getOrigin().getTarget();// obtenemos el elemento web
		orderDetail.setUnits(spinner.getValue());// cargamos al objeto el valor actualizado del elemento web
	}

}
