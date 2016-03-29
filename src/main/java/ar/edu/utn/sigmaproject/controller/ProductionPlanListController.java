package ar.edu.utn.sigmaproject.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductTotal;
import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanState;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.domain.Worker;
import ar.edu.utn.sigmaproject.service.ProductionOrderDetailService;
import ar.edu.utn.sigmaproject.service.ProductionOrderService;
import ar.edu.utn.sigmaproject.service.ProductionPlanDetailService;
import ar.edu.utn.sigmaproject.service.ProductionPlanService;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateService;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeService;
import ar.edu.utn.sigmaproject.service.WorkerService;
import ar.edu.utn.sigmaproject.service.impl.ProductionOrderDetailServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionOrderServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanDetailServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanStateServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanStateTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.WorkerServiceImpl;

public class ProductionPlanListController  extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;

	@Wire
	Grid productionPlanGrid;
	@Wire
	Grid productionOrderGrid;
	@Wire
	Button newButton;

	// services
	private ProductionPlanService productionPlanService = new ProductionPlanServiceImpl();
	private ProductionPlanDetailService productionPlanDetailService = new ProductionPlanDetailServiceImpl();
	private ProductionPlanStateService productionPlanStateService = new ProductionPlanStateServiceImpl();
	private ProductionPlanStateTypeService productionPlanStateTypeService = new ProductionPlanStateTypeServiceImpl();
	private ProductionOrderService productionOrderService = new ProductionOrderServiceImpl();
	private ProductionOrderDetailService productionOrderDetailService = new ProductionOrderDetailServiceImpl();
	private WorkerService workerService = new WorkerServiceImpl();

	// list
	private List<ProductionPlan> productionPlanList;

	// list models
	private ListModelList<ProductionPlan> productionPlanListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		productionPlanList = productionPlanService.getProductionPlanList();
		productionPlanListModel = new ListModelList<ProductionPlan>(productionPlanList);
		productionPlanGrid.setModel(productionPlanListModel);
	}

	@Listen("onEditProductionPlan = #productionPlanGrid")
	public void doEditProductionPlan(ForwardEvent evt) {
		ProductionPlan productionPlan = (ProductionPlan) evt.getData();
		Executions.getCurrent().setAttribute("selected_production_plan", productionPlan);
		Include include = (Include) Selectors.iterable(evt.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/production_plan_creation.zul");
	}

	public String getQuantityOfOrder(int idProductionPlan) {
		return productionPlanDetailService.getProductionPlanDetailList(idProductionPlan).size() + "";// porque hay 1 pedido por detalle
	}

	public String getQuantityOfProduct(int idProductionPlan) {
		return productionPlanDetailService.getProductTotalList(idProductionPlan).size() + "";
	}
	
	public String getProductionPlanStateName(int idProductionPlan) {
		ProductionPlanState lastState = productionPlanStateService.getLastProductionPlanState(idProductionPlan);
		if(lastState != null) {
			ProductionPlanStateType aux = productionPlanStateTypeService.getProductionPlanStateType(lastState.getIdProductionPlanStateType());
			return aux.getName();
		} else {
			return "[sin estado]";
		}
	}
	
	public ProductionOrder getProductionOrder(ProductionPlan productionPlan, Product product) {
		return productionOrderService.getProductionOrder(productionPlan, product);
	}
	
	public String getProductionOrderState(ProductionPlan productionPlan, Product product) {
		ProductionOrder aux = productionOrderService.getProductionOrder(productionPlan, product);
		if(aux == null) {
			return "No Generado";
		} else {
			if(aux.getState() == null) {
				return "Generado";
			} else {
				return aux.getState().name();
			}
		}
		
	}
	public String getProductionOrderNumber(ProductionPlan productionPlan, Product product) {
		ProductionOrder aux = productionOrderService.getProductionOrder(productionPlan, product);
		if(aux == null) {
			return "";
		} else {
			return aux.getNumber() + "";
		}
	}
	
	public String getWorkerName(ProductionPlan productionPlan, Product product) {
		ProductionOrder aux = productionOrderService.getProductionOrder(productionPlan, product);
		if(aux == null) {
			return "";
		} else {
			Worker worker = workerService.getWorker(aux.getIdWorker());
			if(worker != null) {
				return worker.getName();
			} else {
				return "[no asignado]";
			}
		}
	}
	
	public String getPercentComplete(ProductionPlan productionPlan, Product product) {
		ProductionOrder aux = productionOrderService.getProductionOrder(productionPlan, product);
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
			return percentComplete + " %";
		} else {
			return "";
		}
	}
	
	public String getProductionOrderDate(ProductionPlan productionPlan, Product product) {
		ProductionOrder aux = productionOrderService.getProductionOrder(productionPlan, product);
		if(aux == null) {
			return "";
		} else {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			String productionOrderDate = df.format(aux.getDate());
			return productionOrderDate;
		}
	}
	
	public String getProductionOrderDateFinished(ProductionPlan productionPlan, Product product) {
		ProductionOrder aux = productionOrderService.getProductionOrder(productionPlan, product);
		if(aux == null) {
			return "";
		} else {
			if(aux.getDateFinished() != null) {
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				String productionOrderDateFinished = df.format(aux.getDateFinished());
				return productionOrderDateFinished;
			} else {
				return "No Finalizado";
			}
			
		}
	}

	@Listen("onClick = #newButton")
	public void goToProductionPlanCreation() {
		Executions.getCurrent().setAttribute("selected_production_plan", null);
		Include include = (Include) Selectors.iterable(productionPlanGrid.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/production_plan_creation.zul");
	}

	public ListModel<ProductTotal> getProductionPlanProducts(ProductionPlan productionPlan) {
		ArrayList<ProductTotal> productTotalList = productionPlanDetailService.getProductTotalList(productionPlan.getId());
		return new ListModelList<ProductTotal>(productTotalList);
	}

	@Listen("onGenerateProductionOrder = #productionPlanGrid")
	public void goToProductionOrderList(ForwardEvent evt) {
		ProductionPlan productionPlan = (ProductionPlan) evt.getData();
		Executions.getCurrent().setAttribute("selected_production_plan", productionPlan);
		Include include = (Include) Selectors.iterable(evt.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/production_order_list.zul");
	}

	@Listen("onOpenRequirementPlan = #productionPlanGrid")
	public void goToRequirementPlanCreation(ForwardEvent evt) {
		ProductionPlan productionPlan = (ProductionPlan) evt.getData();
		Executions.getCurrent().setAttribute("selected_production_plan", productionPlan);
		Include include = (Include) Selectors.iterable(evt.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/requirement_plan_creation.zul");
	}
}
