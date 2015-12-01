package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class Machine implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    
    Integer idProcessType;
    Integer idMachineType;
    
    public Machine(Integer idProcessType, Integer idMachineType) {
    	this.idProcessType = idProcessType;
        this.idMachineType = idMachineType;
    }

	public Integer getIdProcessType() {
		return idProcessType;
	}

	public void setIdProcessType(Integer idProcessType) {
		this.idProcessType = idProcessType;
	}

	public Integer getIdMachineType() {
		return idMachineType;
	}

	public void setIdMachineType(Integer idMachineType) {
		this.idMachineType = idMachineType;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Machine other = (Machine) obj;
		if (idProcessType != null && idMachineType != null) {
			if (other.idProcessType != null  && other.idMachineType != null) {
				if (other.idProcessType.equals(idProcessType) && other.idMachineType.equals(idMachineType))
					return true;
			}
		}
		return false;
	}

	public static Machine clone(Machine other) {
		try {
			return (Machine) other.clone();
		} catch (CloneNotSupportedException e) {
			// not possible
		}
		return null;
	}
}
