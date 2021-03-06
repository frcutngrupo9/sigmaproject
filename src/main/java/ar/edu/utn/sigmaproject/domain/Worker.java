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
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.search.annotations.Indexed;

@Entity
@Indexed
public class Worker  implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "worker", targetEntity = WorkerRole.class)
	private List<WorkerRole> workerRoleList = new ArrayList<>();

	private String name = "";
	private Date dateEmployed = new Date();

	public Worker() {
	}

	public Worker(String name, Date dateEmployed) {
		this.name = name;
		this.dateEmployed = dateEmployed;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDateEmployed() {
		return dateEmployed;
	}

	public void setDateEmployed(Date dateEmployed) {
		this.dateEmployed = dateEmployed;
	}

	public List<WorkerRole> getWorkerRoleList() {
		return workerRoleList;
	}

	public void setWorkerRoleList(List<WorkerRole> workerRoleList) {
		this.workerRoleList = workerRoleList;
	}

	public boolean containsWorkHour(String workHourRole) {
		for(WorkerRole each : workerRoleList) {
			if(each.getWorkHour().getRole().equalsIgnoreCase(workHourRole)) {
				return true;
			}
		}
		return false;
	}

	public String getWorkHourString() {
		String type = "";
		boolean firstTime = true;
		for(WorkerRole each : workerRoleList) {
			if(firstTime) {
				firstTime = false;
			} else {
				type = type + ", ";
			}
			type = type + each.getWorkHour().getRole();
		}
		return type;
	}
}