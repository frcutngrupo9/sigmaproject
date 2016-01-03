package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.util.Date;

public class Worker  implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    
    Integer id;
    String name;
    Date dateEmployed;
    
    public Worker(Integer id, String name, Date dateEmployed) {
        this.id = id;
        this.name = name;
        this.dateEmployed = dateEmployed;
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

    public Date getDateEmployed() {
		return dateEmployed;
	}

	public void setDateEmployed(Date dateEmployed) {
		this.dateEmployed = dateEmployed;
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
        Worker other = (Worker) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    
    public static Worker clone(Worker measureUnitType){
        try {
            return (Worker)measureUnitType.clone();
        } catch (CloneNotSupportedException e) {
            //not possible
        }
        return null;
    }
}
