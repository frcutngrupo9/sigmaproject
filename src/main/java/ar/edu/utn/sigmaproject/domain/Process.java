package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

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

	@Column
	private String timeInternal;

	private boolean isClone;

	public Process() {

	}

	public Process(Piece piece, ProcessType processType, String details, Duration time) {
		this.piece = piece;
		this.type = processType;
		this.details = details;
		this.setTime(time);
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

	public boolean isClone() {
		return isClone;
	}

	public void setClone(boolean isClone) {
		this.isClone = isClone;
	}
}
