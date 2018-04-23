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
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Chart;
import org.zkoss.zul.Include;
import org.zkoss.zul.PieModel;
import org.zkoss.zul.SimplePieModel;
import org.zkoss.zul.Vbox;

import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductTotal;
import ar.edu.utn.sigmaproject.service.OrderDetailRepository;
import ar.edu.utn.sigmaproject.service.ProductRepository;

public class ReportController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Button productionOrderReportButton;
	@Wire
	Chart productChart;

	// services
	@WireVariable
	private ProductRepository productRepository;
	@WireVariable
	private OrderDetailRepository orderDetailRepository;

	// list

	// list models
	PieModel productPieModel = null;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		productChart.setThreeD(true);
		productChart.setModel(getProductPieModel());
	}

	@Listen("onClick = #productionOrderReportButton")
	public void productionOrderReportButtonClick() {
		Include include = (Include) Selectors.iterable(this.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/report_production_order.zul");
	}

	private PieModel getProductPieModel() {
		if(productPieModel == null) {
			productPieModel = new SimplePieModel();
			for(ProductTotal each : getProductTotalOrders()) {
				productPieModel.setValue(each.getProduct().getName(), each.getTotalUnits());
			}
		}
		return productPieModel;
	}

	private List<ProductTotal> getProductTotalOrders() {
		Map<Product, Integer> productTotalMap = new HashMap<Product, Integer>();
		for(Product eachProduct : productRepository.findAll()) {
			for(OrderDetail eachOrderDetail : orderDetailRepository.findByProduct(eachProduct)) {
				Integer totalUnits = productTotalMap.get(eachProduct);
				productTotalMap.put(eachProduct, (totalUnits == null) ? eachOrderDetail.getUnits() : totalUnits + eachOrderDetail.getUnits());
			}
		}
		List<ProductTotal> list = new ArrayList<ProductTotal>();
		for (Map.Entry<Product, Integer> entry : productTotalMap.entrySet()) {
			Product product = entry.getKey();
			Integer totalUnits = entry.getValue();
			ProductTotal productTotal = new ProductTotal(product, totalUnits);
			list.add(productTotal);
		}
		return list;
	}

	@Listen("onClick = #productChart")
	public void ganttChartOnClick(MouseEvent event) {
		productChart.setThreeD(!productChart.isThreeD());
		productChart.setFgAlpha(productChart.isThreeD() ? 128 : 255);
	}

}
