package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class SupplyExistence implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    
    Integer idSupplyType;
    Double stock;
    Double stockMin;
    Double stockRepo;
    
    public SupplyExistence(Integer idSupplyType, Double stock, Double stockMin, Double stockRepo) {
    	this.idSupplyType = idSupplyType;
        this.stock = stock;
        this.stockMin = stockMin;
        this.stockRepo = stockRepo;
    }
    
    public Integer getIdSupplyType() {
		return idSupplyType;
	}

	public void setIdSupplyType(Integer idSupplyType) {
		this.idSupplyType = idSupplyType;
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
        result = prime * result + ((idSupplyType == null) ? 0 : idSupplyType.hashCode());
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
        SupplyExistence other = (SupplyExistence) obj;
        if (idSupplyType == null) {
            if (other.idSupplyType != null)
                return false;
        } else if (!idSupplyType.equals(other.idSupplyType))
            return false;
        return true;
    }
    
    public static SupplyExistence clone(SupplyExistence supplyExistence){
        try {
            return (SupplyExistence)supplyExistence.clone();
        } catch (CloneNotSupportedException e) {
            //not possible
        }
        return null;
    }

}
