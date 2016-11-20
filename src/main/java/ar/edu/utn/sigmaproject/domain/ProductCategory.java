package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

import javax.persistence.*;

@Entity
public class ProductCategory implements Serializable, Cloneable {
	//	Armario,
	//	Biblioteca,
	//	Comoda,
	//	Cajonera,
	//	Cama,
	//	Escritorio,
	//	Mesa,
	//	Silla,
	//	Sillon;

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(unique = true)
	String name = "";

	public ProductCategory() {

	}

	public ProductCategory(String name) {
		this.name = name;
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
}
