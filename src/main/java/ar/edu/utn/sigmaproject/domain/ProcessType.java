package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

@Entity
@Indexed
@Analyzer(definition = "edge_ngram")
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
}
