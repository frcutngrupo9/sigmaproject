package ar.edu.utn.sigmaproject.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
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
import ar.edu.utn.sigmaproject.service.ProductionOrderRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeRepository;
import ar.edu.utn.sigmaproject.service.WorkerRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProductionPlanListController  extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;

	@Wire
	Grid productionPlanGrid;
	@Wire
	Grid productionOrderGrid;
	@Wire
	Button newButton;

	// services
	@WireVariable
	private ProductionOrderRepository productionOrderRepository;
	@WireVariable
	private ProductionPlanRepository productionPlanRepository;
	@WireVariable
	private ProductionPlanStateTypeRepository productionPlanStateTypeRepository;
	@WireVariable
	private WorkerRepository workerRepository;

	// list
	private List<ProductionPlan> productionPlanList;

	// list models
	private ListModelList<ProductionPlan> productionPlanListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		productionPlanList = productionPlanRepository.findAll();
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

	public String getQuantityOfProduct(ProductionPlan productionPlan) {
		return this.getProductionPlanProducts(productionPlan).getSize() + "";
	}

	public String getProductionPlanStateName(ProductionPlan productionPlan) {
		return productionPlan.getCurrentStateType() != null ? productionPlan.getCurrentStateType().getName() : "[sin estado]";
	}

	public ProductionOrder getProductionOrder(ProductionPlan productionPlan, Product product) {
		return productionOrderRepository.findByProductionPlanAndProduct(productionPlan, product);
	}

	public String getProductionOrderState(ProductionPlan productionPlan, Product product) {
		ProductionOrder aux = productionOrderRepository.findByProductionPlanAndProduct(productionPlan, product);
		if(aux == null) {
			return "No Generado";
		} else {
			if(aux.getState() == null) {
				return "Generado";
			} else {
				return aux.getState().getName();
			}
		}

	}
	public String getProductionOrderNumber(ProductionPlan productionPlan, Product product) {
		ProductionOrder aux = productionOrderRepository.findByProductionPlanAndProduct(productionPlan, product);
		if(aux == null) {
			return "";
		} else {
			return aux.getNumber() + "";
		}
	}

	public String getWorkerName(ProductionPlan productionPlan, Product product) {
		ProductionOrder aux = productionOrderRepository.findByProductionPlanAndProduct(productionPlan, product);
		if(aux == null) {
			return "";
		} else {
			if(aux.getWorker() != null) {
				return aux.getWorker().getName();
			} else {
				return "[no asignado]";
			}
		}
	}

	public String getPercentComplete(ProductionPlan productionPlan, Product product) {
		ProductionOrder aux = productionOrderRepository.findByProductionPlanAndProduct(productionPlan, product);
		if(aux != null) {
			List<ProductionOrderDetail> productionOrderDetailList = aux.getDetails();
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
		ProductionOrder aux = productionOrderRepository.findByProductionPlanAndProduct(productionPlan, product);
		if(aux == null) {
			return "";
		} else {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			String productionOrderDate = df.format(aux.getDate());
			return productionOrderDate;
		}
	}

	public String getProductionOrderDateFinished(ProductionPlan productionPlan, Product product) {
		ProductionOrder aux = productionOrderRepository.findByProductionPlanAndProduct(productionPlan, product);
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
		return new ListModelList<ProductTotal>(productionPlan.getProductTotalList());
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
