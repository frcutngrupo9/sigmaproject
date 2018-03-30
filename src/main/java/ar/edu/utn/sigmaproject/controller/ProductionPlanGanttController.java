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

import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Area;
import org.zkoss.zul.Button;
import org.zkoss.zul.Chart;
import org.zkoss.zul.GanttModel;
import org.zkoss.zul.GanttModel.GanttTask;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.service.ProductionOrderRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;
import ar.edu.utn.sigmaproject.util.GanttChartEngine;


@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProductionPlanGanttController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Chart ganttChart;
	@Wire
	Listbox productionPlanListbox;
	@Wire
	Button cancelButton;
	@Wire
	Listbox themeColorSelectListbox;

	// services
	@WireVariable
	private ProductionOrderRepository productionOrderRepository;
	@WireVariable
	private ProductionPlanRepository productionPlanRepository;

	// atributes
	private ProductionPlan currentProductionPlan;
	private GanttChartEngine ganttChartEngine;

	// list
	private List<ProductionPlan> productionPlanList;

	// list models
	private ListModelList<ProductionPlan> productionPlanListModel;
	private GanttModel productionPlanGanttModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentProductionPlan = (ProductionPlan) Executions.getCurrent().getAttribute("selected_production_plan");
		
		ganttChartEngine = new GanttChartEngine();
		productionPlanList = productionPlanRepository.findAll();
		productionPlanListModel = new ListModelList<>(productionPlanList);
		productionPlanListbox.setModel(productionPlanListModel);
		if(!productionPlanList.isEmpty()) {
			if(currentProductionPlan == null) {
				currentProductionPlan = productionPlanList.get(0);
			} else {
				currentProductionPlan = productionPlanRepository.findOne(currentProductionPlan.getId());
			}
			loadGantt(productionPlanList.get(productionPlanListModel.indexOf(currentProductionPlan)));
		}
	}

	private void loadGantt(ProductionPlan selectedPlan) {
		ganttChart.setType("gantt");
		ganttChart.setEngine(ganttChartEngine);
		productionPlanGanttModel = getModel(selectedPlan);
		ganttChart.setModel(productionPlanGanttModel);
	}

	@Listen("onAfterRender = #productionPlanListbox")
	public void doAfterRenderProductionOrderList() {// se hace refresh despues de q se renderizo para que se le pueda setear un valor seleccionado
		if(!productionPlanList.isEmpty()) {
			productionPlanListbox.setSelectedIndex(productionPlanListModel.indexOf(currentProductionPlan));
		}
	}
	
	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		Include include = (Include) Selectors.iterable(this.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/production_plan_list.zul");
	}

	@Listen("onSelect = #productionPlanListbox")
	public void doProductionPlanListboxSelect() {
		if(productionPlanListbox.getSelectedItem() == null) {
			// just in case for the no selection
			alert("No se ha Seleccionado Plan de Produccion");
			return;
		} 
		ProductionPlan selectedPlan = productionPlanListbox.getSelectedItem().getValue();
		loadGantt(selectedPlan);
	}
	
	@Listen("onSelect = #themeColorSelectListbox")
	public void doThemeColorSelectListboxSelect() {
		if(themeColorSelectListbox.getSelectedItem() == null) {
			// just in case for the no selection
			alert("No se ha Seleccionado Color");
			return;
		} 
		int selectedValue = Integer.parseInt((String) themeColorSelectListbox.getSelectedItem().getValue());
		ganttChartEngine.setColorSet(selectedValue);
		ganttChart.setModel(productionPlanGanttModel);
	}

	@Listen("onClick = #ganttChart")
	public void ganttChartOnClick(MouseEvent event) {
		Area area = (Area) event.getAreaComponent();
		alert(area.getTooltiptext());
	}

	public GanttModel getModel(ProductionPlan selectedPlan) {
		GanttModel ganttmodel = new GanttModel();
		for(ProductionOrder each : selectedPlan.getProductionOrderList()) {
			String nameText = "Orden: " + each.getNumber();
			ganttmodel.addValue("Programado", new GanttTask(nameText, each.getDateStart(), each.getDateFinish(), 0.0));
			if(each.getDateStartReal() != null) {
				// si la orden tiene fecha real de inicio pero no fin real, la fecha fin real se calcula en base a la duracion del estimado
				if(each.getDateFinishReal() == null) {
					Date estimatedDateFinishReal = getEstimatedDateFinishReal(each.getDateStart(), each.getDateFinish(), each.getDateStartReal());
					double quantityComplete = each.getPercent() * 0.01;
					ganttmodel.addValue("Real", new GanttTask(nameText, each.getDateStartReal(), estimatedDateFinishReal, quantityComplete));
				} else {
					ganttmodel.addValue("Real", new GanttTask(nameText, each.getDateStartReal(), each.getDateFinishReal(), 1.0));
				}
			}
		}
		return ganttmodel;
	}

	private Date getEstimatedDateFinishReal(Date dateStart, Date dateFinish, Date dateStartReal) {
		long dateStartMS = dateStart.getTime();
		long dateFinishMS = dateFinish.getTime(); 
		long diference = dateFinishMS - dateStartMS;
		long estimatedDateFinishRealMS = dateStartReal.getTime() + diference;
		return new Date(estimatedDateFinishRealMS);
	}

}