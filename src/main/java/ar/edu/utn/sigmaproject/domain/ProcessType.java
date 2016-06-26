package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

@Entity
@Indexed
public class ProcessType implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@ManyToOne
	MachineType machineType;

	@Field
	@Column(unique = true)
	String name = "";

	@OneToMany
	List<ProcessType> predecessorList = new ArrayList<>(); 

	public ProcessType() {

	}

	public ProcessType(String name, MachineType machineType) {
		this.name = name;
		this.machineType = machineType;
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

	public MachineType getMachineType() {
		return machineType;
	}

	public void setMachineType(MachineType machineType) {
		this.machineType = machineType;
	}

	public List<ProcessType> getPredecessorList() {
		return predecessorList;
	}

	public void setPredecessorList(List<ProcessType> predecessorList) {
		this.predecessorList = predecessorList;
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
