package ar.edu.utn.sigmaproject.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

import javax.persistence.*;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

@Entity
public class MachineType implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Transient
	final Logger logger = LoggerFactory.getLogger(MachineType.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	String name = "";
	String details = "";

	@Transient
	Duration deteriorationTime;

	@Column
	String deteriorationTimeInternal;

	public MachineType() {

	}

	public MachineType(String name, String details, Duration deteriorationTime) {
		this.name = name;
		this.details = details;
		this.setDeteriorationTime(deteriorationTime);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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
		if (this.deteriorationTime == null && this.deteriorationTimeInternal != null) {
			try {
				this.deteriorationTime = DatatypeFactory.newInstance().newDuration(this.deteriorationTimeInternal);
			} catch (Exception e) {
				logger.error("Error while trying to deserialize Duration representation(" + this.deteriorationTimeInternal + "): " + e.toString());
			}
		}
		return deteriorationTime;
	}

	public void setDeteriorationTime(Duration deteriorationTime) {
		this.deteriorationTime = deteriorationTime;
		if (deteriorationTime != null) {
			this.deteriorationTimeInternal = deteriorationTime.toString();
		} else {
			this.deteriorationTimeInternal = null;
		}
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
