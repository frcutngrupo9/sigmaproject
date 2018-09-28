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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Area;
import org.zkoss.zul.Button;
import org.zkoss.zul.Chart;
import org.zkoss.zul.GanttModel;
import org.zkoss.zul.GanttModel.GanttTask;
import org.zkoss.zul.Include;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.ProcessState;
import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.util.GanttChartEngine;
import ar.edu.utn.sigmaproject.util.ProductionDateTimeHelper;


@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProductionOrderGanttController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Chart ganttChart;
	@Wire
	Button cancelButton;
	@Wire
	Window win_production_order_gantt;

	// services

	// atributes
	private ProductionOrder currentProductionOrder;

	// list

	// list models

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentProductionOrder = (ProductionOrder) Executions.getCurrent().getAttribute("selected_production_order");
		if(currentProductionOrder == null) {throw new RuntimeException("ProductionOrder not found");}

		ganttChart.setType("gantt");
		ganttChart.setEngine(new GanttChartEngine());
		ganttChart.setModel(getModel());

		String title = currentProductionOrder.getProductionPlan().getName() + ", Orden: " + currentProductionOrder.getNumber() + ", Producto: " + currentProductionOrder.getProduct().getDescription();
		win_production_order_gantt.setTitle(title);
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		Include include = (Include) Selectors.iterable(this.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/production_follow_up_list.zul");
	}

	@Listen("onClick = #ganttChart")
	public void ganttChartOnClick(MouseEvent event) {
		Area area = (Area) event.getAreaComponent();
		alert(area.getTooltiptext());
	}

	public GanttModel getModel() {
		GanttModel ganttmodel = new GanttModel();
		String processNamePrev = null;
		String processName= null;
		Date dateStart = null;
		Date dateFinish = null;
		
		String planificado = Labels.getLabel("scheduled");
		String real = Labels.getLabel("actual");

		String processNamePrevReal = null;
		String processNameReal= null;
		Date dateStartReal = null;
		Date dateFinishReal = null;
		for(ProductionOrderDetail each : sortProductionOrderDetailListByProcessTypeSequence(currentProductionOrder.getDetails())) {
			if(each.getState() != ProcessState.Cancelado) {
				// si hay muchos procesos iguales para diferentes piezas, se los acumula y se muestran como 1 solo
				processName = each.getProcess().getType().getName();
				if(processNamePrev == null) {// primera vez
					processNamePrev = processName;
					dateStart = each.getDateStart();
					dateFinish = each.getDateFinish();
				}
				if(processName.equalsIgnoreCase(processNamePrev)) {// si son iguales se guarda la fecha fin
					dateFinish = each.getDateFinish();
				} else {// si son diferentes se agrega el previo al gantt y se carga el nuevo como previo
					// si la fecha de inico y fin son 2 dias diferentes, se los corta en el tiempo que no se trabaja
					if(areTheSameDay(dateStart, dateFinish)) {
						ganttmodel.addValue(planificado, new GanttTask(processNamePrev, dateStart, dateFinish, 0.0));
					} else {
						ganttmodel.addValue(planificado, new GanttTask(processNamePrev + "(1)", dateStart, ProductionDateTimeHelper.getLastHourOfDay(dateStart), 0.0));
						ganttmodel.addValue(planificado, new GanttTask(processNamePrev + "(2)", ProductionDateTimeHelper.getFirstHourOfDay(dateFinish), dateFinish, 0.0));
					}
					processNamePrev = processName;
					dateStart = each.getDateStart();
					dateFinish = each.getDateFinish();
				}
				//ganttmodel.addValue(planificado, new GanttTask(each.getProcess().getType().getName(), each.getDateStart(), each.getDateFinish(), 0.0));
				if(each.getDateStartReal() != null && each.getDateFinishReal() != null) {
					processNameReal = each.getProcess().getType().getName();
					if(processNamePrevReal == null) {// primera vez
						processNamePrevReal = processNameReal;
						dateStartReal = each.getDateStartReal();
						dateFinishReal = each.getDateFinishReal();
					}
					if(processNameReal.equalsIgnoreCase(processNamePrevReal)) {// si son iguales se guarda la fecha fin
						dateFinishReal = each.getDateFinishReal();
					} else {// si son diferentes se agrega el previo al gantt y se carga el nuevo como previo
						if(areTheSameDay(dateStartReal, dateFinishReal)) {
							ganttmodel.addValue(real, new GanttTask(processNamePrevReal, dateStartReal, dateFinishReal, 0.0));
						} else {
							ganttmodel.addValue(real, new GanttTask(processNamePrevReal + "(1)", dateStartReal, ProductionDateTimeHelper.getLastHourOfDay(dateStartReal), 0.0));
							ganttmodel.addValue(real, new GanttTask(processNamePrevReal + "(2)", ProductionDateTimeHelper.getFirstHourOfDay(dateFinishReal), dateFinishReal, 0.0));
						}
						processNamePrevReal = processNameReal;
						dateStartReal = each.getDateStartReal();
						dateFinishReal = each.getDateFinishReal();
					}
					//ganttmodel.addValue(real, new GanttTask(each.getProcess().getType().getName(), each.getDateStartReal(), each.getDateFinishReal(), 0.0));
				}
			}
		}
		// si quedo algo
		if(processNamePrev != null) {
			if(areTheSameDay(dateStart, dateFinish)) {
				ganttmodel.addValue(planificado, new GanttTask(processNamePrev, dateStart, dateFinish, 0.0));
			} else {
				ganttmodel.addValue(planificado, new GanttTask(processNamePrev + "(1)", dateStart, ProductionDateTimeHelper.getLastHourOfDay(dateStart), 0.0));
				ganttmodel.addValue(planificado, new GanttTask(processNamePrev + "(2)", ProductionDateTimeHelper.getFirstHourOfDay(dateFinish), dateFinish, 0.0));
			}
		}
		if(processNamePrevReal != null) {
			if(areTheSameDay(dateStartReal, dateFinishReal)) {
				ganttmodel.addValue(real, new GanttTask(processNamePrevReal, dateStartReal, dateFinishReal, 0.0));
			} else {
				ganttmodel.addValue(real, new GanttTask(processNamePrevReal + "(1)", dateStartReal, ProductionDateTimeHelper.getLastHourOfDay(dateStartReal), 0.0));
				ganttmodel.addValue(real, new GanttTask(processNamePrevReal + "(2)", ProductionDateTimeHelper.getFirstHourOfDay(dateFinishReal), dateFinishReal, 0.0));
			}
		}
		return ganttmodel;
	}

	private boolean areTheSameDay(Date dateStart, Date dateFinish) {
		Calendar dateStartCalendar = Calendar.getInstance();
		dateStartCalendar.setTime(dateStart);
		Calendar dateFinishCalendar = Calendar.getInstance();
		dateFinishCalendar.setTime(dateFinish);
		if(dateStartCalendar.get(Calendar.DAY_OF_YEAR) == dateFinishCalendar.get(Calendar.DAY_OF_YEAR)) {
			return true;
		}
		return false;
	}

	private List<ProductionOrderDetail> sortProductionOrderDetailListByProcessTypeSequence(List<ProductionOrderDetail> listParameter) {
		List<ProductionOrderDetail> list = new ArrayList<ProductionOrderDetail>();
		list.addAll(listParameter);
		Comparator<ProductionOrderDetail> comp = new Comparator<ProductionOrderDetail>() {
			@Override
			public int compare(ProductionOrderDetail a, ProductionOrderDetail b) {
				return a.getProcess().getType().getSequence().compareTo(b.getProcess().getType().getSequence());
			}
		};
		Collections.sort(list, comp);
		return list;
	}
}