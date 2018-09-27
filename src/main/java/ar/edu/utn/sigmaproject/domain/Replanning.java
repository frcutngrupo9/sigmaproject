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
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.search.annotations.Indexed;

@Entity
@Indexed
public class Replanning implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	private ProductionOrderDetail productionOrderDetail = null;

	private String cause = "";
	private Date dateInterruption = null;
	private Date dateResumption = null;

	public Replanning() {
	}

	public Replanning(String cause, Date dateInterruption, Date dateResumption, ProductionOrderDetail productionOrderDetail) {
		this.cause = cause;
		this.dateInterruption = dateInterruption;
		this.dateResumption = dateResumption;
		this.productionOrderDetail = productionOrderDetail;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ProductionOrderDetail getProductionOrderDetail() {
		return productionOrderDetail;
	}

	public void setProductionOrderDetail(ProductionOrderDetail productionOrderDetail) {
		this.productionOrderDetail = productionOrderDetail;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public Date getDateInterruption() {
		return dateInterruption;
	}

	public void setDateInterruption(Date dateInterruption) {
		this.dateInterruption = dateInterruption;
	}

	public Date getDateResumption() {
		return dateResumption;
	}

	public void setDateResumption(Date dateResumption) {
		this.dateResumption = dateResumption;
	}
}