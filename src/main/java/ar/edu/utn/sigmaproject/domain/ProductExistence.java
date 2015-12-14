package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class ProductExistence implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    
    Integer idProduct;
    Integer stock;
    Integer stockMin;
    Integer stockRepo;
    
    public ProductExistence(Integer idProduct, Integer stock, Integer stockMin, Integer stockRepo) {
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

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public Integer getStockMin() {
		return stockMin;
	}

	public void setStockMin(Integer stockMin) {
		this.stockMin = stockMin;
	}

	public Integer getStockRepo() {
		return stockRepo;
	}

	public void setStockRepo(Integer stockRepo) {
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
