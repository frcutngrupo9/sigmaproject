package ar.edu.utn.sigmaproject.domain;

public class ProductTotal {
	// clase con el objetivo de guardar el total de unidades de cada producto de un conjunto de pedidos
	// que integran un plan de produccion

	private Product product;
	private Integer totalUnits;

	public ProductTotal(Product product, Integer totalUnits) {
		super();
		this.product = product;
		this.totalUnits = totalUnits;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Integer getTotalUnits() {
		return totalUnits;
	}

	public void setTotalUnits(Integer totalUnits) {
		this.totalUnits = totalUnits;
	}

}