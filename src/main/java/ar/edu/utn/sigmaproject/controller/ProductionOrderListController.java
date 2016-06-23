package ar.edu.utn.sigmaproject.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;

import ar.edu.utn.sigmaproject.domain.Machine;
import ar.edu.utn.sigmaproject.domain.MachineType;
import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Process;
import ar.edu.utn.sigmaproject.domain.ProcessType;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductTotal;
import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.service.MachineRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProductionOrderListController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox productionPlanNameTextbox;
	@Wire
	Datebox productionPlanDatebox;
	@Wire
	Grid productionOrderGrid;
	@Wire
	Textbox productionPlanStateTypeTextbox;

	// services
	@WireVariable
	private MachineRepository machineRepository;
	@WireVariable
	private ProductionOrderRepository productionOrderRepository;
	@WireVariable
	private ProductionPlanRepository productionPlanRepository;
	@WireVariable
	private ProductionPlanStateTypeRepository productionPlanStateTypeRepository;

	// atributes
	private ProductionPlan currentProductionPlan;

	// list
	private List<ProductionOrder> productionOrderList;
	private List<ProductTotal> productTotalList;

	// list models
	private ListModelList<ProductTotal> productionOrderListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);

		currentProductionPlan = (ProductionPlan) Executions.getCurrent().getAttribute("selected_production_plan");
		if(currentProductionPlan == null) {throw new RuntimeException("ProductionPlan not found");}
		productionOrderList = productionOrderRepository.findByProductionPlan(currentProductionPlan);
		List<ProductTotal> productTotalList = currentProductionPlan.getProductTotalList();

		productionOrderListModel = new ListModelList<ProductTotal>(productTotalList);
		productionOrderGrid.setModel(productionOrderListModel);

		refreshView();
	}

	private void refreshView() {
		productionPlanNameTextbox.setDisabled(true);
		productionPlanDatebox.setDisabled(true);
		productionPlanStateTypeTextbox.setDisabled(true);
		if(currentProductionPlan != null) {
			productionPlanNameTextbox.setText(currentProductionPlan.getName());
			productionPlanDatebox.setValue(currentProductionPlan.getDate());
			ProductionPlanStateType lastProductionPlanState = currentProductionPlan.getCurrentStateType();
			if(lastProductionPlanState != null) {
				productionPlanStateTypeTextbox.setText(lastProductionPlanState.getName().toUpperCase());
			} else {
				productionPlanStateTypeTextbox.setText("[Sin Estado]");
			}
		}

	}

	public String getProductUnits(Product product) {
		int product_units = 0;
		for(ProductTotal productTotal : productTotalList) {
			if(productTotal.getProduct().equals(product)) {
				product_units = productTotal.getTotalUnits();
			}
		}
		return "" + product_units;
	}

	public String getWorkerName(Product product) {
		ProductionOrder aux = getProductionOrder(product);
		if(aux == null) {
			return "";
		} else {
			if(aux.getWorker() != null) {
				return aux.getWorker().getName();
			} else {
				return "[no asignado]";
			}
		}

	}

	@Listen("onEditProductionOrder = #productionOrderGrid")
	public void doEditProductionOrder(ForwardEvent evt) {
		ProductTotal product = (ProductTotal) evt.getData();
		Executions.getCurrent().setAttribute("selected_product", product);
		Executions.getCurrent().setAttribute("selected_production_plan", currentProductionPlan);
		Include include = (Include) Selectors.iterable(evt.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/production_order_creation.zul");
	}

	public ListModel<Process> getProductionOrderProcesses(Product product) {// buscar todos los procesos del producto
		List<Process> list = new ArrayList<>();
		for(Piece piece : product.getPieces()) {
			for(Process process : piece.getProcesses()) {
				list.add(Process.clone(process));
			}
		}
		return new ListModelList<>(list);
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

	public String getPercentComplete(Product product) {
		ProductionOrder aux = getProductionOrder(product);
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

	public ProductionOrder getProductionOrder(Product product) {
		for(ProductionOrder each : productionOrderList) {
			if(each.getProduct().equals(product)) {
				return each;
			}
		}
		return null;
	}

	public String getProductionOrderState(Product product) {
		ProductionOrder aux = getProductionOrder(product);
		if(aux == null) {
			return "No Generado";
		} else {
			if(aux.getState() == null) {
				return "Generado";
			} else {
				return aux.getState().getName();
			}
		}
	}

	public String getProductionOrderId(Product product) {
		ProductionOrder aux = getProductionOrder(product);
		if(aux == null) {
			return "";
		} else {
			return aux.getId() + "";
		}
	}

	public String getProductionOrderNumber(Product product) {
		ProductionOrder aux = getProductionOrder(product);
		if(aux == null) {
			return "";
		} else {
			return aux.getNumber() + "";
		}
	}

	public String getProductionOrderDate(Product product) {
		ProductionOrder aux = getProductionOrder(product);
		if(aux == null) {
			return "";
		} else {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			String productionOrderDate = df.format(aux.getDate());
			return productionOrderDate;
		}
	}

	public String getProductionOrderDateFinished(Product product) {
		ProductionOrder aux = getProductionOrder(product);
		if(aux == null) {
			return "";
		} else {
			if(aux.getDateFinished() != null) {
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				String productionOrderDateFinished = df.format(aux.getDateFinished());
				return productionOrderDateFinished;
			} else {
				return "No Finalizado";
			}
		}
	}

	public String getProductionOrderButtonLabel(Product product) {
		ProductionOrder aux = getProductionOrder(product);
		if(aux == null) {
			return "Generar";
		} else {
			return "Abrir";
		}
	}

	public boolean isProductionPlanStateCancel() {
		ProductionPlanStateType lastProductionPlanState = currentProductionPlan.getCurrentStateType();
		return lastProductionPlanState != null && lastProductionPlanState.getName().equals("Cancelado");
	}

	public boolean isProductionOrderStateCancel(Product product) {
		ProductionOrder aux = getProductionOrder(product);
		if(aux == null) {
			return false;
		} else {
			if(aux.getState()!=null && aux.getState().getName().equals("Cancelada")) {
				return true;
			} else {
				return false;
			}
		}
	}
}
