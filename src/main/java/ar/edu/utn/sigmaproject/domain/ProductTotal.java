package ar.edu.utn.sigmaproject.domain;

public class ProductTotal  extends Product {
	// clase con el objetivo de guardar el total de unidades de cada producto de un conjunto de pedidos
	// que integran un plan de produccion
	private static final long serialVersionUID = 1L;
	Integer totalUnits;

	public ProductTotal(Product product) {
		super(product.getId(), product.getCode(), product.getName(), product.getDetails(), product.getCategory(), product.getPrice());
	}

	public Integer getTotalUnits() {
		return totalUnits;
	}

	public void setTotalUnits(Integer totalUnits) {
		this.totalUnits = totalUnits;
	}

}