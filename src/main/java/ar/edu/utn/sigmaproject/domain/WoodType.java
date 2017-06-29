package ar.edu.utn.sigmaproject.domain;

import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Indexed
@Analyzer(definition = "edge_ngram")
@Entity
public class WoodType implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Field
	String name = "";

	@Field
	String details = "";

	@ContainedIn
	@OneToMany(mappedBy = "woodType")
	Set<Wood> woods = new HashSet<>();

	public WoodType() {

	}

	public WoodType(String name, String details) {
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
