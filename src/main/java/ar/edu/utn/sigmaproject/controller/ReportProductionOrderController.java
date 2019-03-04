/*
 * The MIT License
 *
 * Copyright (C) 2017 SigmaProject.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package ar.edu.utn.sigmaproject.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.ProcessState;
import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.Worker;
import ar.edu.utn.sigmaproject.service.ProductionOrderRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;
import ar.edu.utn.sigmaproject.service.WorkerRepository;
import ar.edu.utn.sigmaproject.util.ProductionOrderReportDataSource;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ReportProductionOrderController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Bandbox productionPlanBandbox;
	@Wire
	Listbox productionPlanListbox;
	@Wire
	Bandbox productionOrderBandbox;
	@Wire
	Listbox productionOrderListbox;
	@Wire
	Bandbox workerBandbox;
	@Wire
	Listbox workerListbox;
	@Wire
	Grid productionOrderDetailGrid;

	// services
	@WireVariable
	private ProductionPlanRepository productionPlanRepository;
	@WireVariable
	private ProductionOrderRepository productionOrderRepository;
	@WireVariable
	private WorkerRepository workerRepository;

	// atributes
	private ProductionPlan productionPlanSelected;
	private ProductionOrder productionOrderSelected;
	private Worker workerSelected;
	private String returnPageName;
	private Map<String, Object> returnParameters;

	// list
	private List<ProductionOrderDetail> processList;

	// list models

	@SuppressWarnings("unchecked")
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		returnPageName = (String) Executions.getCurrent().getAttribute("return_page_name");
		if(returnPageName == null) {throw new RuntimeException("returnPageName not found");}
		returnParameters = (Map<String, Object>) Executions.getCurrent().getAttribute("return_parameters");

		productionPlanSelected = (ProductionPlan) Executions.getCurrent().getAttribute("selected_production_plan");
		productionOrderSelected = (ProductionOrder) Executions.getCurrent().getAttribute("selected_production_order");
		workerSelected = (Worker) Executions.getCurrent().getAttribute("selected_worker");
		refreshProductionPlanAndOrderView();
		refreshWorkerView();
		productionPlanListbox.setModel(new ListModelList<ProductionPlan>(productionPlanRepository.findAll()));
		//productionOrderListbox.setModel(new ListModelList<ProductionOrder>(productionOrderRepository.findAll()));
	}

	@Listen("onClick = #returnButton")
	public void returnButtonClick() {
		if(returnParameters != null) {
			for (Map.Entry<String, Object> entry : returnParameters.entrySet()) {
				Executions.getCurrent().setAttribute(entry.getKey(), entry.getValue());
			}
		}
		Include include = (Include) Selectors.iterable(this.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/"+ returnPageName + ".zul");
	}

	@Listen("onSelect = #productionPlanListbox")
	public void productionPlanSelect() {
		productionPlanSelected = productionPlanListbox.getSelectedItem().getValue();
		productionOrderSelected = null;
		refreshProductionPlanAndOrderView();
	}

	private void refreshProductionPlanAndOrderView() {
		if(productionPlanSelected == null) {
			productionPlanBandbox.setValue("Seleccionar");
			productionOrderListbox.setModel(new ListModelList<>(new ArrayList<>()));
			if(productionOrderSelected != null) {
				productionOrderSelected = null;
			}
		} else {
			productionPlanBandbox.setValue(productionPlanSelected.getName());
			productionOrderListbox.setModel(new ListModelList<>(productionPlanSelected.getProductionOrderList()));
		}
		List<Worker> filteredWorkers = filterWorkerByProductionOrder(productionOrderSelected);
		if(productionOrderSelected == null) {
			productionOrderBandbox.setValue("Seleccionar");
			processList = new ArrayList<ProductionOrderDetail>();
			workerListbox.setEmptyMessage("Debe Seleccionar una Orden.");
		} else {
			productionOrderBandbox.setValue("Orden " + productionOrderSelected.getNumber());
			processList = removeCanceledDetails(filterDetailsByWorker(productionOrderSelected.getDetails()));
			if(filteredWorkers.isEmpty()) {
				workerListbox.setEmptyMessage("La orden no tiene empeados asignados.");
			}
		}
		workerListbox.setModel(new ListModelList<>(filteredWorkers));
		productionOrderDetailGrid.setModel(new ListModelList<ProductionOrderDetail>(new ArrayList<ProductionOrderDetail>(processList)));
	}

	private List<Worker> filterWorkerByProductionOrder(ProductionOrder productionOrder) {
		// devuleve una lista conteniendo solo los empleados que esten asignados a la orden
		ArrayList<Worker> list = new ArrayList<Worker>();
		if(productionOrder == null) {
			return list;
		}
		for(ProductionOrderDetail each : productionOrder.getDetails()) {
			if(each.getWorker()!=null && !list.contains(each.getWorker())) {
				list.add(each.getWorker());
			}
		}
		return list;
	}

	@Listen("onSelect = #productionOrderListbox")
	public void productionOrderSelect() {
		productionOrderSelected = productionOrderListbox.getSelectedItem().getValue();
		refreshProductionPlanAndOrderView();
	}

	private List<ProductionOrderDetail> removeCanceledDetails(List<ProductionOrderDetail> productionOrderDetailList) {
		ArrayList<ProductionOrderDetail> removeList = new ArrayList<ProductionOrderDetail>();
		for(ProductionOrderDetail each : productionOrderDetailList) {
			if(each.getState().equals(ProcessState.Cancelado)) {
				removeList.add(each);
			}
		}
		productionOrderDetailList.removeAll(removeList);
		return productionOrderDetailList;
	}

	@Listen("onSelect = #workerListbox")
	public void workerSelect() {
		workerSelected = workerListbox.getSelectedItem().getValue();
		refreshWorkerView();
	}

	private List<ProductionOrderDetail> filterDetailsByWorker(List<ProductionOrderDetail> productionOrderDetailList) {
		if(workerSelected == null) {
			// se seleccionaron todos
			return productionOrderDetailList;
		}
		ArrayList<ProductionOrderDetail> list = new ArrayList<ProductionOrderDetail>();
		for(ProductionOrderDetail each : productionOrderDetailList) {
			if(each.getWorker()!=null && each.getWorker().getId()==workerSelected.getId()) {
				list.add(each);
			}
		}
		return list;
	}

	@Listen("onClick = #allWorkersButton")
	public void allWorkersButtonClick() {
		workerBandbox.close();
		workerSelected = null;
		refreshWorkerView();
	}

	private void refreshWorkerView() {
		if(workerSelected != null) {
			workerBandbox.setValue(workerSelected.getName());
		} else {
			workerBandbox.setValue("Todos");
		}
		refreshProductionPlanAndOrderView();
	}

	@Listen("onClick = #generateReportButton")
	public void generateReportButtonClick() {
		if(productionOrderSelected == null) {
			alert("Debe seleccionar una orden de producccion");
		} else {
			if(workerSelected == null) {
				loadReport();
			} else {
				loadReportByWorker();
			}
		}
	}

	private void loadReportByWorker() {
		Map<String, Object> parameters = getParametersProductionOrder();
		parameters.put("workerName", workerSelected.getName());
		doJasperModal(parameters, "production_order_by_worker");
	}

	private void loadReport() {
		Map<String, Object> parameters = getParametersProductionOrder();
		doJasperModal(parameters, "production_order");
	}

	private Map<String, Object> getParametersProductionOrder() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("productionPlanName", productionPlanSelected.getName());
		parameters.put("productionOrderNumber", productionOrderSelected.getNumber());
		parameters.put("productName", productionOrderSelected.getProduct().getDescription());
		parameters.put("productUnits", productionOrderSelected.getUnits());
		return parameters;
	}

	private void doJasperModal(Map<String, Object> parameters, String reportFileName) {
		Executions.getCurrent().setAttribute("jr_datasource", new ProductionOrderReportDataSource(processList));
		Executions.getCurrent().setAttribute("return_page_name", "report_production_order");
		Map<String, Object> returnParameters = new HashMap<String, Object>();
		returnParameters.put("selected_production_order", productionOrderSelected);
		returnParameters.put("selected_production_plan", productionPlanSelected);
		returnParameters.put("selected_worker", workerSelected);
		returnParameters.put("return_page_name", returnPageName);
		Executions.getCurrent().setAttribute("return_parameters", returnParameters);
		Executions.getCurrent().setAttribute("report_src_name", reportFileName);
		Executions.getCurrent().setAttribute("report_parameters", parameters);
		Window window = (Window)Executions.createComponents("/report_selection_modal.zul", null, null);
		window.doModal();
	}
}
