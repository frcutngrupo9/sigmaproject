package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class ProcessType implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	Integer id;
	Integer idMachineType;
	String name;

	public ProcessType(Integer id, Integer idMachineType, String name) {
		this.id = id;
		this.idMachineType = idMachineType;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdMachineType() {
		return idMachineType;
	}

	public void setIdMachineType(Integer idMachineType) {
		this.idMachineType = idMachineType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		ProcessType other = (ProcessType) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public static ProcessType clone(ProcessType processType) {
		try {
			return (ProcessType) processType.clone();
		} catch (CloneNotSupportedException e) {
			// not possible
		}
		return null;
	}

}
