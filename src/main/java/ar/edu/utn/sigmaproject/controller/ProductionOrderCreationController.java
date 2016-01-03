package ar.edu.utn.sigmaproject.controller;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductExistence;
import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.service.ClientService;
import ar.edu.utn.sigmaproject.service.OrderDetailService;
import ar.edu.utn.sigmaproject.service.OrderService;
import ar.edu.utn.sigmaproject.service.OrderStateService;
import ar.edu.utn.sigmaproject.service.OrderStateTypeService;
import ar.edu.utn.sigmaproject.service.ProductExistenceService;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.ProductionOrderService;
import ar.edu.utn.sigmaproject.service.impl.ClientServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderDetailServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderStateServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.OrderStateTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductExistenceServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionOrderServiceImpl;

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
    private ClientService clientService = new ClientServiceImpl();
    private OrderService orderService = new OrderServiceImpl();
    private OrderDetailService orderDetailService = new OrderDetailServiceImpl();
    private OrderStateService orderStateService = new OrderStateServiceImpl();
    private OrderStateTypeService orderStateTypeService = new OrderStateTypeServiceImpl();
    
    // atributes
    private ProductionPlan currentProductionPlan;
    private Order currentOrder;
    
    // list
    private List<ProductionOrder> productionOrderList;
    
    // list models
    private ListModelList<ProductionOrder> productionOrderListModel;
	
	@Override
    public void doAfterCompose(Component comp) throws Exception{
        super.doAfterCompose(comp);
        
        currentProductionPlan = (ProductionPlan) Executions.getCurrent().getAttribute("selected_production_plan");
        
        productionOrderList = productionOrderService.getProductionOrderList(currentProductionPlan.getId());
        productionOrderListModel = new ListModelList<ProductionOrder>(productionOrderList);
        productionOrderListbox.setModel(productionOrderListModel);
        
        refreshView();
    }

	private void refreshView() {
		// TODO Auto-generated method stub
		
	}
	
}
