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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

@Entity
public class ProductionOrderDetail implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Transient
	final Logger logger = LoggerFactory.getLogger(ProductionOrderDetail.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(targetEntity = ProductionOrder.class)
	private ProductionOrder productionOrder = null;

	@ManyToOne
	private Process process;

	@ManyToOne
	private Machine machine;

	@Transient
	private Duration timeTotal;

	@Column
	private String timeTotalInternal;

	private Integer quantityPiece = 0;

	private BigDecimal quantityFinished = BigDecimal.ZERO;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, updatable = true)
	private ProcessState state;

	private Date dateStart = null;
	private Date dateFinish = null;
	private Date dateStartReal = null;
	private Date dateFinishReal = null;

	@ManyToOne
	private Worker worker = null;

	public ProductionOrderDetail() {

	}

	public ProductionOrderDetail(ProductionOrder productionOrder, Process process, ProcessState state, Machine machine, Duration timeTotal, Integer quantityPiece) {
		this.productionOrder = productionOrder;
		this.process = process;
		this.state = state;
		this.machine = machine;
		this.setTimeTotal(timeTotal);
		this.quantityPiece = quantityPiece;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public Machine getMachine() {
		return machine;
	}

	public void setMachine(Machine machine) {
		this.machine = machine;
	}

	public Duration getTimeTotal() {
		if (this.timeTotal == null && this.timeTotalInternal != null) {
			try {
				this.timeTotal = DatatypeFactory.newInstance().newDuration(this.timeTotalInternal);
			} catch (Exception e) {
				logger.error("Error while trying to deserialize Duration representation(" + this.timeTotalInternal + "): " + e.toString());
			}
		}
		return timeTotal;
	}

	public void setTimeTotal(Duration timeTotal) {
		this.timeTotal = timeTotal;
		if (timeTotal != null) {
			this.timeTotalInternal = timeTotal.toString();
		} else {
			this.timeTotalInternal = null;
		}
	}

	public Integer getQuantityPiece() {
		return quantityPiece;
	}

	public void setQuantityPiece(Integer quantityPiece) {
		this.quantityPiece = quantityPiece;
	}

	public BigDecimal getQuantityFinished() {
		return quantityFinished;
	}

	public void setQuantityFinished(BigDecimal quantityFinished) {
		this.quantityFinished = quantityFinished;
	}

	public ProcessState getState() {
		return state;
	}

	public void setState(ProcessState state) {
		this.state = state;
	}

	public Date getDateStart() {
		return dateStart;
	}

	public void setDateStart(Date dateStart) {
		this.dateStart = dateStart;
	}

	public Date getDateFinish() {
		return dateFinish;
	}

	public void setDateFinish(Date dateFinish) {
		this.dateFinish = dateFinish;
	}

	public Date getDateStartReal() {
		return dateStartReal;
	}

	public void setDateStartReal(Date dateStartReal) {
		this.dateStartReal = dateStartReal;
	}

	public Date getDateFinishReal() {
		return dateFinishReal;
	}

	public void setDateFinishReal(Date dateFinishReal) {
		this.dateFinishReal = dateFinishReal;
	}

	public Worker getWorker() {
		return worker;
	}

	public void setWorker(Worker worker) {
		this.worker = worker;
	}

	public ProductionOrder getProductionOrder() {
		return productionOrder;
	}

	public void setProductionOrder(ProductionOrder productionOrder) {
		this.productionOrder = productionOrder;
	}
}
