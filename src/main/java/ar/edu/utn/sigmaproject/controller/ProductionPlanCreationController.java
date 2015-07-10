package ar.edu.utn.sigmaproject.controller;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Button;

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
	@Wire
    Listbox productionPlanProductListbox;
	@Wire
	Bandbox productBandbox;
	@Wire
	Intbox productUnits;
	@Wire
	Button addProductButton;
	
	// services
	ProductService productListService = new ProductServiceImpl();
	ProductionPlanService productionPlanService = new ProductionPlanServiceImpl();
	ProductionPlanDetailService productionPlanDetailService = new ProductionPlanDetailServiceImpl();
	
	ListModelList<Product> productListModel;
    ListModelList<ProductionPlanDetail> productionPlanDetailListModel;
    
    // atributes
    private Product selectedProduct;
    private List<Product> productList;
	private List<ProductionPlanDetail> productionPlanDetailList;
	
	@Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        productList = productListService.getProductList();
        productListModel = new ListModelList<Product>(productList);
        productPopupListbox.setModel(productListModel);
        
        selectedProduct = null;
        productionPlanDetailList = new ArrayList<ProductionPlanDetail>();
        productionPlanDetailListModel = new ListModelList<ProductionPlanDetail>(productionPlanDetailList);
        productionPlanProductListbox.setModel(productionPlanDetailListModel);
    }
	
	@Listen("onSelect = #productPopupListbox")
    public void selectionProductPopupListbox() {
		selectedProduct = (Product) productPopupListbox.getSelectedItem().getValue();
		productBandbox.setValue(selectedProduct.getName());
		productBandbox.close();
    }
	
	@Listen("onClick = #addProductButton")
    public void addProductToProdPlan() {
		if(productUnits.getValue()==null || productUnits.getValue().intValue()<=0){
			Clients.showNotification("Ingresar Cantidad del Producto",productUnits);
			return;
		}
		productListModel.remove(selectedProduct);
		productPopupListbox.setModel(productListModel);
		ProductionPlanDetail aux = new ProductionPlanDetail(null,selectedProduct.getId(),productUnits.getValue());
		productionPlanDetailList.add(aux);
		productionPlanDetailListModel.add(aux);
		productionPlanProductListbox.setModel(productionPlanDetailListModel);
		
		// luego de actualizar la tabla borramos el text del producto  seleccionado
		// deseleccionamos la tabla y borramos la cantidad
		productBandbox.setValue("");
		productPopupListbox.clearSelection();
		productUnits.setText(null);
    }
	
	public String getNameOfProduct(int idProduct) {
		Product aux = productListService.getProduct(idProduct);
		return aux.getName();
    }
	
}
