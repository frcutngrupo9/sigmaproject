package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

import javax.persistence.*;

@Entity
public class OrderStateType implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(unique = true)
	String name = "";
	String details = "";

	public OrderStateType() {

	}

	public OrderStateType(String name, String details) {
		this.name = name;
		this.details = details;
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
}
