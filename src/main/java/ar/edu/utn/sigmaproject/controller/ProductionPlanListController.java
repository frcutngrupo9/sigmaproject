package ar.edu.utn.sigmaproject.controller;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.ListModelList;

import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanDetail;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.ProductionPlanDetailService;
import ar.edu.utn.sigmaproject.service.ProductionPlanService;
import ar.edu.utn.sigmaproject.service.impl.ProductServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanDetailServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanServiceImpl;

public class ProductionPlanListController  extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;
	
	// services
	ProductService productListService = new ProductServiceImpl();
	ProductionPlanService productionPlanService = new ProductionPlanServiceImpl();
	ProductionPlanDetailService productionPlanDetailService = new ProductionPlanDetailServiceImpl();
	
	ListModelList<Product> productListModel;
    ListModelList<ProductionPlan> productionPlanListModel;
    ListModelList<ProductionPlanDetail> productionPlanDetailListModel;
    
    // atributes
    private Product selectedProduct;
    private List<Product> productList;
    private ProductionPlan productionPlan;
	private ProductionPlanDetail productionPlanDetail;
	private List<ProductionPlan> productionPlanList;
	private List<ProductionPlanDetail> productionPlanDetailList;
	

}
