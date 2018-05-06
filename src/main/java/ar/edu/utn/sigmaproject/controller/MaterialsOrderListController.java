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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.MaterialsOrder;
import ar.edu.utn.sigmaproject.service.MaterialsOrderRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class MaterialsOrderListController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox searchTextbox;
	@Wire
	Grid materialsOrderGrid;
	@Wire
	Button newMaterialsOrderButton;

	// services
	@WireVariable
	private MaterialsOrderRepository materialsOrderRepository;

	// list
	private List<MaterialsOrder> materialsOrderList;

	// list models
	private ListModelList<MaterialsOrder> materialsOrderListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		createReceptionListener();// se crea un listener para actualizar cuando se reciban materiales
		refreshView();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void createReceptionListener() {
		EventQueue<Event> eq = EventQueues.lookup("Materials Reception Queue", EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener() {
			public void onEvent(Event event) throws Exception {
				if(event.getName().equals("onMaterialsReception")) {
					refreshView();
				}
			}
		});
	}

	private void refreshView() {
		materialsOrderList = materialsOrderRepository.findAll();
		sortMaterialsOrdersByDate();
		materialsOrderListModel = new ListModelList<>(materialsOrderList);
		materialsOrderGrid.setModel(materialsOrderListModel);
	}

	public void sortMaterialsOrdersByDate() {
		Comparator<MaterialsOrder> comp = new Comparator<MaterialsOrder>() {
			@Override
			public int compare(MaterialsOrder a, MaterialsOrder b) {
				return b.getDate().compareTo(a.getDate());
			}
		};
		Collections.sort(materialsOrderList, comp);
	}

	@Listen("onClick = #newMaterialsOrderButton")
	public void newMaterialsOrderButtonClick() {
		Executions.getCurrent().setAttribute("selected_materials_order", null);
		Include include = (Include) Selectors.iterable(materialsOrderGrid.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/materials_order_creation.zul");
	}

	@Listen("onEditMaterialsOrder = #materialsOrderGrid")
	public void doEditMaterialsOrder(ForwardEvent evt) {
		MaterialsOrder materialsOrder = (MaterialsOrder) evt.getData();
		Executions.getCurrent().setAttribute("selected_materials_order", materialsOrder);
		Include include = (Include) Selectors.iterable(evt.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/materials_order_creation.zul");
	}

	@Listen("onMaterialsOrderReception = #materialsOrderGrid")
	public void doMaterialsOrderReception(ForwardEvent evt) {
		MaterialsOrder materialsOrder = (MaterialsOrder) evt.getData();
		final HashMap<String, MaterialsOrder> map = new HashMap<String, MaterialsOrder>();
		map.put("selected_materials_order", materialsOrder);
		Window window = (Window)Executions.createComponents("/materials_reception.zul", null, map);
		window.doModal();
	}
}
