package ar.edu.utn.sigmaproject.controller;

import ar.edu.utn.sigmaproject.domain.User;
import ar.edu.utn.sigmaproject.service.UserRepository;
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

	//wire components
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
	PasswordEncoder passwordEncoder;

	@WireVariable
	UserRepository userRepository;

	//attributes
	User currentUser;
	
	@Listen("onClick=#login; onOK=#loginWin")
	public void doLogin() {
		String nm = account.getValue();
		String pd = password.getValue();

		currentUser = userRepository.findByAccount(nm);
		final String wrongCredentialsMessage = "Usuario o password incorrecto.";
		if (currentUser == null) {
			message.setValue(wrongCredentialsMessage);
			return;
		}
		if (currentUser.getHash() == null || currentUser.getHash().length() == 0) {
			enterPasswordPopup.open(this.getSelf());
			return;
		}
		if (passwordEncoder.matches(pd, currentUser.getHash())) {
			showSuccessMessageAndRedirect();
		} else {
			message.setValue(wrongCredentialsMessage);
		}
	}

	@Listen("onClick=#setPasswordButton")
	public void onSetNewPassword() {
		String password = newPasswordTextbox.getText();
		currentUser.setHash(passwordEncoder.encode(password));
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
