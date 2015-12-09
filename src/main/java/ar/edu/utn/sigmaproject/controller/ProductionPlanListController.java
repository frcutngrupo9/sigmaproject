package ar.edu.utn.sigmaproject.controller;

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
import org.zkoss.zul.ListModelList;

import ar.edu.utn.sigmaproject.domain.ProductTotal;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanState;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.service.ProductionPlanDetailService;
import ar.edu.utn.sigmaproject.service.ProductionPlanService;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateService;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeService;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanDetailServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanStateServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanStateTypeServiceImpl;

public class ProductionPlanListController  extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;
	
	@Wire
    Grid productionPlanGrid;
	@Wire
	Button newButton;
	
	// services
	private ProductionPlanService productionPlanService = new ProductionPlanServiceImpl();
	private ProductionPlanDetailService productionPlanDetailService = new ProductionPlanDetailServiceImpl();
	private ProductionPlanStateService productionPlanStateService = new ProductionPlanStateServiceImpl();
	private ProductionPlanStateTypeService productionPlanStateTypeService = new ProductionPlanStateTypeServiceImpl();
	
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
    	int idProductionPlan = (Integer) evt.getData();
    	Executions.getCurrent().setAttribute("selected_production_plan", productionPlanService.getProductionPlan(idProductionPlan));
        Include include = (Include) Selectors.iterable(evt.getPage(), "#mainInclude").iterator().next();
    	include.setSrc("/production_plan_creation.zul");
    }
	
	private void refreshList() {
		productionPlanList = productionPlanService.getProductionPlanList();
        productionPlanListModel = new ListModelList<ProductionPlan>(productionPlanList);
        productionPlanGrid.setModel(productionPlanListModel);
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
	
	@Listen("onClick = #newButton")
    public void goToProductionPlanCreation() {
    	Executions.getCurrent().setAttribute("selected_production_plan", null);
    	Include include = (Include) Selectors.iterable(productionPlanGrid.getPage(), "#mainInclude").iterator().next();
    	include.setSrc("/production_plan_creation.zul");
    }
}
