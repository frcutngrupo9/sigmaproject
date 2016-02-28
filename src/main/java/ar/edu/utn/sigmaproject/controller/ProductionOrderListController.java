package ar.edu.utn.sigmaproject.controller;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.Duration;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductTotal;
import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.Worker;
import ar.edu.utn.sigmaproject.service.PieceService;
import ar.edu.utn.sigmaproject.service.ProcessService;
import ar.edu.utn.sigmaproject.service.ProcessTypeService;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.ProductionOrderDetailService;
import ar.edu.utn.sigmaproject.service.ProductionOrderService;
import ar.edu.utn.sigmaproject.service.ProductionPlanDetailService;
import ar.edu.utn.sigmaproject.service.WorkerService;
import ar.edu.utn.sigmaproject.service.impl.PieceServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProcessServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProcessTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionOrderDetailServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionOrderServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanDetailServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.WorkerServiceImpl;

public class ProductionOrderListController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox productionPlanNameTextbox;
	@Wire
	Datebox productionPlanDatebox;
	@Wire
	Grid productionOrderGrid;

	// services
	private ProductionOrderService productionOrderService = new ProductionOrderServiceImpl();
	private ProductionOrderDetailService productionOrderDetailService = new ProductionOrderDetailServiceImpl();
	private ProductService productService = new ProductServiceImpl();
	private PieceService pieceService = new PieceServiceImpl();
	private ProcessService processService = new ProcessServiceImpl();
	private ProductionPlanDetailService productionPlanDetailService = new ProductionPlanDetailServiceImpl();
	private WorkerService workerService = new WorkerServiceImpl();
	private ProcessTypeService processTypeService = new ProcessTypeServiceImpl();

	// atributes
	private ProductionPlan currentProductionPlan;

	// list
	private List<ProductionOrder> productionOrderList;
	private List<ProductTotal> productTotalList;

	// list models
	private ListModelList<ProductionOrder> productionOrderListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);

		currentProductionPlan = (ProductionPlan) Executions.getCurrent().getAttribute("selected_production_plan");

		if(currentProductionPlan != null) {
			Integer idProductionPlan = currentProductionPlan.getId();
			productionOrderList = productionOrderService.getProductionOrderList(idProductionPlan);
			ArrayList<ProductTotal> productTotalList = productionPlanDetailService.getProductTotalList(idProductionPlan);
			if(productionOrderList.isEmpty() || productionOrderList.size()<productTotalList.size()) {// se deben crear las ordenes de produccion
				for(ProductTotal productTotal : productTotalList) {
					Integer idProduct = productTotal.getId();
					boolean isInDetail = false;
					for(ProductionOrder productionOrder:productionOrderList) {
						if(productionOrder.getIdProduct().equals(idProduct)) {
							isInDetail = true;
							break;
						}
					}
					if(isInDetail == false) {// si el producto no se encuentra en ningun detalle quiere decir que el detalle no esta creado aun
						Integer productUnits = productTotal.getTotalUnits();
						Integer idWorker = null;
						productionOrderList.add(new ProductionOrder(null, idProductionPlan, idProduct, idWorker, null, productUnits, null, null));
					}
				}
			}

		} else {
			productionOrderList = new ArrayList<ProductionOrder>();
		}
		productionOrderListModel = new ListModelList<ProductionOrder>(productionOrderList);
		productionOrderGrid.setModel(productionOrderListModel);

		refreshView();
	}

	private void refreshView() {
		productionPlanNameTextbox.setDisabled(true);
		productionPlanDatebox.setDisabled(true);
		if(currentProductionPlan != null) {
			productionPlanNameTextbox.setText(currentProductionPlan.getName());
			productionPlanDatebox.setValue(currentProductionPlan.getDate());
		}

	}

	public Product getProduct(int idProduct) {
		return productService.getProduct(idProduct);
	}

	public String getProductUnits(int idProduct) {
		int product_units = 0;
		for(ProductTotal productTotal : productTotalList) {
			if(productTotal.getId().equals(idProduct)) {
				product_units = productTotal.getTotalUnits();
			}
		}
		return "" + product_units;
	}

	public String getWorkerName(int idWorker) {
		Worker aux = workerService.getWorker(idWorker);
		if(aux != null) {
			return aux.getName();
		} else {
			return "[sin empleado]";
		}
	}


	@Listen("onEditProductionOrder = #productionOrderGrid")
	public void doEditProductionOrder(ForwardEvent evt) {
		ProductionOrder productionOrder = (ProductionOrder) evt.getData();
		Executions.getCurrent().setAttribute("selected_production_order", productionOrder);
		Include include = (Include) Selectors.iterable(evt.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/production_order_creation.zul");
	}

	public ListModel<Process> getProductionOrderProcesses(Integer idProduct) {// buscar todos los procesos del producto
		List<Process> list = new ArrayList<Process>();
		List<Piece> auxPieceList = pieceService.getPieceList(idProduct);
		for(Piece piece : auxPieceList) {
			List<Process> auxProcessList = processService.getProcessList(piece.getId());
			for(Process process : auxProcessList) {
				list.add(Process.clone(process));
			}
		}
		return new ListModelList<Process>(list);
	}

	public ListModel<ProductionOrderDetail> getProductionOrderDetailList(Integer idProductionOrder) {// buscar todos los procesos del producto
		List<ProductionOrderDetail> list = productionOrderDetailService.getProductionOrderDetailList(idProductionOrder);
		return new ListModelList<ProductionOrderDetail>(list);
	}

	public ProcessType getProcessType(int idProduct) {
		return processTypeService.getProcessType(idProduct);
	}

	public Piece getPiece(int idPiece) {
		return pieceService.getPiece(idPiece);
	}

	public ProcessType getProcessTypeByProcessId(int idProcess) {
		return processTypeService.getProcessType(getProcessByProcessId(idProcess).getIdProcessType());
	}

	public Piece getPieceByProcessId(int idProcess) {
		return pieceService.getPiece(getProcessByProcessId(idProcess).getIdPiece());
	}

	public Process getProcessByProcessId(int idProcess) {
		return processService.getProcess(idProcess);
	}

	public String getIsFinished(boolean value) {
		if(value == true) {
			return "si";
		} else {
			return "no";
		}
	}

	public String getFormatedTime(Duration time) {
		return String.format("Dias: %d Horas: %d Minutos: %d", time.getDays(), time.getHours(), time.getMinutes());
	}

	public String getPercentComplete(int idProductionOrder) {
		ProductionOrder aux = productionOrderService.getProductionOrder(idProductionOrder);
		if(aux != null) {
			List<ProductionOrderDetail> productionOrderDetailList = productionOrderDetailService.getProductionOrderDetailList(aux.getId());
			int quantityFinished = 0;
			for(ProductionOrderDetail productionOrderDetail : productionOrderDetailList) {
				if(productionOrderDetail.isFinished()) {
					quantityFinished += 1;
				}
			}
			double percentComplete;
			if(productionOrderDetailList.size() == 0) {
				percentComplete = 0;
			} else {
				percentComplete = (quantityFinished * 100) / productionOrderDetailList.size();
			}
			return percentComplete + "";
		} else {
			return "0";
		}
	}
}
