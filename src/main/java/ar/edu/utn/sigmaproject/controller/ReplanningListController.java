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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.ProcessState;
import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.service.ProductionOrderDetailRepository;
import ar.edu.utn.sigmaproject.service.ProductionOrderRepository;
import ar.edu.utn.sigmaproject.service.ReplanningRepository;
import ar.edu.utn.sigmaproject.domain.Replanning;

public class ReplanningListController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Window replanningListWindow;

	@Wire
	Listbox replanningListbox;

	// services
	@WireVariable
	private ProductionOrderRepository productionOrderRepository;
	@WireVariable
	private ProductionOrderDetailRepository productionOrderDetailRepository;
	@WireVariable
	private ReplanningRepository replanningRepository;

	private ProductionOrder currentProductionOrder;
	private ProductionOrderDetail productionOrderDetailToReplan;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentProductionOrder  = (ProductionOrder) Executions.getCurrent().getAttribute("selected_production_order");
		if(currentProductionOrder == null) {throw new RuntimeException("ProductionOrder not found");}
		findReplanningDetail();
		createListener();// listener para cuando se modifique la lista
		refreshView();
	}

	private void findReplanningDetail() {
		// busca el detalle al cual se le realizara la replanificacion y lo asigna  a la variable
		for(ProductionOrderDetail each : sortProductionOrderDetailListByProcessTypeSequence(currentProductionOrder.getDetails())) {
			if(each.getState() !=ProcessState.Cancelado && each.getState() != ProcessState.Realizado) {
				// el detalle a buscar es el primero que aun no esta realizado, es el que se cambiara de fecha
				productionOrderDetailToReplan = each;
				return;
			}
		}
		productionOrderDetailToReplan = null;
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

	private void refreshView() {
		currentProductionOrder = productionOrderRepository.findOne(currentProductionOrder.getId());
		replanningListbox.setModel(new ListModelList<Replanning>(currentProductionOrder.getReplanningList()));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createListener() {
		EventQueue<Event> eq = EventQueues.lookup("Replanning Update Queue", EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener() {
			public void onEvent(Event event) throws Exception {
				if(event.getName().equals("onReplanningUpdate")) {
					refreshView();
				}
			}
		});
	}

	@Listen("onSelect = #replanningListbox")
	public void doListBoxSelect() {
		if(replanningListbox.getSelectedItem() == null) {
			//just in case for the no selection
		} else {
			Replanning replanning = replanningListbox.getSelectedItem().getValue();
			if(replanning.getProductionOrderDetail().getState() == ProcessState.Realizado) {
				Messagebox.show("No se puede modificar, el proceso replanificado ya fue realizado.", "Informacion", Messagebox.OK, Messagebox.ERROR);
				return;
			}
			doModalWindow(replanning);
		}
		replanningListbox.clearSelection();
	}

	@Listen("onClick = #newButton")
	public void newButtonClick() {
		// verifica que no sea el ultimo proceso
		if(productionOrderDetailToReplan == null) {
			Messagebox.show("No se puede crear una nueva replanificacion, es el ultimo proceso.", "Informacion", Messagebox.OK, Messagebox.ERROR);
			return;
		}
		// verifica que no exista una replanificacion para el detalle actual
		if(findDetailReplanning() != null) {
			Messagebox.show("No se puede crear una nueva replanificacion, ya existe una replanificacion para el proceso actual.", "Informacion", Messagebox.OK, Messagebox.ERROR);
			return;
		}
		doModalWindow(null);
	}

	private Replanning findDetailReplanning() {
		for(Replanning each : currentProductionOrder.getReplanningList()) {
			if(each.getProductionOrderDetail().getId().equals(productionOrderDetailToReplan.getId())) {
				return each;
			}
		}
		return null;
	}

	private void doModalWindow(Object replanning) {
		Executions.getCurrent().setAttribute("selected_production_order_detail", productionOrderDetailToReplan);
		Executions.getCurrent().setAttribute("selected_replanning", replanning);
		final Window win = (Window) Executions.createComponents("/replanning_creation.zul", null, null);
		win.setSizable(false);
		win.setPosition("center");
		win.doModal();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onDeleteReplanning = #replanningListbox")
	public void deleteButtonClick(final ForwardEvent ForwEvt) {
		final Replanning replanning = (Replanning) ForwEvt.getData();
		if(replanning.getProductionOrderDetail().getState() == ProcessState.Realizado) {
			Messagebox.show("No se puede eliminar, el proceso replanificado ya fue realizado.", "Informacion", Messagebox.OK, Messagebox.ERROR);
			return;
		}
		Messagebox.show("Desea eliminar?", "Confirmar", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
			public void onEvent(Event evt) throws InterruptedException {
				if (evt.getName().equals("onOK")) {
					currentProductionOrder.deleteReplanning(replanning);
					productionOrderRepository.save(currentProductionOrder);
					replanningRepository.delete(replanning);
					EventQueue<Event> eq = EventQueues.lookup("Replanning Update Queue", EventQueues.DESKTOP, true);
					eq.publish(new Event("onReplanningUpdate"));
				}
			}
		});
	}
}
