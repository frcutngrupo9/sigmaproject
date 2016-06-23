package ar.edu.utn.sigmaproject.controller;

//import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;

import ar.edu.utn.sigmaproject.web.Attributes;

//@org.springframework.stereotype.Component("logoutController")
//@Scope("desktop")
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class LogoutController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	public LogoutController() {
		System.out.println("");
	}

	@Transactional
	@Listen("onClick=#logout")
	public void doLogout(){
		Sessions.getCurrent().removeAttribute(Attributes.USER_CREDENTIAL);
		Executions.sendRedirect("/");
	}
}
