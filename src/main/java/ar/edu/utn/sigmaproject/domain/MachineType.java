package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

import javax.xml.datatype.Duration;

public class MachineType implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	Integer id;
	String name;
	String details;
	Duration deteriorationTime;
	
	public MachineType(Integer id, String name, String details, Duration deteriorationTime) {
		this.id = id;
		this.name = name;
		this.details = details;
		this.deteriorationTime = deteriorationTime;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}
	
	public Duration getDeteriorationTime() {
		return deteriorationTime;
	}

	public void setDeteriorationTime(Duration deteriorationTime) {
		this.deteriorationTime = deteriorationTime;
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
        MachineType other = (MachineType) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    
    public static MachineType clone(MachineType other) {
        try {
            return (MachineType)other.clone();
        } catch (CloneNotSupportedException e) {
            //not possible
        }
        return null;
    }
}
