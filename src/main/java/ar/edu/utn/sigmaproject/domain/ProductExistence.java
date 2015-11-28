package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class ProductExistence implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    
    Integer idProduct;
    Double stock;
    Double stockMin;
    Double stockRepo;
    
    public ProductExistence(Integer idProduct, Double stock, Double stockMin, Double stockRepo) {
    	this.idProduct = idProduct;
        this.stock = stock;
        this.stockMin = stockMin;
        this.stockRepo = stockRepo;
    }

	public Integer getIdProduct() {
		return idProduct;
	}

	public void setIdProduct(Integer idProduct) {
		this.idProduct = idProduct;
	}

	public Double getStock() {
		return stock;
	}

	public void setStock(Double stock) {
		this.stock = stock;
	}

	public Double getStockMin() {
		return stockMin;
	}

	public void setStockMin(Double stockMin) {
		this.stockMin = stockMin;
	}

	public Double getStockRepo() {
		return stockRepo;
	}

	public void setStockRepo(Double stockRepo) {
		this.stockRepo = stockRepo;
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idProduct == null) ? 0 : idProduct.hashCode());
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
        ProductExistence other = (ProductExistence) obj;
        if (idProduct == null) {
            if (other.idProduct != null)
                return false;
        } else if (!idProduct.equals(other.idProduct))
            return false;
        return true;
    }
    
    public static ProductExistence clone(ProductExistence productExistence){
        try {
            return (ProductExistence)productExistence.clone();
        } catch (CloneNotSupportedException e) {
            //not possible
        }
        return null;
    }

}
