package ar.edu.utn.sigmaproject.controller;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanDetail;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.ProductionPlanService;
import ar.edu.utn.sigmaproject.service.ProductionPlanDetailService;
import ar.edu.utn.sigmaproject.service.impl.ProductServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanDetailServiceImpl;

public class ProductionPlanCreationController extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;
	
	@Wire
    Listbox productPopupListbox;
	
	// services
	ProductService productListService = new ProductServiceImpl();
	ProductionPlanService productionPlanService = new ProductionPlanServiceImpl();
	ProductionPlanDetailService productionPlanDetailService = new ProductionPlanDetailServiceImpl();
	
	ListModelList<Product> productListModel;
    ListModelList<ProductionPlan> productionPlanListModel;
    ListModelList<ProductionPlanDetail> productionPlanDetailListModel;
    
    // atributes
    private ProductionPlan productionPlan;
	private ProductionPlanDetail productionPlanDetail;
	private List<ProductionPlan> productionPlanList;
	private List<ProductionPlanDetail> productionPlanDetailList;
	
	@Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        List<Product> productList = productListService.getProductList();
        productListModel = new ListModelList<Product>(productList);
        productPopupListbox.setModel(productionPlanListModel);
    }

	
}
