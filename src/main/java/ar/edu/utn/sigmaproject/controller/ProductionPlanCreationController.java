package ar.edu.utn.sigmaproject.controller;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Button;

import ar.edu.utn.sigmaproject.domain.Client;
import ar.edu.utn.sigmaproject.domain.MeasureUnit;
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
import ar.edu.utn.sigmaproject.service.PieceService;
import ar.edu.utn.sigmaproject.service.ProcessService;
import ar.edu.utn.sigmaproject.service.ProcessTypeService;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.ProductionPlanService;
import ar.edu.utn.sigmaproject.service.ProductionPlanDetailService;
import ar.edu.utn.sigmaproject.service.impl.ClientServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderDetailServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.PieceServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProcessServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProcessTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanDetailServiceImpl;

public class ProductionPlanCreationController extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;
	
	@Wire
    Listbox productPopupListbox;
	@Wire
    Listbox productionPlanProductListbox;
	@Wire
	Bandbox productBandbox;
	@Wire
	Intbox productUnits;
	@Wire
	Button addProductButton;
	@Wire
    Listbox processListbox;
	@Wire
    Listbox supplyListbox;
	
	// services
	OrderService orderService = new OrderServiceImpl();
	OrderDetailService orderDetailService = new OrderDetailServiceImpl();
	ProductService productService = new ProductServiceImpl();
	ProductionPlanService productionPlanService = new ProductionPlanServiceImpl();
	ProductionPlanDetailService productionPlanDetailService = new ProductionPlanDetailServiceImpl();
	PieceService pieceService = new PieceServiceImpl();
	ProcessService processService = new ProcessServiceImpl();
	ProcessTypeService processTypeService = new ProcessTypeServiceImpl();
	ClientService clientService = new ClientServiceImpl();
	//SupplyService supplyListService = new SupplyServiceImpl();
	
	// list models
	ListModelList<Product> productListModel;
	ListModelList<Process> processListModel;
	//ListModelList<Product> supplyListModel;
    ListModelList<ProductionPlanDetail> productionPlanDetailListModel;
    ListModelList<OrderDetail> orderDetailListModel;
    
    // atributes
    private Product selectedProduct;
    private Client selectedClient;
    private Order selectedOrder;
    private OrderDetail selectedOrderDetail;
    private List<Product> productList;
    private List<Process> processList;
	private List<ProductionPlanDetail> productionPlanDetailList;
	private List<OrderDetail> orderDetailList;
	
	@Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        //productList = productService.getProductList();
        //productListModel = new ListModelList<Product>(productList);
        orderDetailList = orderDetailService.getOrderDetailList();
        orderDetailListModel = new ListModelList<OrderDetail>(orderDetailList);
        productPopupListbox.setModel(orderDetailListModel);
        
        selectedProduct = null;
        productionPlanDetailList = new ArrayList<ProductionPlanDetail>();
        productionPlanDetailListModel = new ListModelList<ProductionPlanDetail>(productionPlanDetailList);
        productionPlanProductListbox.setModel(productionPlanDetailListModel);
        
        processList = new ArrayList<Process>();
        processListModel = new ListModelList<Process>(processList);
        processListbox.setModel(processListModel);
    }
	
	@Listen("onSelect = #productPopupListbox")
    public void selectionProductPopupListbox() {
		selectedOrderDetail = (OrderDetail) productPopupListbox.getSelectedItem().getValue();
		selectedOrder = orderService.getOrder(selectedOrderDetail.getIdOrder());
		selectedProduct = productService.getProduct(selectedOrderDetail.getIdProduct());
		selectedClient = clientService.getClient(selectedOrder.getIdClient());
		productBandbox.setValue(selectedProduct.getName());
		productBandbox.close();
		productUnits.setValue(selectedOrderDetail.getUnits());
    }
	
	@Listen("onClick = #addProductButton")
    public void addProductToProdPlan() {
		if(productUnits.getValue()==null || productUnits.getValue().intValue()<=0){
			Clients.showNotification("Ingresar Cantidad del Producto",productUnits);
			return;
		}
		productListModel.remove(selectedProduct);
		productPopupListbox.setModel(productListModel);
		ProductionPlanDetail aux = new ProductionPlanDetail(null, selectedOrder.getId());
		productionPlanDetailList.add(aux);
		productionPlanDetailListModel.add(aux);
		productionPlanProductListbox.setModel(productionPlanDetailListModel);
		
		// luego de actualizar la tabla borramos el text del producto  seleccionado
		// deseleccionamos la tabla y borramos la cantidad
		productBandbox.setValue("");
		productPopupListbox.clearSelection();
		productUnits.setText(null);
		refreshProcessListBox();
    }
	
	private void refreshProcessListBox() {
		processList = new ArrayList<Process>();
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
	
	public String getClientName(int idClient) {
		Client aux = clientService.getClient(idClient);
		return aux.getName();
    }
	
	public String getClientNameByOrderId(int idOrder) {
		Order auxOrder = orderService.getOrder(idOrder);
		Client auxClient = clientService.getClient(auxOrder.getIdClient());
		return auxClient.getName();
    }
	
	public String getProductName(int idProduct) {
		Product aux = productService.getProduct(idProduct);
		return aux.getName();
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
}
