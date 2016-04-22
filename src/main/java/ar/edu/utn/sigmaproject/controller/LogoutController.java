package ar.edu.utn.sigmaproject.controller;

import ar.edu.utn.sigmaproject.web.Attributes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class LogoutController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;
	
	@Listen("onClick=#logout")
	public void doLogout(){
		Sessions.getCurrent().removeAttribute(Attributes.USER_CREDENTIAL);
		Executions.sendRedirect("/");
	}
}
