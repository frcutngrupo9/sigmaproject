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

import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

import javax.persistence.*;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

@Entity
@Indexed
@Analyzer(definition = "edge_ngram")
public class MachineType implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Transient
	final Logger logger = LoggerFactory.getLogger(MachineType.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Field
	private String name = "";

	@Field
	private String details = "";

	@Transient
	private Duration deteriorationTime;

	@Column
	private String deteriorationTimeInternal;

	public MachineType() {

	}

	public MachineType(String name, String details, Duration deteriorationTime) {
		this.name = name;
		this.details = details;
		this.setDeteriorationTime(deteriorationTime);
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

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Duration getDeteriorationTime() {
		if (this.deteriorationTime == null && this.deteriorationTimeInternal != null) {
			try {
				this.deteriorationTime = DatatypeFactory.newInstance().newDuration(this.deteriorationTimeInternal);
			} catch (Exception e) {
				logger.error("Error while trying to deserialize Duration representation(" + this.deteriorationTimeInternal + "): " + e.toString());
			}
		}
		return deteriorationTime;
	}

	public void setDeteriorationTime(Duration deteriorationTime) {
		this.deteriorationTime = deteriorationTime;
		if (deteriorationTime != null) {
			this.deteriorationTimeInternal = deteriorationTime.toString();
		} else {
			this.deteriorationTimeInternal = null;
		}
	}
}
