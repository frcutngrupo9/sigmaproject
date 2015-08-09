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

import ar.edu.utn.sigmaproject.domain.MeasureUnit;
import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanDetail;
import ar.edu.utn.sigmaproject.service.PieceService;
import ar.edu.utn.sigmaproject.service.ProcessService;
import ar.edu.utn.sigmaproject.service.ProcessTypeService;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.ProductionPlanService;
import ar.edu.utn.sigmaproject.service.ProductionPlanDetailService;
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
	ProductService productService = new ProductServiceImpl();
	ProductionPlanService productionPlanService = new ProductionPlanServiceImpl();
	ProductionPlanDetailService productionPlanDetailService = new ProductionPlanDetailServiceImpl();
	PieceService pieceService = new PieceServiceImpl();
	ProcessService processService = new ProcessServiceImpl();
	ProcessTypeService processTypeService = new ProcessTypeServiceImpl();
	//SupplyService supplyListService = new SupplyServiceImpl();
	
	// list models
	ListModelList<Product> productListModel;
	ListModelList<Process> processListModel;
	//ListModelList<Product> supplyListModel;
    ListModelList<ProductionPlanDetail> productionPlanDetailListModel;
    
    // atributes
    private Product selectedProduct;
    private List<Product> productList;
    private List<Process> processList;
	private List<ProductionPlanDetail> productionPlanDetailList;
	
	@Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        productList = productService.getProductList();
        productListModel = new ListModelList<Product>(productList);
        productPopupListbox.setModel(productListModel);
        
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
		selectedProduct = (Product) productPopupListbox.getSelectedItem().getValue();
		productBandbox.setValue(selectedProduct.getName());
		productBandbox.close();
    }
	
	@Listen("onClick = #addProductButton")
    public void addProductToProdPlan() {
		if(productUnits.getValue()==null || productUnits.getValue().intValue()<=0){
			Clients.showNotification("Ingresar Cantidad del Producto",productUnits);
			return;
		}
		productListModel.remove(selectedProduct);
		productPopupListbox.setModel(productListModel);
		ProductionPlanDetail aux = new ProductionPlanDetail(null,selectedProduct.getId(),productUnits.getValue());
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
			Product auxProduct = productService.getProduct(auxProductionPlanDetail.getIdProduct());
			List<Piece> auxPieceList = pieceService.getPieceList(auxProduct.getId());
			for(Piece auxPiece : auxPieceList) {
				List<Process> auxProcessList = processService.getProcessList(auxPiece.getId());
				for(Process auxProcess : auxProcessList) {
					processList.add(auxProcess);
				}
			}
		}
		processListModel = new ListModelList<Process>(processList);
        processListbox.setModel(processListModel);
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
			if(auxProductionPlanDetail.getIdProduct().compareTo(auxPiece.getIdProduct()) == 0) {
				units = auxProductionPlanDetail.getUnits();
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
			if(auxProductionPlanDetail.getIdProduct().compareTo(auxPiece.getIdProduct()) == 0) {
				units = auxProductionPlanDetail.getUnits();
			}
		}
		if(units > 0) {
			total = auxProcess.getTime().getMinutes() * units;
		}
		return total + "";
    }
}
