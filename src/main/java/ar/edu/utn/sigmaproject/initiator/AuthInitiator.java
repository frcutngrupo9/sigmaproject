package ar.edu.utn.sigmaproject.initiator;

import java.util.Map;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Initiator;

import ar.edu.utn.sigmaproject.service.AuthenticationService;
import ar.edu.utn.sigmaproject.service.UserCredential;
import org.zkoss.zk.ui.Executions;

/**
 *
 * @author gfzabarino
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class AuthInitiator implements Initiator {

    //services
	
	@WireVariable
    AuthenticationService authenticationService;
     
    public void doInit(Page page, Map<String, Object> args) throws Exception {
    	//wire service manually by calling Selectors API
        Selectors.wireVariables(page, this, Selectors.newVariableResolvers(getClass(), null));
        
        UserCredential cre = authenticationService.getUserCredential();
        if(cre==null || cre.isAnonymous()){
            Executions.sendRedirect("/login.zul");
            return;
        }
    }
    
}
