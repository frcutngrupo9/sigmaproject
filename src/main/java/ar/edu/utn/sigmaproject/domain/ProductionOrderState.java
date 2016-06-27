package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

import javax.persistence.*;

@Entity
public class ProductionOrderState implements Serializable, Cloneable {
	//	Generada,
	//	Iniciada,
	//	Finalizada,
	//	Cancelada;

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(unique = true)
	String name = "";

	public ProductionOrderState() {

	}

	public ProductionOrderState(String name) {
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ProductionOrderState that = (ProductionOrderState) o;

		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		return name != null ? name.equals(that.name) : that.name == null;

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}
}
