package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Button;

import ar.edu.utn.sigmaproject.domain.Client;
import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanDetail;
import ar.edu.utn.sigmaproject.service.ClientService;
import ar.edu.utn.sigmaproject.service.OrderDetailService;
import ar.edu.utn.sigmaproject.service.OrderService;
import ar.edu.utn.sigmaproject.service.OrderStateService;
import ar.edu.utn.sigmaproject.service.OrderStateTypeService;
import ar.edu.utn.sigmaproject.service.PieceService;
import ar.edu.utn.sigmaproject.service.ProcessService;
import ar.edu.utn.sigmaproject.service.ProcessTypeService;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.ProductionPlanService;
import ar.edu.utn.sigmaproject.service.ProductionPlanDetailService;
import ar.edu.utn.sigmaproject.service.impl.ClientServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderDetailServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderStateServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderStateTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.PieceServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProcessServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProcessTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanDetailServiceImpl;

public class ProductionPlanCreationController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;
	
	@Wire
    Listbox orderPopupListbox;
	@Wire
	Grid productionPlanDetailGrid;
	@Wire
	Bandbox orderBandbox;
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
	
	// services
	private OrderService orderService = new OrderServiceImpl();
	private OrderDetailService orderDetailService = new OrderDetailServiceImpl();
	private OrderStateService orderStateService = new OrderStateServiceImpl();
	private OrderStateTypeService orderStateTypeService = new OrderStateTypeServiceImpl();
	private ProductService productService = new ProductServiceImpl();
	private ProductionPlanService productionPlanService = new ProductionPlanServiceImpl();
	private ProductionPlanDetailService productionPlanDetailService = new ProductionPlanDetailServiceImpl();
	private PieceService pieceService = new PieceServiceImpl();
	private ProcessService processService = new ProcessServiceImpl();
	private ProcessTypeService processTypeService = new ProcessTypeServiceImpl();
	private ClientService clientService = new ClientServiceImpl();
	
	// list
	private List<Order> orderPopupList;
    private List<ProductionPlanDetail> productionPlanDetailList;
	private List<ProductionPlanDetail> lateDeleteProductionPlanDetailList;
	private List<ProductTotal> productTotalList;
	
	// list models
	private ListModelList<Order> orderPopupListModel;
	private ListModelList<Process> processListModel;
	private ListModelList<ProductionPlanDetail> productionPlanDetailListModel;
    
    // atributes
    private Order currentOrder;
    private ProductionPlan currentProductionPlan;
    
	@Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        
        productTotalList = new ArrayList<ProductTotal>();
        productionPlanDetailList = new ArrayList<ProductionPlanDetail>();
        
        currentProductionPlan = (ProductionPlan) Executions.getCurrent().getAttribute("selected_production_plan");
        refreshViewProductionPlan();
        
        lateDeleteProductionPlanDetailList = new ArrayList<ProductionPlanDetail>();
        
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
			orderBandbox.setValue("Pedido nro " + currentOrder.getId());
			orderBandbox.close();
		}else {
			orderBandbox.setValue("");// borramos el text del producto  seleccionado
			orderPopupListbox.clearSelection();// deseleccionamos la tabla
		}
    }
	
	private void refreshOrderPopupList() {// el popup se actualiza en base a los detalles
		Integer order_state_type_id = orderStateTypeService.getOrderStateType("iniciado").getId();// se busca el id del estado de pedido iniciado
		orderPopupList = orderService.getOrderList(order_state_type_id);// se buscan los pedidos que no estan asignados a un plan y no estan cancelados (estan en estado iniciado)
    	for(ProductionPlanDetail productionPlanDetail : productionPlanDetailList) {// no debe contener los pedidos que ya estan en el detalle
    		Order aux = orderService.getOrder(productionPlanDetail.getIdOrder());
    		orderPopupList.remove(aux);// sacamos todos los pedidos del popup
    	}
    	orderPopupListModel = new ListModelList<Order>(orderPopupList);
        orderPopupListbox.setModel(orderPopupListModel);
	}
	
	@Listen("onClick = #resetProductionPlanButton")
    public void resetProductionPlan() {
		refreshViewProductionPlan();
    }
	
	@Listen("onClick = #addOrderButton")
    public void addOrder() {
		if(currentOrder == null) {
			Clients.showNotification("Debe seleccionar un pedido", orderBandbox);
			return;
		}
		// buscamos si el pedido no esta en un detalle eliminado
		ProductionPlanDetail aux = null;
		for(ProductionPlanDetail lateDeleteProductionPlanDetail : lateDeleteProductionPlanDetailList) {
			if(currentOrder.getId().equals(lateDeleteProductionPlanDetail.getIdOrder())) {
				aux = lateDeleteProductionPlanDetail;
			}
		}
		if(aux != null) {
			lateDeleteProductionPlanDetailList.remove(aux);// lo eliminamos de la lista de eliminacion tardia (porque el pedido sera agregado de nuevo, y no se eliminara tardiamente)
		}
		if(currentProductionPlan == null) {// es un plan de produccion nuevo
			productionPlanDetailList.add(new ProductionPlanDetail(null, currentOrder.getId()));
		} else {
			productionPlanDetailList.add(new ProductionPlanDetail(currentProductionPlan.getId(), currentOrder.getId()));
		}
		
		refreshProductionPlanDetailListGrid();
		currentOrder = null;
		
		refreshOrderPopupList();
		refreshOrder();
		refreshProcessListBox();
		refreshProductTotalListbox();
    }
	
	private void refreshProductionPlanDetailListGrid() {
		productionPlanDetailListModel = new ListModelList<ProductionPlanDetail>(productionPlanDetailList);
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
  			productionPlanDateBox.setValue(new Date());
  	        productionPlanDetailList = new ArrayList<ProductionPlanDetail>();
  	        deleteProductionPlanButton.setDisabled(true);
  		} else {// se edita plan de produccion
  			productionPlanDateBox.setValue(currentProductionPlan.getDate());
  			productionPlanDetailList = productionPlanDetailService.getProductionPlanDetailList(currentProductionPlan.getId());
  			deleteProductionPlanButton.setDisabled(false);
  		}
  		refreshOrderPopupList();
  		refreshProductionPlanDetailListGrid();
  	}
    
	private void refreshProcessListBox() {
		List<Process> processList = new ArrayList<Process>();
		for(ProductionPlanDetail auxProductionPlanDetail : productionPlanDetailList) {
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
		for(ProductionPlanDetail auxProductionPlanDetail : productionPlanDetailList) {
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
						//(!) no muy seguro si las modificaciones de los objetos en este for afectan a los que estan adentro de la lista(!)
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
	
	public String getPieceUnits(int idPiece) {
		Piece aux = pieceService.getPiece(idPiece);
		return aux.getUnits() + "";
    }
	
	public String getPieceTotalUnits(int idPiece) {
		Piece auxPiece = pieceService.getPiece(idPiece);
		int units = 0;
		for(ProductionPlanDetail auxProductionPlanDetail : productionPlanDetailList) {
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
		for(ProductionPlanDetail auxProductionPlanDetail : productionPlanDetailList) {
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
	
	class ProductTotal extends Product {
		
		private static final long serialVersionUID = 1L;
		Integer totalUnits;
		
		public ProductTotal(Product product) {
			super(product.getId(), product.getCode(), product.getName(), product.getDetails(), product.getPrice());
		}
		
		public Integer getTotalUnits() {
			return totalUnits;
		}

		public void setTotalUnits(Integer totalUnits) {
			this.totalUnits = totalUnits;
		}
		
	}
	
}
