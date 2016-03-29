package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class ProcessPredecessor implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	Integer id;
	Integer idProcessType;
	Integer idProcessTypePredecessor;
	
	public ProcessPredecessor(Integer id, Integer idProcessType, Integer idProcessTypePredecessor) {
		this.id = id;
		this.idProcessType = idProcessType;
		this.idProcessTypePredecessor = idProcessTypePredecessor;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdProcessType() {
		return idProcessType;
	}

	public void setIdProcessType(Integer idProcessType) {
		this.idProcessType = idProcessType;
	}

	public Integer getIdProcessTypePredecessor() {
		return idProcessTypePredecessor;
	}

	public void setIdProcessTypePredecessor(Integer idProcessTypePredecessor) {
		this.idProcessTypePredecessor = idProcessTypePredecessor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProcessPredecessor other = (ProcessPredecessor) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public static ProcessPredecessor clone(ProcessPredecessor processPredecessor) {
		try {
			return (ProcessPredecessor) processPredecessor.clone();
		} catch (CloneNotSupportedException e) {
			// not possible
		}
		return null;
	}
}
