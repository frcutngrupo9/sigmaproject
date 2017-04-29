package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

@Entity
public class MaterialsOrder implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToMany(orphanRemoval = true)
	@OrderColumn(name = "detail_index")
	private List<MaterialsOrderDetail> details = new ArrayList<>();
	
	private Integer number = 0;
	private Date date = null;
	
	public MaterialsOrder() {
		
	}
	
	public MaterialsOrder(Integer number, Date date) {
		this.number = number;
		this.date = date;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<MaterialsOrderDetail> getDetails() {
		return details;
	}

	public void setDetails(List<MaterialsOrderDetail> details) {
		this.details = details;
	}
}
