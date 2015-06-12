package ar.edu.utn.sigmaproject.controller;


import ar.edu.utn.sigmaproject.service.AuthService;
import ar.edu.utn.sigmaproject.service.AuthenticationServiceImpl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;

public class LogoutController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;
	
	//services
	AuthService authService = new AuthenticationServiceImpl();
	
	@Listen("onClick=#logout")
	public void doLogout(){
		authService.logout();		
		Executions.sendRedirect("/");
	}
}
