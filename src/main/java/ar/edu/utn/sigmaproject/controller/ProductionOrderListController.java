package ar.edu.utn.sigmaproject.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.Duration;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
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
import ar.edu.utn.sigmaproject.domain.ProductionOrderState;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanState;
import ar.edu.utn.sigmaproject.domain.Worker;
import ar.edu.utn.sigmaproject.service.MachineService;
import ar.edu.utn.sigmaproject.service.MachineTypeService;
import ar.edu.utn.sigmaproject.service.PieceService;
import ar.edu.utn.sigmaproject.service.ProcessService;
import ar.edu.utn.sigmaproject.service.ProcessTypeService;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.ProductionOrderDetailService;
import ar.edu.utn.sigmaproject.service.ProductionOrderService;
import ar.edu.utn.sigmaproject.service.ProductionPlanDetailService;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateService;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeService;
import ar.edu.utn.sigmaproject.service.WorkerService;
import ar.edu.utn.sigmaproject.service.impl.MachineServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.MachineTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.PieceServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProcessServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProcessTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionOrderDetailServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionOrderServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanDetailServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanStateServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.ProductionPlanStateTypeServiceImpl;
import ar.edu.utn.sigmaproject.service.impl.WorkerServiceImpl;

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
	private ProductionOrderService productionOrderService = new ProductionOrderServiceImpl();
	private ProductionOrderDetailService productionOrderDetailService = new ProductionOrderDetailServiceImpl();
	private ProductService productService = new ProductServiceImpl();
	private PieceService pieceService = new PieceServiceImpl();
	private ProcessService processService = new ProcessServiceImpl();
	private ProductionPlanDetailService productionPlanDetailService = new ProductionPlanDetailServiceImpl();
	private WorkerService workerService = new WorkerServiceImpl();
	private ProcessTypeService processTypeService = new ProcessTypeServiceImpl();
	private MachineTypeService machineTypeService = new MachineTypeServiceImpl();
	private MachineService machineService = new MachineServiceImpl();
	private ProductionPlanStateService productionPlanStateService = new ProductionPlanStateServiceImpl();
	private ProductionPlanStateTypeService productionPlanStateTypeService = new ProductionPlanStateTypeServiceImpl();

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
		productionOrderList = productionOrderService.getProductionOrderList(currentProductionPlan.getId());
		ArrayList<ProductTotal> productTotalList = productionPlanDetailService.getProductTotalList(currentProductionPlan.getId());

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
			ProductionPlanState lastProductionPlanState = productionPlanStateService.getLastProductionPlanState(currentProductionPlan.getId());
			if(lastProductionPlanState != null) {
				productionPlanStateTypeTextbox.setText(productionPlanStateTypeService.getProductionPlanStateType(lastProductionPlanState.getIdProductionPlanStateType()).getName().toUpperCase());
			} else {
				productionPlanStateTypeTextbox.setText("[Sin Estado]");
			}
		}

	}

	public Product getProduct(int idProduct) {
		return productService.getProduct(idProduct);
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

	public String getWorkerName(Product product) {
		ProductionOrder aux = getProductionOrder(product);
		if(aux == null) {
			return "";
		} else {
			Worker worker = workerService.getWorker(aux.getIdWorker());
			if(worker != null) {
				return worker.getName();
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

	public ListModel<Process> getProductionOrderProcesses(Integer idProduct) {// buscar todos los procesos del producto
		List<Process> list = new ArrayList<Process>();
		List<Piece> auxPieceList = pieceService.getPieceList(idProduct);
		for(Piece piece : auxPieceList) {
			List<Process> auxProcessList = processService.getProcessList(piece.getId());
			for(Process process : auxProcessList) {
				list.add(Process.clone(process));
			}
		}
		return new ListModelList<Process>(list);
	}

	public ListModel<ProductionOrderDetail> getProductionOrderDetailList(Product product) {// buscar todos los procesos del producto
		List<ProductionOrderDetail> list = new ArrayList<ProductionOrderDetail>();
		ProductionOrder aux = getProductionOrder(product);
		if(aux != null) {
			list = productionOrderDetailService.getProductionOrderDetailList(aux.getId());
		}
		return new ListModelList<ProductionOrderDetail>(list);
	}

	public ProcessType getProcessType(int idProduct) {
		return processTypeService.getProcessType(idProduct);
	}

	public Piece getPiece(int idPiece) {
		return pieceService.getPiece(idPiece);
	}

	public ProcessType getProcessTypeByProcessId(int idProcess) {
		return processTypeService.getProcessType(getProcessByProcessId(idProcess).getIdProcessType());
	}

	public Piece getPieceByProcessId(int idProcess) {
		return pieceService.getPiece(getProcessByProcessId(idProcess).getIdPiece());
	}

	public Process getProcessByProcessId(int idProcess) {
		return processService.getProcess(idProcess);
	}

	public String getIsFinished(boolean value) {
		if(value == true) {
			return "si";
		} else {
			return "no";
		}
	}

	public String getFormatedTime(Duration time) {
		return String.format("Dias: %d Horas: %d Minutos: %d", time.getDays(), time.getHours(), time.getMinutes());
	}

	public String getPercentComplete(Product product) {
		ProductionOrder aux = getProductionOrder(product);
		if(aux != null) {
			List<ProductionOrderDetail> productionOrderDetailList = productionOrderDetailService.getProductionOrderDetailList(aux.getId());
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
		Process process = processService.getProcess(productionOrderDetail.getIdProcess());
		ProcessType processType = processTypeService.getProcessType(process.getIdProcessType());
		MachineType machineType = machineTypeService.getMachineType(processType.getIdMachineType());
		if(machineType != null) {
			name = machineType.getName();
		}
		return name;
	}

	public String getMachineName(ProductionOrderDetail productionOrderDetail) {
		String name = "Ninguna";
		Machine machine = machineService.getMachine(productionOrderDetail.getIdMachine());
		if(machine != null) {
			name = machine.getName();
		}
		return name;
	}

	public ProductionOrder getProductionOrder(Product product) {
		for(ProductionOrder each : productionOrderList) {
			if(each.getIdProduct().equals(product.getId())) {
				return ProductionOrder.clone(each);
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
				return aux.getState().name();
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
		ProductionPlanState lastProductionPlanState = productionPlanStateService.getLastProductionPlanState(currentProductionPlan.getId());
		if(lastProductionPlanState != null) {
			if(productionPlanStateTypeService.getProductionPlanStateType("cancelado").getId().equals(lastProductionPlanState.getIdProductionPlanStateType())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean isProductionOrderStateCancel(Product product) {
		ProductionOrder aux = getProductionOrder(product);
		if(aux == null) {
			return false;
		} else {
			if(aux.getState()!=null && aux.getState().equals(ProductionOrderState.Cancelada)) {
				return true;
			} else {
				return false;
			}
		}
	}
}
