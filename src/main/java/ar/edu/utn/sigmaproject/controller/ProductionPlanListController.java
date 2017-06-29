package ar.edu.utn.sigmaproject.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.zkoss.zul.ListModelList;

import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductTotal;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanDetail;
import ar.edu.utn.sigmaproject.service.ProductRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProductionPlanListController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Grid productionPlanGrid;
	@Wire
	Grid productionOrderGrid;
	@Wire
	Button newButton;

	// services
	@WireVariable
	private ProductionPlanRepository productionPlanRepository;
	@WireVariable
	private ProductRepository productRepository;

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
		return getProductTotalList(productionPlan).size() + "";
	}

	@Listen("onClick = #newButton")
	public void goToProductionPlanCreation() {
		Executions.getCurrent().setAttribute("selected_production_plan", null);
		Include include = (Include) Selectors.iterable(productionPlanGrid.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/production_plan_creation.zul");
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

	private List<ProductTotal> getProductTotalList(ProductionPlan productionPlan) {
		List<ProductionPlanDetail> productionPlanDetailList = productionPlan.getPlanDetails();
		Map<Product, Integer> productTotalMap = new HashMap<Product, Integer>();
		for(ProductionPlanDetail auxProductionPlanDetail : productionPlanDetailList) {
			for(OrderDetail auxOrderDetail : auxProductionPlanDetail.getOrder().getDetails()) {
				Integer totalUnits = productTotalMap.get(productRepository.findOne(auxOrderDetail.getProduct().getId()));
				productTotalMap.put(productRepository.findOne(auxOrderDetail.getProduct().getId()), (totalUnits == null) ? auxOrderDetail.getUnits() : totalUnits + auxOrderDetail.getUnits());
			}
		}
		List<ProductTotal> list = new ArrayList<ProductTotal>();
		for (Map.Entry<Product, Integer> entry : productTotalMap.entrySet()) {
			Product product = entry.getKey();
			Integer totalUnits = entry.getValue();
			ProductTotal productTotal = new ProductTotal(product, totalUnits);
			list.add(productTotal);
		}
		return list;
	}
}
