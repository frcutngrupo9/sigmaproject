package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class Machine implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	Integer id;
	Integer idMachineType;
	String code;
	String name;
	Integer year;
	Long usedTime;

	public Machine(Integer id, Integer idMachineType, String code, String name, Integer year, Long usedTime) {
		this.id = id;
		this.idMachineType = idMachineType;
		this.code = code;
		this.name = name;
		this.year = year;
		this.usedTime = usedTime;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Long getUsedTime() {
		return usedTime;
	}

	public void setUsedTime(Long usedTime) {
		this.usedTime = usedTime;
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
		Machine other = (Machine) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public static Machine clone(Machine other){
		try {
			return (Machine)other.clone();
		} catch (CloneNotSupportedException e) {
			//not possible
		}
		return null;
	}
}
