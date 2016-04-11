package ar.edu.utn.sigmaproject.controller;

import ar.edu.utn.sigmaproject.service.AuthenticationService;
import ar.edu.utn.sigmaproject.service.AuthenticationServiceImpl;
import ar.edu.utn.sigmaproject.service.UserCredential;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class LoginController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	//wire components
	@Wire
	Textbox account;
	@Wire
	Textbox password;
	@Wire
	Label message;

	//services
	AuthenticationService authenticationService = new AuthenticationServiceImpl();


	@Listen("onClick=#login; onOK=#loginWin")
	public void doLogin(){
		String nm = account.getValue();
		String pd = password.getValue();

		if(!authenticationService.login(nm,pd)){
			message.setValue("usuario o password incorrecto.");
			return;
		}
		UserCredential cre= authenticationService.getUserCredential();
		message.setValue("Bienvenido, "+cre.getName());
		message.setSclass("");

		Executions.sendRedirect("/");

	}
}
