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

import java.awt.Color;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.chart.renderer.category.GradientBarPainter;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkex.zul.impl.JFreeChartEngine;
import org.zkoss.zul.Area;
import org.zkoss.zul.Chart;
import org.zkoss.zul.GanttModel;
import org.zkoss.zul.GanttModel.GanttTask;

import ar.edu.utn.sigmaproject.domain.ProcessState;
import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;


@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProductionOrderGanttController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Chart ganttChart;

	// services

	// atributes
	private String category;
	private ProductionOrder currentProductionOrder;

	// list

	// list models

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentProductionOrder = (ProductionOrder) Executions.getCurrent().getAttribute("selected_production_order");
		if(currentProductionOrder == null) {throw new RuntimeException("ProductionOrder not found");}


		ganttChart.setType("gantt");
		ganttChart.setEngine(new GanttChartEngine2());
		ganttChart.setModel(getModel(category = "both"));
	}

	@Listen("onClick = #ganttChart")
	public void ganttChartOnClick(MouseEvent event) {
		Area area = (Area) event.getAreaComponent();
		alert(area.getTooltiptext());
	}

	public GanttModel getModel(String category) {
		GanttModel ganttmodel = new GanttModel();
		for(ProductionOrderDetail each : currentProductionOrder.getDetails()) {
			if(each.getState() != ProcessState.Cancelado) {
				if ("both".equals(category) || "actual".equals(category)) {
					ganttmodel.addValue("Estimado", new GanttTask(each.getProcess().getType().getName(), each.getDateStart(), each.getDateFinish(), 0.0));
				}
				if(each.getDateStartReal() != null && each.getDateFinishReal() != null) {
					if ("both".equals(category) || "scheduled".equals(category)) {
						ganttmodel.addValue("Real", new GanttTask(each.getProcess().getType().getName(), each.getDateStartReal(), each.getDateFinishReal(), 0.0));
					}
				}
			}
		}
		return ganttmodel;
	}

}

class GanttChartEngine2 extends JFreeChartEngine {
	private static final long serialVersionUID = 1L;

	public Color task1Color;
	public Color task2Color;
	public Color shadowColor;
	public Color completeColor;
	public Color incompleteColor;
	public boolean gradientBar = false;

	public GanttChartEngine2(){
		setColorSet(1);
	}

	public boolean prepareJFreeChart(JFreeChart jfchart, Chart chart) {
		if (task1Color == null)
			return false;
		CategoryPlot plot = (CategoryPlot) jfchart.getPlot();
		GanttRenderer renderer = (GanttRenderer) plot.getRenderer();
		renderer.setBarPainter(gradientBar ? new GradientBarPainter() : new StandardBarPainter());
		renderer.setIncompletePaint(incompleteColor);
		renderer.setCompletePaint(completeColor);
		renderer.setShadowPaint(shadowColor);
		renderer.setSeriesPaint(0, task1Color);
		renderer.setSeriesPaint(1, task2Color);
		return false;
	}

	public void setColorSet(int value) {
		switch (value) {
		case 1:
			gradientBar = false;
			task1Color = new Color(0x083643);
			task2Color = new Color(0xB1E001);
			shadowColor = new Color(0x1D3C42);
			completeColor = new Color(0xCEF09D);
			incompleteColor = new Color(0x476C5E);
			break;
		case 2:
			gradientBar = false;
			task1Color = new Color(0xADCF4F);
			task2Color = new Color(0xF2EC99);
			shadowColor = new Color(0x84815B);
			completeColor = new Color(0x4A1A2C);
			incompleteColor = new Color(0x8E3557);
			break;
		case 0:
		default: // Default Color
			task1Color = null;
		}
	}
}