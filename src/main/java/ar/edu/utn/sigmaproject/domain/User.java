/*
 * The MIT License
 *
 * Copyright (C) 2017 SigmaProject.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.search.annotations.Indexed;

@Entity
@Indexed
public class User implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany
	private List<UserType> userTypeList = new ArrayList<>();

	private String account = "";
	private String email = "";
	private String fullName = "";
	private String hash = "";

	public User() {
	}

	public User(String account, String hash, String fullName, String email) {
		this.account = account;
		this.fullName = fullName;
		this.hash = hash;
		this.email = email;
	}

	public Long getId() {
		return id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public List<UserType> getUserTypeList() {
		return userTypeList;
	}

	public void setUserTypeList(List<UserType> userTypeList) {
		this.userTypeList = userTypeList;
	}

	public boolean containsType(String userTypeName) {
		for(UserType each : userTypeList) {
			if(each.getName().equalsIgnoreCase(userTypeName)) {
				return true;
			}
		}
		return false;
	}
	
	public String getTypeString() {
		String type = "";
		boolean firstTime = true;
		for(UserType each : userTypeList) {
			if(firstTime) {
				firstTime = false;
			} else {
				type = type + ", ";
			}
			type = type + each.getName();
		}
		return type;
	}
}