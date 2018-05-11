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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.event.ScrollEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Chart;
import org.zkoss.zul.ChartModel;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Slider;

import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.service.ClientRepository;
import ar.edu.utn.sigmaproject.service.OrderDetailRepository;
import ar.edu.utn.sigmaproject.service.OrderRepository;
import ar.edu.utn.sigmaproject.service.ProductRepository;
import ar.edu.utn.sigmaproject.util.ChartHelper;

public class GraphicsController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Chart productChart;
	@Wire
	Datebox fromDatebox;
	@Wire
	Datebox toDatebox;
	@Wire
	Checkbox chart3dCheckbox;
	@Wire
	Slider transparencySlider;

	private ChartHelper chartHelper;
	private String type;
	private String title;
	private boolean is3dChecked;
	private int fgAlpha;
	private ChartModel chartModel;
	private Date dateFrom;
	private Date dateTo;

	// services
	@WireVariable
	private ProductRepository productRepository;
	@WireVariable
	private OrderDetailRepository orderDetailRepository;
	@WireVariable
	private OrderRepository orderRepository;
	@WireVariable
	private ClientRepository clientRepository;

	// list

	// list models

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		chartHelper = new ChartHelper(orderDetailRepository, productRepository, orderRepository, clientRepository);
		chartModel = null;
		Order[] firstAndLastOrderArray = chartHelper.getFirstAndLastOrder();
		dateFrom = firstAndLastOrderArray[0].getDate();
		dateTo = firstAndLastOrderArray[1].getDate();
	}

	private void setAllValues(String type, String title, boolean is3dChecked, int fgAlpha, ChartModel chartModel) {
		this.type = type;
		this.title = title;
		this.is3dChecked = is3dChecked;
		this.fgAlpha = fgAlpha;
		this.chartModel = chartModel;
	}

	@Listen("onClick = #productChart")
	public void productChartOnClick(MouseEvent event) {
		if(chartModel != null) {
			//Clients.showNotification("tipo de model: " + chartModel.getClass().toString());
			boolean threeD = !productChart.isThreeD();
			int fgAlpha = threeD ? 128 : 255;
			productChart.setThreeD(threeD);
			productChart.setFgAlpha(fgAlpha);
			chart3dCheckbox.setChecked(threeD);
			transparencySlider.setCurpos(256 - fgAlpha);
		}
	}

	@Listen("onCheck = #chart3dCheckbox")
	public void chart3dCheckboxOnCheck(CheckEvent event) {
		if(chartModel != null) {
			is3dChecked = chart3dCheckbox.isChecked();
			refreshView();
		}
	}

	@Listen("onScroll = #transparencySlider")
	public void transparencySliderOnScroll(ScrollEvent event) {
		if(chartModel != null) {
			fgAlpha = 256 - transparencySlider.getCurpos();
			refreshView();
		}
	}

	@Listen("onClick = #productLineChartButton")
	public void productLineChartButtonOnClick(MouseEvent event) {
		setAllValues("line", "Productos por Mes", false, 255, chartHelper.getProductLineChartModel(dateFrom, dateTo));
		refreshView();
	}

	@Listen("onClick = #productBarChartButton")
	public void productBarChartButtonOnClick() {
		setAllValues("bar", "Productos por Cliente", true, 255, chartHelper.getProductBarModel(dateFrom, dateTo));
		refreshView();
	}

	@Listen("onClick = #productPieChartButton")
	public void productPieChartButtonOnClick() {
		setAllValues("pie", "Productos Pedidos", true, 128, chartHelper.getProductPieModel(dateFrom, dateTo));
		refreshView();
	}

	@Listen("onChange = #fromDatebox")
	public void fromDateboxOnChange() {
		if(chartModel != null) {
			// pevenimos que la fecha sea anterior
			if(fromDatebox.getValue().after(dateTo)) {
				Clients.alert("La fecha seleccionada no puede ser posterior a Hasta.");
				// se la resetea
				fromDatebox.setValue(dateFrom);
				return;
			}
			dateFrom = fromDatebox.getValue();
			refreshChartModel();
			refreshView();
		}
	}

	@Listen("onChange = #toDatebox")
	public void toDateboxOnChange() {
		if(chartModel != null) {
			// pevenimos que la fecha sea posterior
			if(toDatebox.getValue().before(dateFrom)) {
				Clients.alert("La fecha seleccionada no puede ser previa a Desde.");
				// se la resetea
				toDatebox.setValue(dateTo);
				return;
			}
			dateTo = toDatebox.getValue();
			refreshChartModel();
			refreshView();
		}
	}

	@Listen("onClick = #beginningOfTimeButton")
	public void beginningOfTimeButtonOnClick() {
		if(chartModel != null) {
			dateFrom = chartHelper.getFirstAndLastOrder()[0].getDate();
			refreshChartModel();
			refreshView();
		}
	}

	@Listen("onClick = #endOfTimeButton")
	public void endOfTimeButtonOnClick() {
		if(chartModel != null) {
			dateTo = chartHelper.getFirstAndLastOrder()[1].getDate();
			refreshChartModel();
			refreshView();
		}
	}

	private void refreshChartModel() {
		// depende de cual chartType este seleccionado cambiamos el model
		if(type.equalsIgnoreCase("line")) {
			chartModel = chartHelper.getProductLineChartModel(dateFrom, dateTo);
		} else if(type.equalsIgnoreCase("pie")) {
			chartModel = chartHelper.getProductPieModel(dateFrom, dateTo);
		} else if(type.equalsIgnoreCase("bar")) {
			chartModel = chartHelper.getProductBarModel(dateFrom, dateTo);
		}
	}

	private void refreshView() {
		chart3dCheckbox.setChecked(is3dChecked);
		transparencySlider.setCurpos(256 - fgAlpha);
		productChart.setType(type);
		productChart.setTitle(title);
		productChart.setThreeD(is3dChecked);
		productChart.setFgAlpha(fgAlpha);
		productChart.setModel(chartModel);
		fromDatebox.setValue(dateFrom);
		toDatebox.setValue(dateTo);
	}
}
