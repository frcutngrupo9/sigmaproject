package ar.edu.utn.sigmaproject.controller;

import java.util.ArrayList;
import java.util.Date;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Machine;
import ar.edu.utn.sigmaproject.domain.MachineType;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.domain.Product;
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
public class ProductionOrderListController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox productionPlanNameTextbox;
	@Wire
	Datebox productionPlanCreationDatebox;
	@Wire
	Grid productionOrderGrid;
	@Wire
	Textbox productionPlanStateTypeTextbox;
	@Wire
	Button returnButton;
	@Wire
	Datebox productionPlanStartDatebox;
	@Wire
	Datebox productionPlanFinishDatebox;
	@Wire
	Datebox productionPlanStartRealDatebox;
	@Wire
	Datebox productionPlanFinishRealDatebox;
	@Wire
	Button saveButton;
	@Wire
	Button resetButton;

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

	// atributes
	private ProductionPlan currentProductionPlan;

	// list
	private List<ProductionOrder> productionOrderList;

	// list models
	private ListModelList<ProductionOrder> productionOrderListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentProductionPlan = (ProductionPlan) Executions.getCurrent().getAttribute("selected_production_plan");
		if(currentProductionPlan == null) {throw new RuntimeException("ProductionPlan not found");}
		productionOrderList = productionOrderRepository.findByProductionPlan(currentProductionPlan);
		productionOrderListModel = new ListModelList<ProductionOrder>(productionOrderList);
		productionOrderGrid.setModel(productionOrderListModel);
		
		productionPlanNameTextbox.setDisabled(true);
		productionPlanCreationDatebox.setDisabled(true);
		productionPlanStateTypeTextbox.setDisabled(true);
		productionPlanFinishDatebox.setDisabled(true);
		productionPlanStartRealDatebox.setDisabled(true);
		productionPlanFinishRealDatebox.setDisabled(true);
		
		refreshView();
	}

	private void refreshView() {
		if(currentProductionPlan != null) {
			productionPlanNameTextbox.setText(currentProductionPlan.getName());
			productionPlanCreationDatebox.setValue(currentProductionPlan.getDateCreation());
			ProductionPlanStateType lastProductionPlanState = currentProductionPlan.getCurrentStateType();
			if(lastProductionPlanState != null) {
				productionPlanStateTypeTextbox.setText(lastProductionPlanState.getName().toUpperCase());
			} else {
				productionPlanStateTypeTextbox.setText("[Sin Estado]");
			}
			productionPlanStartDatebox.setValue(currentProductionPlan.getDateStart());
		}
	}

	public String getWorkerName(ProductionOrder productionOrder) {
		if(productionOrder == null) {
			return "";
		} else {
			if(productionOrder.getWorker() != null) {
				return productionOrder.getWorker().getName();
			} else {
				return "[no asignado]";
			}
		}
	}

	@Listen("onEditProductionOrder = #productionOrderGrid")
	public void doEditProductionOrder(ForwardEvent evt) {
		ProductionOrder productionOrder = (ProductionOrder) evt.getData();
		Executions.getCurrent().setAttribute("selected_production_order", productionOrder);
		Executions.getCurrent().setAttribute("selected_production_plan", currentProductionPlan);
		Include include = (Include) Selectors.iterable(evt.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/production_order_creation.zul");
	}

	public ListModel<ProductionOrderDetail> getProductionOrderDetailList(Product product) {// buscar todos los procesos del producto
		List<ProductionOrderDetail> list = new ArrayList<>();
		ProductionOrder aux = productionOrderRepository.findByProductionPlanAndProduct(currentProductionPlan, product);
		if(aux != null) {
			list = aux.getDetails();
		}
		return new ListModelList<>(list);
	}

	public String getIsFinished(boolean value) {
		if(value == true) {
			return "si";
		} else {
			return "no";
		}
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

	public String getMachineTypeName(ProductionOrderDetail productionOrderDetail) {
		String name = "Ninguna";
		Process process = productionOrderDetail.getProcess();
		ProcessType processType = process.getType();
		MachineType machineType = processType.getMachineType();
		if(machineType != null) {
			name = machineType.getName();
		}
		return name;
	}

	public String getMachineName(ProductionOrderDetail productionOrderDetail) {
		String name = "Ninguna";
		Machine machine = productionOrderDetail.getMachine();
		if(machine != null) {
			name = machine.getName();
		}
		return name;
	}

	public boolean isProductionPlanStateCancel() {
		ProductionPlanStateType lastProductionPlanState = currentProductionPlan.getCurrentStateType();
		return lastProductionPlanState != null && lastProductionPlanState.getName().equals("Cancelado");
	}

	public boolean isProductionOrderStateCancel(ProductionOrder aux) {
		if(aux == null) {
			return false;
		} else {
			if(aux.getCurrentStateType()!=null && aux.getCurrentStateType().getName().equals("Cancelada")) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Listen("onClick = #returnButton")
	public void returnButtonClick() {
		Include include = (Include) Selectors.iterable(this.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/production_plan_list.zul");
	}
	
	@Listen("onChange = #productionPlanStartDatebox")
	public void productionPlanStartDateboxChange() {
		//TODO selecciona el primer valor de secuencia y le asigna como fecha de inicio el valor seleccionado, y calcula las demas fechas de los restantes ordenes y se las asigna
		//productionPlanStartDatebox.getValue()
	}
}
