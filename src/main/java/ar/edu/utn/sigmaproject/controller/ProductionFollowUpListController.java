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
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModelList;

import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionPlan;
import ar.edu.utn.sigmaproject.domain.ProductionPlanStateType;
import ar.edu.utn.sigmaproject.service.ProductionOrderRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanRepository;
import ar.edu.utn.sigmaproject.service.ProductionPlanStateTypeRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProductionFollowUpListController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Grid productionOrderGrid;

	// services
	@WireVariable
	private ProductionOrderRepository productionOrderRepository;
	@WireVariable
	private ProductionPlanRepository productionPlanRepository;
	@WireVariable
	private ProductionPlanStateTypeRepository productionPlanStateTypeRepository;

	// list
	private List<ProductionOrder> productionOrderList;

	// list models
	private ListModelList<ProductionOrder> productionOrderListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		refreshView();
	}

	private void refreshView() {
		// busca todos los planes de produccion que esten en estado "Abastecido" o "Lanzado" o "En Produccion", y 
		// guarda todas sus ordenes de produccion, que esten en estado "preparada" o posterior, a la lista
		ProductionPlanStateType productionPlanStateTypeAbastecido = productionPlanStateTypeRepository.findFirstByName("Abastecido");
		ProductionPlanStateType productionPlanStateTypeLanzado = productionPlanStateTypeRepository.findFirstByName("Lanzado");
		ProductionPlanStateType productionPlanStateTypeEnProduccion = productionPlanStateTypeRepository.findFirstByName("En Ejecucion");
		List<ProductionPlan> productionPlanListAbastecido = productionPlanRepository.findByCurrentStateType(productionPlanStateTypeAbastecido);
		List<ProductionPlan> productionPlanListLanzado = productionPlanRepository.findByCurrentStateType(productionPlanStateTypeLanzado);
		List<ProductionPlan> productionPlanListEnProduccion = productionPlanRepository.findByCurrentStateType(productionPlanStateTypeEnProduccion);
		List<ProductionPlan> productionPlanList = new ArrayList<ProductionPlan>();
		productionPlanList.addAll(productionPlanListAbastecido);
		productionPlanList.addAll(productionPlanListLanzado);
		productionPlanList.addAll(productionPlanListEnProduccion);
		productionOrderList = new ArrayList<ProductionOrder>();
		for(ProductionPlan eachPlan : productionPlanList) {
			for(ProductionOrder eachProductionOrder : productionOrderRepository.findByProductionPlan(eachPlan)) {
				String stateName = eachProductionOrder.getCurrentStateType().getName();
				if(stateName.equalsIgnoreCase("Preparada") || stateName.equalsIgnoreCase("Iniciada") || stateName.equalsIgnoreCase("Finalizada")) {
					productionOrderList.add(eachProductionOrder);
				}
			}
		}
		productionOrderListModel = new ListModelList<ProductionOrder>(productionOrderList);
		productionOrderGrid.setModel(productionOrderListModel);
	}

	@Listen("onEditProductionOrder = #productionOrderGrid")
	public void doEditProductionOrder(ForwardEvent evt) {
		ProductionOrder productionOrder = (ProductionOrder) evt.getData();
		Executions.getCurrent().setAttribute("selected_production_order", productionOrder);
		Include include = (Include) Selectors.iterable(evt.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/production_follow_up.zul");
	}
	
	@Listen("onGanttProductionOrder = #productionOrderGrid")
	public void doGanttProductionOrder(ForwardEvent evt) {
		ProductionOrder productionOrder = (ProductionOrder) evt.getData();
		Executions.getCurrent().setAttribute("selected_production_order", productionOrder);
		Include include = (Include) Selectors.iterable(evt.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/production_order_gantt.zul");
	}
}
