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
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.hibernate.search.annotations.Indexed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Indexed
public class Machine implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Transient
	final Logger logger = LoggerFactory.getLogger(Machine.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Transient
	private Duration usedTime;

	@Column
	private String usedTimeInternal;

	@ManyToOne
	private MachineType machineType;

	private String name = "";
	private Integer year = Calendar.getInstance().get(Calendar.YEAR);

	public Machine() {
	}

	public Machine(MachineType machineType, String name, Integer year, Duration usedTime) {
		this.machineType = machineType;
		this.name = name;
		this.year = year;
		this.setUsedTime(usedTime);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MachineType getMachineType() {
		return machineType;
	}

	public void setMachineType(MachineType machineType) {
		this.machineType = machineType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Duration getUsedTime() {
		if (this.usedTime == null && this.usedTimeInternal != null) {
			try {
				this.usedTime = DatatypeFactory.newInstance().newDuration(this.usedTimeInternal);
			} catch (Exception e) {
				logger.error("Error while trying to deserialize Duration representation(" + this.usedTimeInternal + "): " + e.toString());
			}
		}
		return usedTime;
	}

	public void setUsedTime(Duration usedTime) {
		this.usedTime = usedTime;
		if (usedTime != null) {
			this.usedTimeInternal = usedTime.toString();
		} else {
			this.usedTimeInternal = null;
		}
	}
}