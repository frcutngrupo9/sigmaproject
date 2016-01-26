package ar.edu.utn.sigmaproject.controller;


import ar.edu.utn.sigmaproject.service.AuthenticationService;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class LogoutController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;
	
	//services
	
	@WireVariable
	AuthenticationService authenticationService;
	
	@Listen("onClick=#logout")
	public void doLogout(){
		authenticationService.logout();		
		Executions.sendRedirect("/");
	}
}
