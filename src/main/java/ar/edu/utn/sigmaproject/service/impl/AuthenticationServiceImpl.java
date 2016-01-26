package ar.edu.utn.sigmaproject.service.impl;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import ar.edu.utn.sigmaproject.domain.User;
import ar.edu.utn.sigmaproject.service.AuthenticationService;
import ar.edu.utn.sigmaproject.service.UserCredential;
import ar.edu.utn.sigmaproject.service.UserRepository;

@Service("authenticationService")
public class AuthenticationServiceImpl implements AuthenticationService, Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	UserRepository userRepository;

	public UserCredential getUserCredential() {
		Session sess = Sessions.getCurrent();
		UserCredential cre = (UserCredential) sess.getAttribute("userCredential");
		if (cre == null) {
			cre = new UserCredential();// new a anonymous user and set to
										// session
			sess.setAttribute("userCredential", cre);
		}
		return cre;
	}

	public boolean login(String nm, String pd) {
		User user = userRepository.findByAccount(nm);
		// a simple plan text password verification
		if (user == null || !user.getPassword().equals(pd)) {
			return false;
		}

		Session sess = Sessions.getCurrent();
		UserCredential cre = new UserCredential(user.getAccount(), user.getFullName());
		// just in case for this demo.
		if (cre.isAnonymous()) {
			return false;
		}
		sess.setAttribute("userCredential", cre);

		// TODO handle the role here for authorization
		return true;
	}

	public void logout() {
		Session sess = Sessions.getCurrent();
		sess.removeAttribute("userCredential");
	}
}
