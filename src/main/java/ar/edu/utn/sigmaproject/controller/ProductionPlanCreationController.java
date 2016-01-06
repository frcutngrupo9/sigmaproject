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
import org.zkoss.zk.ui.select.annotation.Wire;
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
import ar.edu.utn.sigmaproject.service.ClientService;
import ar.edu.utn.sigmaproject.service.OrderDetailService;
import ar.edu.utn.sigmaproject.service.OrderService;
import ar.edu.utn.sigmaproject.service.OrderStateTypeService;
import ar.edu.utn.sigmaproject.service.PieceService;
import ar.edu.utn.sigmaproject.service.ProcessService;
import ar.edu.utn.sigmaproject.service.ProcessTypeService;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.ProductionPlanDetailService;
import ar.edu.utn.sigmaproject.service.ProductionPlanService;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateService;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeService;
import ar.edu.utn.sigmaproject.service.impl.ClientServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderDetailServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderStateTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.PieceServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProcessServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProcessTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanDetailServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanStateServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanStateTypeServiceImpl;

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
    Datebox productionPlanDateBox;
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
    Selectbox productionPlanStateTypeSelectBox;
    @Wire
	Caption productionPlanCaption;
	
	// services
	private OrderService orderService = new OrderServiceImpl();
	private OrderDetailService orderDetailService = new OrderDetailServiceImpl();
	private OrderStateTypeService orderStateTypeService = new OrderStateTypeServiceImpl();
	private ProductService productService = new ProductServiceImpl();
	private ProductionPlanService productionPlanService = new ProductionPlanServiceImpl();
	private ProductionPlanDetailService productionPlanDetailService = new ProductionPlanDetailServiceImpl();
	private PieceService pieceService = new PieceServiceImpl();
	private ProcessService processService = new ProcessServiceImpl();
	private ProcessTypeService processTypeService = new ProcessTypeServiceImpl();
	private ClientService clientService = new ClientServiceImpl();
	private ProductionPlanStateService productionPlanStateService = new ProductionPlanStateServiceImpl();
	private ProductionPlanStateTypeService productionPlanStateTypeService = new ProductionPlanStateTypeServiceImpl();;
	
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
        
        productionPlanStateTypeList = productionPlanStateTypeService.getProductionPlanStateTypeList();
        productionPlanStateTypeListModel = new ListModelList<ProductionPlanStateType>(productionPlanStateTypeList);
        productionPlanStateTypeSelectBox.setModel(productionPlanStateTypeListModel);
        
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
		Integer order_state_type_id = orderStateTypeService.getOrderStateType("iniciado").getId();// se busca el id del estado de pedido iniciado
		orderPopupList = orderService.getOrderList(order_state_type_id);// se buscan los pedidos que no estan asignados a un plan y no estan cancelados (estan en estado iniciado)
		for(ProductionPlanDetail productionPlanDetail : currentProductionPlanDetailList) {// no debe contener los pedidos que ya estan en el detalle
    		Order aux = orderService.getOrder(productionPlanDetail.getIdOrder());
    		orderPopupList.remove(aux);// sacamos los pedidos que estan en la currentProductionPlanDetailList del popup
    	}
		// agregamos el pedido que se eliminó y al ser de un plan que se esta editando, no aparece en la lista
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
			productionPlanService.deleteProductionPlan(currentProductionPlan);
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
		String production_plan_name = productionPlanNameTextbox.getText();
		Date production_plan_date = productionPlanDateBox.getValue();
		int production_plan_state_type_id = productionPlanStateTypeListModel.getElementAt(productionPlanStateTypeSelectBox.getSelectedIndex()).getId();
		if(currentProductionPlan == null) { // es un plan nuevo
			// creamos el nuevo plan
			currentProductionPlan = new ProductionPlan(null, production_plan_name, null, production_plan_date);
			currentProductionPlan = productionPlanService.saveProductionPlan(currentProductionPlan, production_plan_state_type_id, currentProductionPlanDetailList);
		} else { // se edita un plan
			currentProductionPlan.setName(production_plan_name);
			currentProductionPlan.setDate(production_plan_date);
			currentProductionPlan = productionPlanService.updateProductionPlan(currentProductionPlan, production_plan_state_type_id, currentProductionPlanDetailList);
		}
		currentProductionPlan = null;
		currentProductionPlanState = null;
		refreshViewProductionPlan();
		alert("Plan guardado.");
	}
	
	@Listen("onClick = #addOrderButton")
    public void addOrder() {
		// el detalle debe tener id de plan nulo ya que se lo agrega al guardar o actualizar el plan
		currentProductionPlanDetailList.add(new ProductionPlanDetail(null, currentOrder.getId()));
		refreshProductionPlanDetailListGrid();
		currentOrder = null;
		refreshOrderPopupList();
		refreshOrder();
		refreshProcessListBox();
		refreshProductTotalListbox();
    }
	
	@Listen("onRemoveOrder = #productionPlanDetailGrid")
    public void doRemoveOrder(ForwardEvent evt) {
		int idOrder = (Integer) evt.getData();
    	// eliminamos el detalle de la lista
    	for(ProductionPlanDetail auxProductionPlanDetail : currentProductionPlanDetailList) {
    		if(auxProductionPlanDetail.getIdOrder().equals(idOrder)) {
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
  			productionPlanStateTypeListModel.addToSelection(productionPlanStateTypeService.getProductionPlanStateType("iniciado"));
  			productionPlanStateTypeSelectBox.setModel(productionPlanStateTypeListModel);
  			productionPlanNameTextbox.setText("");
  			productionPlanDateBox.setValue(new Date());
  	        currentProductionPlanDetailList = new ArrayList<ProductionPlanDetail>();
  	        deleteProductionPlanButton.setDisabled(true);
  	        productionPlanStateTypeSelectBox.setDisabled(true);
  		} else {// se edita plan de produccion
  			productionPlanCaption.setLabel("Edicion de Plan de Produccion");
  			currentProductionPlanState = productionPlanStateService.getLastProductionPlanState(currentProductionPlan.getId());
  			if(currentProductionPlanState != null) {
  				productionPlanStateTypeListModel.addToSelection(productionPlanStateTypeService.getProductionPlanStateType(currentProductionPlanState.getIdProductionPlanStateType()));
  				productionPlanStateTypeSelectBox.setModel(productionPlanStateTypeListModel);
        	} else {
        		productionPlanStateTypeSelectBox.setSelectedIndex(-1);
        	}
  			if(currentProductionPlan.getName() != null) {
  				productionPlanNameTextbox.setText(currentProductionPlan.getName());
  			} else {
  				productionPlanNameTextbox.setText("");
  			}
  			productionPlanDateBox.setValue(currentProductionPlan.getDate());
  			currentProductionPlanDetailList = productionPlanDetailService.getProductionPlanDetailList(currentProductionPlan.getId());
  			deleteProductionPlanButton.setDisabled(false);
  			productionPlanStateTypeSelectBox.setDisabled(false);
  		}
  		productionPlanDateBox.setDisabled(true);// nunca se debe poder modificar
  		refreshOrderPopupList();
  		refreshProductionPlanDetailListGrid();
  		refreshProductTotalListbox();
  	}
    
	private void refreshProcessListBox() {
		List<Process> processList = new ArrayList<Process>();
		for(ProductionPlanDetail auxProductionPlanDetail : currentProductionPlanDetailList) {
			Order auxOrder = orderService.getOrder(auxProductionPlanDetail.getIdOrder());
			List<OrderDetail> auxOrderDetailList = orderDetailService.getOrderDetailList(auxOrder.getId());
			for(OrderDetail auxOrderDetail : auxOrderDetailList) {
				Product auxProduct = productService.getProduct(auxOrderDetail.getIdProduct());
				List<Piece> auxPieceList = pieceService.getPieceList(auxProduct.getId());
				for(Piece auxPiece : auxPieceList) {
					List<Process> auxProcessList = processService.getProcessList(auxPiece.getId());
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
			Order auxOrder = orderService.getOrder(auxProductionPlanDetail.getIdOrder());// buscamos el pedido referente al detalle del plan de produccion
			List<OrderDetail> orderDetailList =  orderDetailService.getOrderDetailList(auxOrder.getId());// buscamos todos sus detalles
			for(OrderDetail auxOrderDetail : orderDetailList) {// por cada detalle del pedido, observamos si el producto ya esta en la lista, si lo esta sumamos su cantidad y, si no esta lo agregamos
				Boolean is_in_list = false;
				Integer id_product = auxOrderDetail.getIdProduct();
				Integer order_detail_units = auxOrderDetail.getUnits();
				for(ProductTotal productTotal : productTotalList) {
					if(productTotal.getId().equals(id_product)) {// si esta
						is_in_list = true;
						productTotal.setTotalUnits(productTotal.getTotalUnits() + order_detail_units);// sumamos su cantidad con la existente
						break;
					}
				}
				if(is_in_list == false) {// no esta, por lo tanto agregamos el producto a la lista total
					ProductTotal productTotal = new ProductTotal(productService.getProduct(id_product));
					productTotal.setTotalUnits(order_detail_units);// el primer valor son el total de unidades del detalle de pedido
					productTotalList.add(productTotal);
				}
			}
			// si es el primer loop del productionPlanDetailList entonces la lista productTotalList deberia estar llena solo con los productos
			// del primer pedido, en el siguiente loop se sumaran los que ya estan y agregaran los nuevos
		}
		// aqui deberia llegar con el productTotalList lleno con todos los productos sin repetir y con el total, que conforman el plan de produccion
	}
	
	public String getClientName(int idClient) {
		Client aux = clientService.getClient(idClient);
		return aux.getName();
    }
	
	public String getClientNameByOrderId(int idOrder) {
		Order auxOrder = orderService.getOrder(idOrder);
		Client auxClient = clientService.getClient(auxOrder.getIdClient());
		return auxClient.getName();
    }
	
	public int getProductTotalUnits(int idProduct) {
		int product_total_units = 0;
		for(ProductTotal productTotal : productTotalList) {// buscamos el total de unidades
			if(productTotal.getId().equals(idProduct)) {
				product_total_units = productTotal.getTotalUnits();
			}
		}
		return product_total_units;
    }
	
	public BigDecimal getProductTotalPrice(int idProduct) {
		int product_total_units = getProductTotalUnits(idProduct);
    	return getTotalPrice(product_total_units, productService.getProduct(idProduct).getPrice());// esta funcion es incorrecta pq agarra el valor actual del producto cuando deberia ser el valor en el pedido
    }
	
	private BigDecimal getTotalPrice(int units, BigDecimal price) {
		if(price == null) {
			price = new BigDecimal("0");
		}
    	return price.multiply(new BigDecimal(units));
    }
	
	public String getProductNameByPieceId(int idPiece) {
		Piece aux = pieceService.getPiece(idPiece);
		Product aux2 = productService.getProduct(aux.getIdProduct());
		return aux2.getName();
    }
	
	public String getPieceName(int idPiece) {
		Piece aux = pieceService.getPiece(idPiece);
		return aux.getName();
    }
	
	public String getProcessName(int idProcessType) {
		ProcessType aux = processTypeService.getProcessType(idProcessType);
		return aux.getName();
    }
	
	public Date getOrderDate(int idOrder) {
		Order aux = orderService.getOrder(idOrder);
		return aux.getDate();
    }
	
	public String getPieceUnits(int idPiece) {
		Piece aux = pieceService.getPiece(idPiece);
		return aux.getUnits() + "";
    }
	
	public String getPieceTotalUnits(int idPiece) {
		Piece auxPiece = pieceService.getPiece(idPiece);
		int units = 0;
		for(ProductionPlanDetail auxProductionPlanDetail : currentProductionPlanDetailList) {
			Order auxOrder = orderService.getOrder(auxProductionPlanDetail.getIdOrder());
			List<OrderDetail> auxOrderDetailList = orderDetailService.getOrderDetailList(auxOrder.getId());
			for(OrderDetail auxOrderDetail : auxOrderDetailList) {
				if(auxOrderDetail.getIdProduct().equals(auxPiece.getIdProduct())) {
					units = auxOrderDetail.getUnits();
				}
			}
		}
		if(units > 0) {
			units = auxPiece.getUnits() * units;
		}
		return units + "";
    }
	
	public String getTotalTime(int idPiece, int idProcessType) {
		Process auxProcess = processService.getProcess(idPiece, idProcessType);
		Piece auxPiece = pieceService.getPiece(idPiece);
		long total = 0;
		int units = 0;
		for(ProductionPlanDetail auxProductionPlanDetail : currentProductionPlanDetailList) {
			Order auxOrder = orderService.getOrder(auxProductionPlanDetail.getIdOrder());
			List<OrderDetail> auxOrderDetailList = orderDetailService.getOrderDetailList(auxOrder.getId());
			for(OrderDetail auxOrderDetail : auxOrderDetailList) {
				if(auxOrderDetail.getIdProduct().equals(auxPiece.getIdProduct())) {
					units = auxOrderDetail.getUnits();
				}
			}
		}
		if(units > 0) {
			total = auxProcess.getTime().getMinutes() * units;
		}
		return total + "";
    }
	
	public String quantityOfDetail(int idOrder) {
    	return orderDetailService.getOrderDetailList(idOrder).size() + "";
    }
	
	public ListModel<OrderDetail> getOrderDetailList(int idOrder) {
		List<OrderDetail> orderDetailList = orderDetailService.getOrderDetailList(idOrder);
		return new ListModelList<OrderDetail>(orderDetailList);
    }
	
	public String getProductName(int idProduct) {
    	return productService.getProduct(idProduct).getName();
    }
    
    public String getProductCode(int idProduct) {
    	return productService.getProduct(idProduct).getCode();
    }
	
}
