package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

import javax.xml.datatype.Duration;

public class Process implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	Integer id;
	Integer idPiece;
	Integer idProcessType;
	String details;
	Duration time;
	boolean isClone;
	
	public Process(Integer id, Integer idPiece, Integer idProcessType, String details, Duration time) {
		this.id = id;
		this.idPiece = idPiece;
		this.idProcessType = idProcessType;
		this.details = details;
		this.time = time;
		isClone = false;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdPiece() {
		return idPiece;
	}

	public void setIdPiece(Integer idPiece) {
		this.idPiece = idPiece;
	}
	
	public Integer getIdProcessType() {
		return idProcessType;
	}

	public void setIdProcessType(Integer idProcessType) {
		this.idProcessType = idProcessType;
	}
	
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}
	
	public Duration getTime() {
		return time;
	}

	public void setTime(Duration time) {
		this.time = time;
	}

	public boolean isClone() {
		return isClone;
	}

	public void setClone(boolean isClone) {
		this.isClone = isClone;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Process other = (Process) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public static Process clone(Process process) {
		try {
			return (Process) process.clone();
		} catch (CloneNotSupportedException e) {
			// not possible
		}
		return null;
	}
	
}
