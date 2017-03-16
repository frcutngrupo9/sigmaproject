package ar.edu.utn.sigmaproject.controller;

import java.util.ArrayList;
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
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModelList;

import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.service.MachineRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderStateTypeRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProductionFollowUpListController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Grid productionOrderGrid;

	// services
	@WireVariable
	private MachineRepository machineRepository;
	@WireVariable
	private ProductionOrderRepository productionOrderRepository;
	@WireVariable
	private ProductionPlanRepository productionPlanRepository;
	@WireVariable
	private ProductionPlanStateTypeRepository productionPlanStateTypeRepository;
	@WireVariable
	private ProductionOrderStateRepository productionOrderStateRepository;
	@WireVariable
	private ProductionOrderStateTypeRepository productionOrderStateTypeRepository;

	// list
	private List<ProductionOrder> productionOrderList;

	// list models
	private ListModelList<ProductionOrder> productionOrderListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		refreshView();
	}

	private void refreshView() {
		// busca todos los planes de produccion que esten en estado "Lanzado" o "En Produccion", y 
		// guarda todas sus ordenes de produccion en la lista
		ProductionPlanStateType productionPlanStateTypeLanzado = productionPlanStateTypeRepository.findFirstByName("Lanzado");
		ProductionPlanStateType productionPlanStateTypeEnProduccion = productionPlanStateTypeRepository.findFirstByName("En Produccion");
		List<ProductionPlan> productionPlanListLanzado = productionPlanRepository.findByCurrentStateType(productionPlanStateTypeLanzado);
		List<ProductionPlan> productionPlanListEnProduccion = productionPlanRepository.findByCurrentStateType(productionPlanStateTypeEnProduccion);
		productionOrderList = new ArrayList<ProductionOrder>();
		for(ProductionPlan eachPlan : productionPlanListLanzado) {
			productionOrderList.addAll(productionOrderRepository.findByProductionPlan(eachPlan));
		}
		for(ProductionPlan eachPlan : productionPlanListEnProduccion) {
			productionOrderList.addAll(productionOrderRepository.findByProductionPlan(eachPlan));
		}
		productionOrderListModel = new ListModelList<ProductionOrder>(productionOrderList);
		productionOrderGrid.setModel(productionOrderListModel);
	}
	
	@Listen("onEditProductionOrder = #productionOrderGrid")
	public void doEditProductionOrder(ForwardEvent evt) {
		ProductionOrder productionOrder = (ProductionOrder) evt.getData();
		Executions.getCurrent().setAttribute("selected_production_order", productionOrder);
		Include include = (Include) Selectors.iterable(evt.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/production_follow_up.zul");
	}
	
	public String getPercentComplete(ProductionOrder aux) {
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
}
