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

import java.math.BigDecimal;

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
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.WorkHour;
import ar.edu.utn.sigmaproject.service.WorkHourRepository;

public class WorkHourCreationController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Window workHourCreationWindow;
	@Wire
	Textbox roleTextbox;
	@Wire
	Doublebox priceDoublebox;

	@WireVariable
	private WorkHourRepository workHourRepository;

	private WorkHour currentWorkHour;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentWorkHour = (WorkHour) Executions.getCurrent().getAttribute("selected_work_hour");
		refreshView();
	}

	private void refreshView() {
		if(currentWorkHour != null) {
			roleTextbox.setValue(currentWorkHour.getRole());
			if(currentWorkHour.getPrice() != null) {
				priceDoublebox.setValue(currentWorkHour.getPrice().doubleValue());
			} else {
				priceDoublebox.setValue(null);
			}
		} else {
			roleTextbox.setValue(null);
			priceDoublebox.setValue(null);
		}
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		workHourCreationWindow.detach();
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(Strings.isBlank(roleTextbox.getText())){
			Clients.showNotification("Debe ingresar un rol", roleTextbox);
			return;
		}
		if(priceDoublebox.getValue() == null || priceDoublebox.getValue() <= 0.0) {
			Clients.showNotification("Debe ingresar valor mayor a cero", priceDoublebox);
			return;
		}
		String role = roleTextbox.getText();
		BigDecimal price = BigDecimal.valueOf(priceDoublebox.doubleValue());
		if(currentWorkHour == null) {
			// nuevo
			currentWorkHour = new WorkHour(role, price);
		} else {
			// edicion
			currentWorkHour.setRole(role);
			currentWorkHour.setPrice(price);
		}
		workHourRepository.save(currentWorkHour);
		EventQueue<Event> eq = EventQueues.lookup("WorkHour Update Queue", EventQueues.DESKTOP, true);
		eq.publish(new Event("onWorkHourUpdate"));
		workHourCreationWindow.detach();
	}
}