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

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.ProcessState;
import ar.edu.utn.sigmaproject.domain.ProductionOrder;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;
import ar.edu.utn.sigmaproject.domain.Replanning;
import ar.edu.utn.sigmaproject.service.ProductionOrderRepository;
import ar.edu.utn.sigmaproject.service.ReplanningRepository;
import ar.edu.utn.sigmaproject.util.ProductionDateTimeHelper;

public class ReplanningCreationController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Window replanningCreationWindow;
	@Wire
	Bandbox causeBandbox;
	@Wire
	Datebox interruptionDatebox;
	@Wire
	Datebox resumptionDatebox;

	@WireVariable
	private ProductionOrderRepository productionOrderRepository;
	@WireVariable
	private ReplanningRepository replanningRepository;

	private Replanning currentReplanning;
	private ProductionOrderDetail productionOrderDetailToReplan;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentReplanning = (Replanning) Executions.getCurrent().getAttribute("selected_replanning");
		productionOrderDetailToReplan = (ProductionOrderDetail) Executions.getCurrent().getAttribute("selected_production_order_detail");
		refreshView();
	}

	private void refreshView() {
		if(currentReplanning != null) {
			causeBandbox.setValue(currentReplanning.getCause());
			interruptionDatebox.setValue(currentReplanning.getDateInterruption());
			resumptionDatebox.setValue(currentReplanning.getDateResumption());
		} else {
			causeBandbox.setValue(null);
			interruptionDatebox.setValue(productionOrderDetailToReplan.getDateStart());
			resumptionDatebox.setValue(ProductionDateTimeHelper.getFirstHourOfDay(ProductionDateTimeHelper.addDays(1, productionOrderDetailToReplan.getDateStart())));
		}
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		replanningCreationWindow.detach();
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(Strings.isBlank(causeBandbox.getText())){
			Clients.showNotification("Debe ingresar una causa", causeBandbox);
			return;
		}
		if(resumptionDatebox.getValue() == null) {
			Clients.showNotification("Debe seleccionar una fecha de reanudacion", resumptionDatebox);
			return;
		}
		String cause = causeBandbox.getText();
		Date dateInterruption = interruptionDatebox.getValue();
		Date dateResumption = resumptionDatebox.getValue();//ProductionDateTimeHelper.getFirstHourOfDay(resumptionDatebox.getValue());
		if(dateResumption.before(dateInterruption)) {
			Clients.showNotification("La fecha de reanudacion no puede ser previa a la fecha de interrupcion", resumptionDatebox);
			return;
		}
		if(ProductionDateTimeHelper.isOutsideWorkingHours(dateInterruption)) {
			Clients.showNotification("La fecha de reanudacion no puede estar fuera de las horas laborales", resumptionDatebox);
			return;
		}
		if(currentReplanning == null) {
			// nuevo
			currentReplanning = new Replanning(cause, dateInterruption, dateResumption, productionOrderDetailToReplan);
		} else {
			// edicion
			currentReplanning.setCause(cause);
			currentReplanning.setDateInterruption(dateInterruption);
			currentReplanning.setDateResumption(dateResumption);
		}
		currentReplanning = replanningRepository.save(currentReplanning);
		ProductionOrder productionOrder = productionOrderDetailToReplan.getProductionOrder();
		productionOrder.setReplanning(currentReplanning);
		productionOrderRepository.save(productionOrder);
		EventQueue<Event> eq = EventQueues.lookup("Replanning Update Queue", EventQueues.DESKTOP, true);
		eq.publish(new Event("onReplanningUpdate"));
		replanningCreationWindow.detach();
	}
}