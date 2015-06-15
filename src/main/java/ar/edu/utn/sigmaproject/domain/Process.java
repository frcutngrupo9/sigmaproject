package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class Process implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	Integer idPiece;
	Integer idProcessType;
	Long time;
	
	public Process(Integer idPiece, Integer idProcessType, Long time) {
		this.idPiece = idPiece;
		this.idProcessType = idProcessType;
		this.time = time;
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
	
	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
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
		if (idPiece != null && idProcessType != null) {
			if (other.idPiece != null && other.idProcessType != null) {
				if (other.idPiece == idPiece && other.idProcessType == idProcessType)
					return true;
			}
		}
		return false;
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
