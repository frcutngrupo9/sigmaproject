package ar.edu.utn.sigmaproject.initiator;

import java.util.Map;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Initiator;
import ar.edu.utn.sigmaproject.service.AuthService;
import ar.edu.utn.sigmaproject.service.AuthenticationServiceImpl;
import ar.edu.utn.sigmaproject.service.UserCredential;
import org.zkoss.zk.ui.Executions;

/**
 *
 * @author gfzabarino
 */
public class AuthInitiator implements Initiator {

    //services
    AuthService authService = new AuthenticationServiceImpl();
     
    public void doInit(Page page, Map<String, Object> args) throws Exception {
         
        UserCredential cre = authService.getUserCredential();
        if(cre==null || cre.isAnonymous()){
            Executions.sendRedirect("/login.zul");
            return;
        }
    }
    
}
