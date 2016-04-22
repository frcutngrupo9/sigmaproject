package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Selectbox;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Client;
import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductTotal;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanDetail;
import ar.edu.utn.sigmaproject.domain.ProductionPlanState;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.service.ClientRepository;
import ar.edu.utn.sigmaproject.service.OrderRepository;
import ar.edu.utn.sigmaproject.service.OrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductRepository;
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
	Listbox processListbox;
	@Wire
	Listbox supplyListbox;
	@Wire
	Button resetProductionPlanButton;
	@Wire
	Button saveProductionPlanButton;
	@Wire
	Button deleteProductionPlanButton;
	@Wire
	Listbox productTotalListbox;
	@Wire
	Selectbox productionPlanStateTypeSelectbox;
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
	private ListModelList<Process> processListModel;
	private ListModelList<ProductionPlanDetail> productionPlanDetailListModel;
	private ListModelList<ProductionPlanStateType> productionPlanStateTypeListModel;

	// atributes
	private Order currentOrder;
	private ProductionPlan currentProductionPlan;
	private ProductionPlanState currentProductionPlanState;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		productTotalList = new ArrayList<ProductTotal>();
		currentProductionPlanDetailList = new ArrayList<ProductionPlanDetail>();
		currentProductionPlan = (ProductionPlan) Executions.getCurrent().getAttribute("selected_production_plan");

		productionPlanStateTypeList = productionPlanStateTypeRepository.findAll();
		productionPlanStateTypeListModel = new ListModelList<ProductionPlanStateType>(productionPlanStateTypeList);
		productionPlanStateTypeSelectbox.setModel(productionPlanStateTypeListModel);

		refreshViewProductionPlan();
		refreshProcessListBox();
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
		String productionPlanName = productionPlanNameTextbox.getText();
		Date production_plan_date = productionPlanDatebox.getValue();
		ProductionPlanStateType productionPlanStateType = productionPlanStateTypeListModel.getElementAt(productionPlanStateTypeSelectbox.getSelectedIndex());
		if(currentProductionPlan == null) { // es un plan nuevo
			// creamos el nuevo plan
			currentProductionPlan = new ProductionPlan(productionPlanName, null, production_plan_date);
			currentProductionPlan.setPlanDetails(currentProductionPlanDetailList);
			currentProductionPlan.getStates().add(new ProductionPlanState(currentProductionPlan, productionPlanStateType, new Date()));
			currentProductionPlan.setCurrentStateType(productionPlanStateType);
		} else { // se edita un plan
			currentProductionPlan.setName(productionPlanName);
			currentProductionPlan.setDate(production_plan_date);
			currentProductionPlan.setPlanDetails(currentProductionPlanDetailList);
			if (!currentProductionPlan.getCurrentStateType().equals(productionPlanStateType)) {
				currentProductionPlan.getStates().add(new ProductionPlanState(currentProductionPlan, productionPlanStateType, new Date()));
				currentProductionPlan.setCurrentStateType(productionPlanStateType);
			}
		}
		productionPlanRepository.save(currentProductionPlan);
		currentProductionPlan = null;
		currentProductionPlanState = null;
		refreshViewProductionPlan();
		alert("Plan guardado.");
	}

	@Listen("onClick = #addOrderButton")
	public void addOrder() {
		// el detalle debe tener id de plan nulo ya que se lo agrega al guardar o actualizar el plan
		currentProductionPlanDetailList.add(new ProductionPlanDetail(currentProductionPlan, currentOrder));
		refreshProductionPlanDetailListGrid();
		currentOrder = null;
		refreshOrderPopupList();
		refreshOrder();
		refreshProcessListBox();
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
			productionPlanStateTypeListModel.addToSelection(productionPlanStateTypeRepository.findByName("iniciado"));
			productionPlanStateTypeSelectbox.setModel(productionPlanStateTypeListModel);
			productionPlanNameTextbox.setText("");
			productionPlanDatebox.setValue(new Date());
			currentProductionPlanDetailList = new ArrayList<ProductionPlanDetail>();
			deleteProductionPlanButton.setDisabled(true);
			productionPlanStateTypeSelectbox.setDisabled(true);
		} else {// se edita plan de produccion
			productionPlanCaption.setLabel("Edicion de Plan de Produccion");
			if (currentProductionPlan.getCurrentStateType() != null) {
				productionPlanStateTypeListModel.addToSelection(currentProductionPlan.getCurrentStateType());
				productionPlanStateTypeSelectbox.setModel(productionPlanStateTypeListModel);
			} else {
				productionPlanStateTypeSelectbox.setSelectedIndex(-1);
			}
			if(currentProductionPlan.getName() != null) {
				productionPlanNameTextbox.setText(currentProductionPlan.getName());
			} else {
				productionPlanNameTextbox.setText("");
			}
			productionPlanDatebox.setValue(currentProductionPlan.getDate());
			currentProductionPlanDetailList = currentProductionPlan.getPlanDetails();
			deleteProductionPlanButton.setDisabled(false);
			productionPlanStateTypeSelectbox.setDisabled(false);
		}
		productionPlanDatebox.setDisabled(true);// nunca se debe poder modificar
		refreshOrderPopupList();
		refreshProductionPlanDetailListGrid();
		refreshProductTotalListbox();
	}

	private void refreshProcessListBox() {
		List<Process> processList = new ArrayList<Process>();
		for(ProductionPlanDetail auxProductionPlanDetail : currentProductionPlanDetailList) {
			Order auxOrder = auxProductionPlanDetail.getOrder();
			List<OrderDetail> auxOrderDetailList = auxOrder.getDetails();
			for(OrderDetail auxOrderDetail : auxOrderDetailList) {
				Product auxProduct = auxOrderDetail.getProduct();
				List<Piece> auxPieceList = auxProduct.getPieces();
				for(Piece auxPiece : auxPieceList) {
					List<Process> auxProcessList = auxPiece.getProcesses();
					for(Process auxProcess : auxProcessList) {
						processList.add(auxProcess);
					}
				}
			}
		}
		processListModel = new ListModelList<Process>(processList);
		processListbox.setModel(processListModel);
	}

	private void refreshProductTotalList() {
		productTotalList = new ArrayList<ProductTotal>();// se empieza con una lista vacia
		for(ProductionPlanDetail auxProductionPlanDetail : currentProductionPlanDetailList) {
			for(OrderDetail auxOrderDetail : auxProductionPlanDetail.getOrder().getDetails()) {// por cada detalle del pedido, observamos si el producto ya esta en la lista, si lo esta sumamos su cantidad y, si no esta lo agregamos
				Boolean is_in_list = false;
				Integer order_detail_units = auxOrderDetail.getUnits();
				for(ProductTotal productTotal : productTotalList) {
					if(productTotal.getId().equals(auxOrderDetail.getProduct().getId())) {// si esta
						is_in_list = true;
						productTotal.setTotalUnits(productTotal.getTotalUnits() + order_detail_units);// sumamos su cantidad con la existente
						break;
					}
				}
				if(is_in_list == false) {// no esta, por lo tanto agregamos el producto a la lista total
					ProductTotal productTotal = new ProductTotal(auxOrderDetail.getProduct());
					productTotal.setTotalUnits(order_detail_units);// el primer valor son el total de unidades del detalle de pedido
					productTotalList.add(productTotal);
				}
			}
			// si es el primer loop del productionPlanDetailList entonces la lista productTotalList deberia estar llena solo con los productos
			// del primer pedido, en el siguiente loop se sumaran los que ya estan y agregaran los nuevos
		}
		// aqui deberia llegar con el productTotalList lleno con todos los productos sin repetir y con el total, que conforman el plan de produccion
	}

	public int getProductTotalUnits(Product product) {
		int product_total_units = 0;
		for(ProductTotal productTotal : productTotalList) {// buscamos el total de unidades
			if(productTotal.getId().equals(product.getId())) {
				product_total_units = productTotal.getTotalUnits();
			}
		}
		return product_total_units;
	}

	public BigDecimal getProductTotalPrice(Product product) {
		int product_total_units = getProductTotalUnits(product);
		return getTotalPrice(product_total_units, product.getPrice());// esta funcion es incorrecta pq agarra el valor actual del producto cuando deberia ser el valor en el pedido
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
				if (auxOrderDetail.getProduct().equals(piece.getProduct())) {
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
				if (auxOrderDetail.getProduct().equals(piece.getProduct())) {
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
