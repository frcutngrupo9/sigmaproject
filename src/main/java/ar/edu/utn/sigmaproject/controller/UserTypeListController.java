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

import ar.edu.utn.sigmaproject.domain.User;
import ar.edu.utn.sigmaproject.domain.UserRole;
import ar.edu.utn.sigmaproject.domain.UserType;
import ar.edu.utn.sigmaproject.service.UserRepository;
import ar.edu.utn.sigmaproject.service.UserTypeRepository;

public class UserTypeListController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Listbox userTypeListbox;

	@WireVariable
	private UserTypeRepository userTypeRepository;
	@WireVariable
	private UserRepository userRepository;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		createListener();// listener para cuando se modifique la lista
		refreshView();
	}

	private void refreshView() {
		userTypeListbox.setModel(new ListModelList<UserType>(userTypeRepository.findAll()));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createListener() {
		EventQueue<Event> eq = EventQueues.lookup("UserType Update Queue", EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener() {
			public void onEvent(Event event) throws Exception {
				if(event.getName().equals("onUserTypeUpdate")) {
					refreshView();
				}
			}
		});
	}

	@Listen("onSelect = #userTypeListbox")
	public void doListBoxSelect() {
		if(userTypeListbox.getSelectedItem() == null) {
			//just in case for the no selection
		} else {
			doModalWindow(userTypeListbox.getSelectedItem().getValue());
		}
		userTypeListbox.clearSelection();
	}

	@Listen("onClick = #newButton")
	public void newButtonClick() {
		doModalWindow(null);
	}

	private void doModalWindow(Object object) {
		Executions.getCurrent().setAttribute("selected_user_type", object);
		final Window win = (Window) Executions.createComponents("/user_type_creation.zul", null, null);
		win.setSizable(false);
		win.setPosition("center");
		win.doModal();
	}

	private boolean isUserTypeAssigned(UserType userType) {
		for(User eachUser : userRepository.findAll()) {
			for(UserRole eachUserRole : eachUser.getUserRoleList()) {
				if(eachUserRole.getUserType().getName().equalsIgnoreCase(userType.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onDeleteUserType = #userTypeListbox")
	public void deleteButtonClick(final ForwardEvent ForwEvt) {
		final UserType userType = (UserType) ForwEvt.getData();
		if(isUserTypeAssigned(userType)) {
			Messagebox.show("No se puede eliminar, el tipo de usuario se encuentra asignado a uno o mas usuarios.", "Informacion", Messagebox.OK, Messagebox.ERROR);
			return;
		}
		Messagebox.show("Desea eliminar?", "Confirmar", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
			public void onEvent(Event evt) throws InterruptedException {
				if (evt.getName().equals("onOK")) {
					userTypeRepository.delete(userType);
					EventQueue<Event> eq = EventQueues.lookup("UserType Update Queue", EventQueues.DESKTOP, true);
					eq.publish(new Event("onUserTypeUpdate"));
				}
			}
		});
	}
}