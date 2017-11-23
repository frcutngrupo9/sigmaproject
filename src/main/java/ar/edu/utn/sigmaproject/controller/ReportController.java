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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModelList;

import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.service.ProductionOrderRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;

public class ReportController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Button generateReportButton;
	@Wire
	Grid reportProductionPlanGrid; 

	// services
	@WireVariable
	private ProductionPlanRepository productionPlanRepository;
	@WireVariable
	private ProductionOrderRepository productionOrderRepository;

	// list
	private List<ProductionPlan> productionPlanList;

	// list models
	private ListModelList<ProductionPlan> productionPlanListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		reportProductionPlanGrid.setVisible(false);
		productionPlanList = productionPlanRepository.findAll();
		productionPlanListModel = new ListModelList<ProductionPlan>(productionPlanList);
		reportProductionPlanGrid.setModel(productionPlanListModel);
	}

	@Listen("onClick = #generateReportButton")
	public void generateReportButtonClick() {
		reportProductionPlanGrid.setVisible(true);
	}

	public Date getDateStartReal(ProductionPlan productionPlan) {
		List<ProductionOrder> productionOrderList = productionOrderRepository.findByProductionPlan(productionPlan);
		// busca la primera fecha de inicio real de ordenes de produccion
		Date date = null;
		for(ProductionOrder each : productionOrderList) {
			Date startRealDate = each.getDateStartReal();
			if(startRealDate != null) {
				if(date == null) {
					date = startRealDate;
				} else {
					if(startRealDate.before(date)) {
						date = startRealDate;
					}
				}
			}
		}
		return date;
	}

	public String getDeviation(ProductionPlan productionPlan) {
		Date dateStart = productionPlan.getDateStart();
		if(dateStart == null) {
			return "No hay Fecha Inicio";
		}
		Date dateStartReal = getDateStartReal(productionPlan);
		if(dateStartReal == null) {
			return "No hay Fecha Inicio Real";
		}
		if(dateStart.before(dateStartReal)) {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			String dateStartFormatted = dateFormat.format(dateStart);
			String dateStartRealFormatted = dateFormat.format(dateStartReal);
			if(dateStartFormatted.equals(dateStartRealFormatted)) {
				return "Esta Puntual";
			}
			return "Esta Retrasado";
		} else {
			return "Esta Adelantado";
		}
	}

	public String getFormattedDate(Date date) {
		if(date == null) {
			return "";
		}
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		return dateFormat.format(date);
	}

	@Listen("onNavigateToProductionOrder = #reportProductionPlanGrid")
	public void goToReportProductionOrder(ForwardEvent evt) {
		ProductionPlan productionPlan = (ProductionPlan) evt.getData();
		Executions.getCurrent().setAttribute("selected_production_plan", productionPlan);
		Include include = (Include) Selectors.iterable(evt.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/report_production_order.zul");
	}
}
