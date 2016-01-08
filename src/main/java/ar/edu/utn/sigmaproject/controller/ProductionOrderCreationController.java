package ar.edu.utn.sigmaproject.controller;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.ProductTotal;
import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.Worker;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.ProductionOrderService;
import ar.edu.utn.sigmaproject.service.ProductionPlanDetailService;
import ar.edu.utn.sigmaproject.service.WorkerService;
import ar.edu.utn.sigmaproject.service.impl.ProductServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionOrderServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanDetailServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.WorkerServiceImpl;

public class ProductionOrderCreationController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;
	
	@Wire
	Textbox productionPlanNameTextbox;
	@Wire
    Datebox productionPlanDateBox;
	@Wire
    Listbox productionOrderListbox;
	
	// services
    private ProductionOrderService productionOrderService = new ProductionOrderServiceImpl();
    private ProductService productService = new ProductServiceImpl();
    private ProductionPlanDetailService productionPlanDetailService = new ProductionPlanDetailServiceImpl();
    private WorkerService workerService = new WorkerServiceImpl();
    
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
        	Integer id_production_plan = currentProductionPlan.getId();
        	productionOrderList = productionOrderService.getProductionOrderList(id_production_plan);
        	
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
        productionOrderListModel = new ListModelList<ProductionOrder>(productionOrderList);
        productionOrderListbox.setModel(productionOrderListModel);
        
        refreshView();
    }

	private void refreshView() {
		productionPlanNameTextbox.setDisabled(true);
		productionPlanDateBox.setDisabled(true);
		if(currentProductionPlan != null) {
			productionPlanNameTextbox.setText(currentProductionPlan.getName());
			productionPlanDateBox.setValue(currentProductionPlan.getDate());
		}
		
	}
	
	public String getProductName(int idProduct) {
    	return productService.getProduct(idProduct).getName();
    }
    
    public String getProductCode(int idProduct) {
    	return productService.getProduct(idProduct).getCode();
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
    
    @Listen("onEditProductionOrderNumber = #productionOrderListbox")
	public void doEditProductionOrderNumber(ForwardEvent evt) {
    	ProductionOrder data = (ProductionOrder) evt.getData();// obtenemos el objeto pasado por parametro
    	Spinner element = (Spinner) evt.getOrigin().getTarget();// obtenemos el elemento web
    	data.setNumber(element.getValue());// cargamos al objeto el valor actualizado del elemento web
    }
    
    @Listen("onEditProductionOrderDate = #productionOrderListbox")
	public void doEditProductionOrderDate(ForwardEvent evt) {
    	ProductionOrder data = (ProductionOrder) evt.getData();// obtenemos el objeto pasado por parametro
    	Datebox element = (Datebox) evt.getOrigin().getTarget();// obtenemos el elemento web
    	data.setDate(element.getValue());// cargamos al objeto el valor actualizado del elemento web
    }
	
}
