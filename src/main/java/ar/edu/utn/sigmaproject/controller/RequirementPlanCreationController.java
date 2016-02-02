package ar.edu.utn.sigmaproject.controller;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.RequirementPlanDetailSupply;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.RequirementPlanDetailSupplyService;
import ar.edu.utn.sigmaproject.service.RequirementPlanService;
import ar.edu.utn.sigmaproject.service.impl.ProductServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.RequirementPlanDetailSupplyServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.RequirementPlanServiceImpl;

public class RequirementPlanCreationController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;
	
	@Wire
	Textbox productionPlanNameTextbox;
	@Wire
    Datebox productionPlanDatebox;
	@Wire
    Listbox rawMaterialRequirementListbox;
	@Wire
    Listbox supplyRequirementListbox;
	
	// services
    private RequirementPlanService requirementPlanService = new RequirementPlanServiceImpl();
    private RequirementPlanDetailSupplyService requirementPlanPlanDetailSupplyService = new RequirementPlanDetailSupplyServiceImpl();
    private ProductService productService = new ProductServiceImpl();
    
    // atributes
    private ProductionPlan currentProductionPlan;
    
    // list
    private List<RequirementPlanDetailSupply> supplyRequirementList;
    
    // list models
    private ListModelList<RequirementPlanDetailSupply> supplyRequirementListModel;
	
	@Override
    public void doAfterCompose(Component comp) throws Exception{
        super.doAfterCompose(comp);
        
        currentProductionPlan = (ProductionPlan) Executions.getCurrent().getAttribute("selected_production_plan");
        /*
        if(currentProductionPlan != null) {
        	Integer id_production_plan = currentProductionPlan.getId();
        	supplyRequirementList = requirementPlanPlanDetailSupplyService.getProductionOrderList(id_production_plan);
        	
        	if(productionOrderList.isEmpty()) {// se deben crear las ordenes de produccion
        		ArrayList<ProductTotal> productTotalList = productionPlanDetailService.getProductTotalList(id_production_plan);
        		for(ProductTotal productTotal : productTotalList) {
        			Integer id_product = productTotal.getId();
        			Integer product_units = productTotal.getTotalUnits();
        			Integer id_worker = null;
        			productionOrderList.add(new ProductionOrder(null, id_production_plan, id_product, id_worker, null, product_units, null, null));
        		}
        	}
        	
        } else {
        	productionOrderList = new ArrayList<ProductionOrder>();
        }
        */
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
    
    @Listen("onFulfillSupplyRequirement = #supplyRequirementListbox")
	public void doFulfillSupplyRequirement(ForwardEvent evt) {
    	RequirementPlanDetailSupply data = (RequirementPlanDetailSupply) evt.getData();// obtenemos el objeto pasado por parametro
    	Checkbox element = (Checkbox) evt.getOrigin().getTarget();// obtenemos el elemento web
    	data.setFulfilled(element.isChecked());// cargamos al objeto el valor actualizado del elemento web
    }
}
