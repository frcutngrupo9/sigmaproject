/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.utn.sigmaproject.service;

/**
 *
 * @author gfzabarino
 */
public interface AuthService {

	public boolean login(String account, String password);
	public void logout();
	public UserCredential getUserCredential();

}
