package ar.edu.utn.sigmaproject.service;

import ar.edu.utn.sigmaproject.domain.User;

public interface UserInfoService {

	/** find user by account **/
	public User findUser(String account);
	
	/** update user **/
	public User updateUser(User user);
}
