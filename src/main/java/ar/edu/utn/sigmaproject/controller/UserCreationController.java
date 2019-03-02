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

import org.zkoss.lang.Strings;
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
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.User;
import ar.edu.utn.sigmaproject.domain.UserRole;
import ar.edu.utn.sigmaproject.domain.UserType;
import ar.edu.utn.sigmaproject.service.UserRepository;
import ar.edu.utn.sigmaproject.service.UserTypeRepository;

public class UserCreationController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Window userCreationWindow;
	@Wire
	Textbox accountTextbox;
	@Wire
	Textbox fullNameTextbox;
	@Wire
	Textbox emailTextbox;
	@Wire
	Grid userTypeGrid;

	@WireVariable
	private UserRepository userRepository;
	@WireVariable
	private UserTypeRepository userTypeRepository;

	private User currentUser;
	private List<UserRole> currentUserRoleList;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentUser = (User) Executions.getCurrent().getAttribute("selected_user");
		createListener();
		refreshView();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createListener() {
		EventQueue<Event> eq = EventQueues.lookup("UserType Update Queue", EventQueues.DESKTOP, true);
		eq.subscribe(new EventListener() {
			public void onEvent(Event event) throws Exception {
				if(event.getName().equals("onUserTypeUpdate")) {
					refreshGridView();
				}
			}
		});
	}

	private void refreshView() {
		if(currentUser != null) {
			currentUser = userRepository.findOne(currentUser.getId());
			currentUserRoleList = currentUser.getUserRoleList();
			accountTextbox.setValue(currentUser.getAccount());
			fullNameTextbox.setValue(currentUser.getFullName());
			emailTextbox.setValue(currentUser.getEmail());
		} else {
			currentUserRoleList = new ArrayList<>();
			accountTextbox.setValue(null);
			fullNameTextbox.setValue(null);
			emailTextbox.setValue(null);
		}
		refreshGridView();
	}

	private void refreshGridView() {
		userTypeGrid.setModel(new ListModelList<UserType>(userTypeRepository.findAll()));
	}

	public boolean isSelected(UserType userType) {
		for(UserRole each : currentUserRoleList) {
			if(each.getUserType().getId() == userType.getId()) {
				return true;
			}
		}
		return false;
	}

	@Listen("onSelectUserType = #userTypeGrid")
	public void doSelectUserType(ForwardEvent evt) {
		UserType data = (UserType) evt.getData();// obtenemos el objeto pasado por parametro
		Checkbox element = (Checkbox) evt.getOrigin().getTarget();// obtenemos el elemento web
		if(element.isChecked()) {
			modifyUserTypeList(data, true);
		} else {
			modifyUserTypeList(data, false);
		}
		refreshGridView();
	}

	private void modifyUserTypeList(UserType userType, boolean add) {
		// agrega o elimina user type de la lista
		if(add) {
			currentUserRoleList.add(new UserRole(userType));
		} else {
			UserRole aux = null;
			for(UserRole each : currentUserRoleList) {
				if(each.getUserType().getId() == userType.getId()) {
					aux = each;
				}
			}
			currentUserRoleList.remove(aux);
		}
	}

	@Listen("onClick = #resetButton")
	public void resetButtonClick() {
		refreshView();
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		userCreationWindow.detach();
	}

	private boolean isNameUsed(String name) {
		for(User each : userRepository.findAll()) {
			if(each.getAccount().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	@Listen("onClick = #saveButton")
	public void saveButtonClick() {
		if(Strings.isBlank(accountTextbox.getText())) {
			Clients.showNotification("Debe ingresar un usuario", accountTextbox);
			return;
		}
		String account = accountTextbox.getText();
		String fullName = fullNameTextbox.getText();
		String email = emailTextbox.getText();
		if(currentUser == null) {
			if(isNameUsed(account)) {
				Clients.showNotification("El nombre de usuario ya existe", accountTextbox);
				return;
			}
			// nuevo
			currentUser = new User(account, null, fullName, email);
			currentUser.getUserRoleList().addAll(currentUserRoleList);
		} else {
			// edicion
			currentUser.setAccount(account);
			currentUser.setFullName(fullName);
			currentUser.setEmail(email);
		}
		// asignamos el usuario a todos los roles
		for(UserRole each : currentUserRoleList) {
			each.setUser(currentUser);
		}
		userRepository.save(currentUser);
		EventQueue<Event> eq = EventQueues.lookup("User Update Queue", EventQueues.DESKTOP, true);
		eq.publish(new Event("onUserUpdate"));
		userCreationWindow.detach();
	}

	@Listen("onClick = #userTypeListButton")
	public void userTypeListButtonClick() {
		final Window win = (Window) Executions.createComponents("/user_type_list.zul", null, null);
		win.setSizable(false);
		win.setPosition("center");
		win.doModal();
	}
}