package ar.edu.utn.sigmaproject.controller;


import ar.edu.utn.sigmaproject.service.AuthenticationService;
import ar.edu.utn.sigmaproject.service.AuthenticationServiceImpl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;

public class LogoutController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	//services
	AuthenticationService authenticationService = new AuthenticationServiceImpl();

	@Listen("onClick=#logout")
	public void doLogout(){
		authenticationService.logout();
		Executions.sendRedirect("/");
	}
}
