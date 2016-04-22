package ar.edu.utn.sigmaproject.initiator;

import java.util.Map;

import ar.edu.utn.sigmaproject.domain.User;
import ar.edu.utn.sigmaproject.web.Attributes;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zk.ui.Executions;

/**
 *
 * @author gfzabarino
 */
public class AuthInitiator implements Initiator {

	public void doInit(Page page, Map<String, Object> args) throws Exception {
		User user = (User) Sessions.getCurrent().getAttribute(Attributes.USER_CREDENTIAL);
		if (user == null) {
			Executions.sendRedirect("/login.zul");
		}
	}

}
