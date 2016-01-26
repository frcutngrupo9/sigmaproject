package ar.edu.utn.sigmaproject.controller;

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
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductTotal;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanDetail;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProductionPlanListController  extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;
	
	@Wire
    Grid productionPlanGrid;
	@Wire
	Button newButton;
	
	// services
	@WireVariable
	private ProductionPlanRepository productionPlanRepository;
	
	@WireVariable
	private ProductionPlanStateTypeRepository productionPlanStateTypeRepository;
	
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
	
	@Listen("onClick = #newButton")
    public void goToProductionPlanCreation() {
    	Executions.getCurrent().setAttribute("selected_production_plan", null);
    	Include include = (Include) Selectors.iterable(productionPlanGrid.getPage(), "#mainInclude").iterator().next();
    	include.setSrc("/production_plan_creation.zul");
    }
	
	public ListModel<ProductTotal> getProductionPlanProducts(ProductionPlan productionPlan) {
		Map<Product, ProductTotal> productTotalByProductMap = new HashMap<>(); 
		for (ProductionPlanDetail productionPlanDetail : productionPlan.getDetails()) {
			for (OrderDetail orderDetail : productionPlanDetail.getOrder().getDetails()) {
				if (!productTotalByProductMap.containsKey(orderDetail.getProduct())) {
					productTotalByProductMap.put(orderDetail.getProduct(), new ProductTotal(orderDetail.getProduct()));
				}
				ProductTotal productTotal = productTotalByProductMap.get(orderDetail.getProduct());
				productTotal.setTotalUnits(productTotal.getTotalUnits() + orderDetail.getUnits()); // sumamos su cantidad con la existente
			}			
		}
		return new ListModelList<ProductTotal>(productTotalByProductMap.values());
	}
}
