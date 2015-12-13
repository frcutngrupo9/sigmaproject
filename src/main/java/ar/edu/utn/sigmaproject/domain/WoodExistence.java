package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class WoodExistence implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    
    Integer idWood;
    Double stock;
    Double stockMin;
    Double stockRepo;
    
    public WoodExistence(Integer idWood, Double stock, Double stockMin, Double stockRepo) {
    	this.idWood = idWood;
        this.stock = stock;
        this.stockMin = stockMin;
        this.stockRepo = stockRepo;
    }
    
    public Integer getIdWood() {
		return idWood;
	}

	public void setIdWood(Integer idWood) {
		this.idWood = idWood;
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
        result = prime * result + ((idWood == null) ? 0 : idWood.hashCode());
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
        WoodExistence other = (WoodExistence) obj;
        if (idWood == null) {
            if (other.idWood != null)
                return false;
        } else if (!idWood.equals(other.idWood))
            return false;
        return true;
    }
    
    public static WoodExistence clone(WoodExistence other){
        try {
            return (WoodExistence)other.clone();
        } catch (CloneNotSupportedException e) {
            //not possible
        }
        return null;
    }
}
