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
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
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
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.Product;
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
	private List<Product> filterProductList;

	// list models

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		filterProductList = null;
		chartHelper = new ChartHelper(orderDetailRepository, productRepository, orderRepository, clientRepository);
		chartModel = null;
		Order[] firstAndLastOrderArray = chartHelper.getFirstAndLastOrder();
		dateFrom = firstAndLastOrderArray[0].getDate();
		dateTo = firstAndLastOrderArray[1].getDate();
		is3dChecked = true;
		fgAlpha = 128;
		addListeners();
		refreshView();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addListeners() {
		EventQueue eq = EventQueues.lookup("Product Filter Change Queue", EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener() {
			public void onEvent(Event event) throws Exception {
				filterProductList = (List<Product>) event.getData();
				//Clients.alert("valor de title: " + title);
				if(title != null) {
					refreshChartModel();
					refreshView();
				}
			}
		});
	}

	private void setAllValues(String type, String title) {
		this.type = type;
		this.title = title;
		refreshChartModel();
	}

	/*@Listen("onClick = #productChart")
	public void productChartOnClick(MouseEvent event) {
		if(chartModel != null) {
			boolean threeD = !is3dChecked;
			is3dChecked = threeD;
			fgAlpha = threeD ? 128 : 255;
			refreshView();
		}
	}*/

	@Listen("onCheck = #chart3dCheckbox")
	public void chart3dCheckboxOnCheck(CheckEvent event) {
		is3dChecked = chart3dCheckbox.isChecked();
		if(chartModel != null) {
			refreshView();
		}
	}

	@Listen("onScroll = #transparencySlider")
	public void transparencySliderOnScroll(ScrollEvent event) {
		fgAlpha = 256 - transparencySlider.getCurpos();
		if(chartModel != null) {
			refreshView();
		}
	}

	@Listen("onClick = #productLineChartButton")
	public void productLineChartButtonOnClick(MouseEvent event) {
		setAllValues("line", "Productos por Mes");
		refreshView();
	}

	@Listen("onClick = #productBarChartButton")
	public void productBarChartButtonOnClick() {
		setAllValues("bar", "Productos por Cliente");
		refreshView();
	}

	@Listen("onClick = #productPieChartButton")
	public void productPieChartButtonOnClick() {
		setAllValues("pie", "Productos Pedidos");
		refreshView();
	}

	@Listen("onClick = #suppliesPieChartButton")
	public void suppliesPieChartButtonOnClick() {
		setAllValues("pie", "Insumos Utilizados");
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
		if(title.equalsIgnoreCase("Productos por Mes")) {
			chartModel = chartHelper.getProductLineChartModel(dateFrom, dateTo, filterProductList);
		} else if(title.equalsIgnoreCase("Productos Pedidos")) {
			chartModel = chartHelper.getProductPieModel(dateFrom, dateTo, filterProductList);
		} else if(title.equalsIgnoreCase("Productos por Cliente")) {
			chartModel = chartHelper.getProductBarModel(dateFrom, dateTo, filterProductList);
		} else if(title.equalsIgnoreCase("Insumos Utilizados")) {
			chartModel = chartHelper.getSuppliesPieModel(dateFrom, dateTo);
		}
	}

	private void refreshView() {
		chart3dCheckbox.setChecked(is3dChecked);
		transparencySlider.setCurpos(256 - fgAlpha);
		fromDatebox.setValue(dateFrom);
		toDatebox.setValue(dateTo);
		if(chartModel != null) {
			productChart.setType(type);
			productChart.setTitle(title);
			productChart.setThreeD(is3dChecked);
			productChart.setFgAlpha(fgAlpha);
			productChart.setModel(chartModel);
		}
	}

	@Listen("onClick = #productFilterButton")
	public void productFilterButtonOnClick(MouseEvent event) {
		Executions.getCurrent().setAttribute("filterProductList", filterProductList);
		final Window win = (Window) Executions.createComponents("/product_filter.zul", null, null);
		win.setMaximizable(false);
		win.setMaximized(false);
		win.setClosable(true);
		win.setSizable(true);
		win.setPosition("center");
		win.doModal();
	}
}
