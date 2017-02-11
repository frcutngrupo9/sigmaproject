package ar.edu.utn.sigmaproject.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class StockMovement implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;

	private Date date;

	// default is "in"
	private Short sign = 1;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, updatable = false)
	private StockMovementType type;

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "stockMovement", targetEntity = StockMovementDetail.class)
	private List<StockMovementDetail> details = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Short getSign() {
		return sign;
	}

	public void setSign(Short sign) {
		this.sign = sign;
	}

	public StockMovementType getType() {
		return type;
	}

	public void setType(StockMovementType type) {
		this.type = type;
	}

	public List<StockMovementDetail> getDetails() {
		return details;
	}

	public void setDetails(List<StockMovementDetail> details) {
		this.details = details;
	}
}
