package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductTotal;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanDetail;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.service.ClientRepository;
import ar.edu.utn.sigmaproject.service.OrderRepository;
import ar.edu.utn.sigmaproject.service.OrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanDetailRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeRepository;

public class ProductionPlanCreationController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Listbox orderPopupListbox;
	@Wire
	Grid productionPlanDetailGrid;
	@Wire
	Bandbox orderBandbox;
	@Wire
	Textbox productionPlanNameTextbox;
	@Wire
	Datebox productionPlanDatebox;
	@Wire
	Button addOrderButton;
	@Wire
	Button resetProductionPlanButton;
	@Wire
	Button saveProductionPlanButton;
	@Wire
	Button deleteProductionPlanButton;
	@Wire
	Listbox productTotalListbox;
	@Wire
	Combobox productionPlanStateTypeCombobox;
	@Wire
	Caption productionPlanCaption;

	// services
	@WireVariable
	private OrderRepository orderRepository;
	@WireVariable
	private OrderStateTypeRepository orderStateTypeRepository;
	@WireVariable
	private ProductRepository productRepository;
	@WireVariable
	private ProductionPlanRepository productionPlanRepository;
	@WireVariable
	private ProductionPlanDetailRepository productionPlanDetailRepository;
	@WireVariable
	private ClientRepository clientService;
	@WireVariable
	private ProductionPlanStateTypeRepository productionPlanStateTypeRepository;

	// list
	private List<Order> orderPopupList;
	private List<ProductionPlanDetail> currentProductionPlanDetailList;
	private List<ProductTotal> productTotalList;
	private List<ProductionPlanStateType> productionPlanStateTypeList;

	// list models
	private ListModelList<Order> orderPopupListModel;
	private ListModelList<ProductionPlanDetail> productionPlanDetailListModel;
	private ListModelList<ProductionPlanStateType> productionPlanStateTypeListModel;

	// atributes
	private Order currentOrder;
	private ProductionPlan currentProductionPlan;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		productTotalList = new ArrayList<ProductTotal>();
		currentProductionPlanDetailList = new ArrayList<ProductionPlanDetail>();
		currentProductionPlan = (ProductionPlan) Executions.getCurrent().getAttribute("selected_production_plan");

		productionPlanStateTypeList = productionPlanStateTypeRepository.findAll();
		productionPlanStateTypeListModel = new ListModelList<ProductionPlanStateType>(productionPlanStateTypeList);
		productionPlanStateTypeCombobox.setModel(productionPlanStateTypeListModel);

		refreshViewProductionPlan();
		refreshProductTotalListbox();
	}

	@Listen("onSelect = #orderPopupListbox")
	public void selectionOrderPopupListbox() {
		currentOrder = (Order) orderPopupListbox.getSelectedItem().getValue();
		refreshOrder();
	}

	private void refreshOrder() {
		if(currentOrder != null) {
			orderBandbox.setValue("Pedido " + currentOrder.getId());
			orderBandbox.close();
		}else {
			orderBandbox.setValue("");// borramos el text del producto  seleccionado
			orderPopupListbox.clearSelection();// deseleccionamos la tabla
		}
	}

	private void refreshOrderPopupList() {// el popup se actualiza en base a los detalles
		orderPopupList = orderRepository.findByCurrentStateType(orderStateTypeRepository.findByName("iniciado"));// se buscan los pedidos que no estan asignados a un plan y no estan cancelados (estan en estado iniciado)
		for(ProductionPlanDetail productionPlanDetail : currentProductionPlanDetailList) {// no debe contener los pedidos que ya estan en el detalle
			Order aux = productionPlanDetail.getOrder();
			orderPopupList.remove(aux);// sacamos los pedidos que estan en la currentProductionPlanDetailList del popup
		}
		// agregamos el pedido que se elimin� y al ser de un plan que se esta editando, no aparece en la lista
		// [aca deberia agregarse a la lista los pedidos que esten en el ProductionPlanDetailList serializado pero no esten en el currentProductionPlanDetailList (solo si se esta editando un plan)]
		orderPopupListModel = new ListModelList<Order>(orderPopupList);
		orderPopupListbox.setModel(orderPopupListModel);
	}

	@Listen("onClick = #resetProductionPlanButton")
	public void resetProductionPlan() {
		refreshViewProductionPlan();
	}

	@Listen("onClick = #deleteProductionPlanButton")
	public void deleteProductionPlan() {
		if(currentProductionPlan != null) {
			productionPlanRepository.delete(currentProductionPlan);
			currentProductionPlan = null;
			refreshViewProductionPlan();
		}
	}

	@Listen("onClick = #saveProductionPlanButton")
	public void saveProductionPlan() {
		if(currentProductionPlanDetailList.size() == 0) {
			Clients.showNotification("Ingresar al menos 1 pedido", addOrderButton);
			return;
		}
		String productionPlanName = productionPlanNameTextbox.getText().toUpperCase();
		Date production_plan_date = productionPlanDatebox.getValue();
		ProductionPlanStateType productionPlanStateType;
		if(productionPlanStateTypeCombobox.getSelectedIndex() == -1) {
			productionPlanStateType = null;
		} else {
			productionPlanStateType = productionPlanStateTypeCombobox.getSelectedItem().getValue();
		}
		if(currentProductionPlan == null) { // es un plan nuevo
			// creamos el nuevo plan
			currentProductionPlan = new ProductionPlan(productionPlanName, null, production_plan_date);
			currentProductionPlan.setCurrentStateType(productionPlanStateType);
		} else { // se edita un plan
			currentProductionPlan.setName(productionPlanName);
			currentProductionPlan.setDate(production_plan_date);
			if (!currentProductionPlan.getCurrentStateType().equals(productionPlanStateType)) {
				currentProductionPlan.setCurrentStateType(productionPlanStateType);
			}
		}

		for(ProductionPlanDetail each : currentProductionPlanDetailList) {
			each = productionPlanDetailRepository.save(each);
		}
		currentProductionPlan.setPlanDetails(currentProductionPlanDetailList);
		productionPlanRepository.save(currentProductionPlan);
		currentProductionPlan = null;
		refreshViewProductionPlan();
		alert("Plan guardado.");
	}

	@Listen("onClick = #addOrderButton")
	public void addOrder() {
		currentProductionPlanDetailList.add(new ProductionPlanDetail(currentOrder));
		refreshProductionPlanDetailListGrid();
		currentOrder = null;
		refreshOrderPopupList();
		refreshOrder();
		refreshProductTotalListbox();
	}

	@Listen("onRemoveOrder = #productionPlanDetailGrid")
	public void doRemoveOrder(ForwardEvent evt) {
		Order order = (Order) evt.getData();
		// eliminamos el detalle de la lista
		for(ProductionPlanDetail auxProductionPlanDetail : currentProductionPlanDetailList) {
			if (auxProductionPlanDetail.getOrder().equals(order)) {
				currentProductionPlanDetailList.remove(auxProductionPlanDetail);
				break;
			}
		}
		refreshProductionPlanDetailListGrid();
		refreshProductTotalListbox();
		refreshOrderPopupList();
	}

	private void refreshProductionPlanDetailListGrid() {
		productionPlanDetailListModel = new ListModelList<ProductionPlanDetail>(currentProductionPlanDetailList);
		productionPlanDetailGrid.setModel(productionPlanDetailListModel);
	}

	private void refreshProductTotalListbox() {
		refreshProductTotalList();
		ListModelList<ProductTotal> productTotalListModel = new ListModelList<ProductTotal>(productTotalList);
		productTotalListbox.setModel(productTotalListModel);
	}

	private void refreshViewProductionPlan() {
		currentOrder = null;// deseleccionamos el pedido
		refreshOrder();
		if (currentProductionPlan == null) {// nuevo plan de produccion
			productionPlanCaption.setLabel("Creacion de Plan de Produccion");
			productionPlanStateTypeListModel.addToSelection(productionPlanStateTypeRepository.findByName("Iniciado"));
			productionPlanStateTypeCombobox.setModel(productionPlanStateTypeListModel);
			productionPlanNameTextbox.setText("");
			productionPlanDatebox.setValue(new Date());
			currentProductionPlanDetailList = new ArrayList<ProductionPlanDetail>();
			deleteProductionPlanButton.setDisabled(true);
			productionPlanStateTypeCombobox.setDisabled(true);
		} else {// se edita plan de produccion
			productionPlanCaption.setLabel("Edicion de Plan de Produccion");
			if (currentProductionPlan.getCurrentStateType() != null) {
				productionPlanStateTypeListModel.addToSelection(currentProductionPlan.getCurrentStateType());
				productionPlanStateTypeCombobox.setModel(productionPlanStateTypeListModel);
			} else {
				productionPlanStateTypeCombobox.setSelectedIndex(-1);
			}
			if(currentProductionPlan.getName() != null) {
				productionPlanNameTextbox.setText(currentProductionPlan.getName());
			} else {
				productionPlanNameTextbox.setText("");
			}
			productionPlanDatebox.setValue(currentProductionPlan.getDate());
			currentProductionPlanDetailList = currentProductionPlan.getPlanDetails();
			deleteProductionPlanButton.setDisabled(false);
			productionPlanStateTypeCombobox.setDisabled(false);
		}
		productionPlanDatebox.setDisabled(true);// nunca se debe poder modificar
		refreshOrderPopupList();
		refreshProductionPlanDetailListGrid();
		refreshProductTotalListbox();
	}

	private void refreshProductTotalList() {
		Map<Product, Integer> productTotalMap = new HashMap<Product, Integer>();
		for(ProductionPlanDetail auxProductionPlanDetail : currentProductionPlanDetailList) {
			for(OrderDetail auxOrderDetail : auxProductionPlanDetail.getOrder().getDetails()) {
				Integer totalUnits = productTotalMap.get(auxOrderDetail.getProduct());
				productTotalMap.put(auxOrderDetail.getProduct(), (totalUnits == null) ? auxOrderDetail.getUnits() : totalUnits + auxOrderDetail.getUnits());
			}
		}
		productTotalList = new ArrayList<ProductTotal>();
		for (Map.Entry<Product, Integer> entry : productTotalMap.entrySet()) {
			Product product = entry.getKey();
			Integer totalUnits = entry.getValue();
			ProductTotal productTotal = new ProductTotal(product, totalUnits);
			productTotalList.add(productTotal);
		}
	}

	public int getProductTotalUnits(Product product) {
		int productTotalUnits = 0;
		for(ProductTotal productTotal : productTotalList) {// buscamos el total de unidades
			if(productTotal.getProduct().equals(product)) {
				productTotalUnits = productTotal.getTotalUnits();
			}
		}
		return productTotalUnits;
	}

	public BigDecimal getProductTotalPrice(Product product) {
		int productTotalUnits = getProductTotalUnits(product);
		return getTotalPrice(productTotalUnits, product.getPrice());// esta funcion es incorrecta pq agarra el valor actual del producto cuando deberia ser el valor en el pedido
	}

	private BigDecimal getTotalPrice(int units, BigDecimal price) {
		if(price == null) {
			price = BigDecimal.ZERO;
		}
		return price.multiply(new BigDecimal(units));
	}

	public String getPieceTotalUnits(Piece piece) {
		int units = 0;
		for(ProductionPlanDetail auxProductionPlanDetail : currentProductionPlanDetailList) {
			for (OrderDetail auxOrderDetail : auxProductionPlanDetail.getOrder().getDetails()) {
				if (auxOrderDetail.getProduct().equals(productRepository.findByPieces(piece))) {
					units = auxOrderDetail.getUnits();
				}
			}
		}
		if(units > 0) {
			units = piece.getUnits() * units;
		}
		return units + "";
	}

	public String getTotalTime(Piece piece, ProcessType processType) {
		Process process = null;
		for(int j = 0; j < piece.getProcesses().size(); j++) {
			if (piece.getProcesses().get(j).getType().equals(processType)) {
				process = piece.getProcesses().get(j);
				break;
			}
		}
		long total = 0;
		int units = 0;
		for (ProductionPlanDetail productionPlanDetail : currentProductionPlanDetailList) {
			for (OrderDetail auxOrderDetail : productionPlanDetail.getOrder().getDetails()) {
				if (auxOrderDetail.getProduct().equals(productRepository.findByPieces(piece))) {
					units = auxOrderDetail.getUnits();
				}
			}
		}
		if(units > 0) {
			total = process.getTime().getMinutes() * units;
		}
		return total + "";
	}

}
