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
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
public class Process implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Transient
	final Logger logger = LoggerFactory.getLogger(Process.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(targetEntity = Piece.class)
	private Piece piece = null;

	@ManyToOne
	private ProcessType type;

	private String details = "";

	@Transient
	private Duration time;

	@ManyToOne
	private WorkHour workHour = null;

	@Column
	private String timeInternal;

	private boolean isClone;

	public Process() {

	}

	public Process(Piece piece, ProcessType processType, String details, Duration time, WorkHour workHour) {
		this.piece = piece;
		this.type = processType;
		this.details = details;
		this.setTime(time);
		this.workHour = workHour;
		isClone = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Piece getPiece() {
		return piece;
	}

	public void setPiece(Piece piece) {
		this.piece = piece;
	}

	public ProcessType getType() {
		return type;
	}

	public void setType(ProcessType type) {
		this.type = type;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Duration getTime() {
		// devuelve el tiempo del proceso para 1 sola pieza
		if (time == null && this.timeInternal != null) {
			try {
				this.time = DatatypeFactory.newInstance().newDuration(this.timeInternal);
			} catch (Exception e) {
				logger.error("Error while trying to deserialize Duration representation(" + this.timeInternal + "): " + e.toString());
			}
		}
		return time;
	}

	public void setTime(Duration time) {
		this.time = time;
		if (time != null) {
			this.timeInternal = time.toString();
		} else {
			this.timeInternal = null;
		}
	}

	public WorkHour getWorkHour() {
		return workHour;
	}

	public void setWorkHour(WorkHour workHour) {
		this.workHour = workHour;
	}

	public String getTimeInternal() {
		return timeInternal;
	}

	public void setTimeInternal(String timeInternal) {
		this.timeInternal = timeInternal;
	}

	public boolean isClone() {
		return isClone;
	}

	public void setClone(boolean isClone) {
		this.isClone = isClone;
	}

	public BigDecimal getCost() {
		// calcula el costo de todos los procesos
		BigDecimal cost = BigDecimal.ZERO;
		//no se calcula si el rol es null
		if(getWorkHour() != null) {
			// el costo de cada proceso es el tiempo total en horas multiplicado por el costo por hora del rol
			int hours = getTime().getHours();
			int minutes = getTime().getMinutes();
			int seconds = getTime().getSeconds();
			while(seconds >= 60) {
				seconds -= 60;
				minutes += 1;
			}
			while(minutes >= 60) {
				minutes -= 60;
				hours += 1;
			}
			BigDecimal secondsToHour = (new BigDecimal(seconds)).divide(new BigDecimal(3600), 2, BigDecimal.ROUND_HALF_EVEN);
			// sumamos el decimal minutos a la hora
			BigDecimal minutesToHour = (new BigDecimal(minutes)).divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_EVEN);
			BigDecimal durationTotal = (new BigDecimal(hours)).add(minutesToHour).add(secondsToHour);
			cost = cost.add(durationTotal.multiply(getWorkHour().getPrice()));
		}
		return cost;
	}
}
