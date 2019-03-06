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

import ar.edu.utn.sigmaproject.domain.User;
import ar.edu.utn.sigmaproject.domain.UserRole;
import ar.edu.utn.sigmaproject.domain.UserType;
import ar.edu.utn.sigmaproject.service.UserRepository;
import ar.edu.utn.sigmaproject.service.UserTypeRepository;
import ar.edu.utn.sigmaproject.web.Attributes;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Textbox;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class LoginController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox account;
	@Wire
	Textbox password;
	@Wire
	Label message;
	@Wire
	Popup enterPasswordPopup;
	@Wire
	Textbox newPasswordTextbox;
	@Wire
	Button setPasswordButton;

	//services
	@WireVariable
	private PasswordEncoder passwordEncoder;
	@WireVariable
	private UserRepository userRepository;
	@WireVariable
	private UserTypeRepository userTypeRepository;

	//attributes
	private User currentUser;

	@Listen("onClick=#login; onOK=#loginWin")
	public void doLogin() {
		String nm = account.getValue();
		String pd = password.getValue();
		currentUser = userRepository.findByAccount(nm);
		final String wrongCredentialsMessage = "Usuario o password incorrecto.";
		if(currentUser == null) {
			message.setValue(wrongCredentialsMessage);
			return;
		}
		if(currentUser.getHash() == null || currentUser.getHash().length() == 0) {
			enterPasswordPopup.open(this.getSelf());
			return;
		}
		if(passwordEncoder.matches(pd, currentUser.getHash())) {
			showSuccessMessageAndRedirect();
		} else {
			message.setValue(wrongCredentialsMessage);
		}
	}

	@Listen("onClick=#setPasswordButton")
	public void onSetNewPassword() {
		String password = newPasswordTextbox.getText();
		currentUser.setHash(passwordEncoder.encode(password));
		if(userRepository.findAll().size() == 1 && currentUser.getUserRoleList().isEmpty()) {
			UserType userType = userTypeRepository.findFirstByName("Admin");
			UserRole userRole = new UserRole(userType);
			userRole.setUser(currentUser);
			currentUser.getUserRoleList().add(userRole);
		}
		currentUser = userRepository.save(currentUser);
		showSuccessMessageAndRedirect();
	}

	private void showSuccessMessageAndRedirect() {
		Sessions.getCurrent(true).setAttribute(Attributes.USER_CREDENTIAL, currentUser);
		message.setValue("Bienvenido, " + currentUser.getFullName());
		message.setSclass("");
		Executions.sendRedirect("/");
	}
}