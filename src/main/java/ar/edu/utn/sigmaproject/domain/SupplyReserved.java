package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class SupplyReserved  implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    
    Integer idSupplyType;
    Double stock;
    
    public SupplyReserved(Integer idSupplyType, Double stock) {
    	this.idSupplyType = idSupplyType;
        this.stock = stock;
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
        SupplyReserved other = (SupplyReserved) obj;
        if (idSupplyType == null) {
            if (other.idSupplyType != null)
                return false;
        } else if (!idSupplyType.equals(other.idSupplyType))
            return false;
        return true;
    }
    
    public static SupplyReserved clone(SupplyReserved supplyReserved){
        try {
            return (SupplyReserved)supplyReserved.clone();
        } catch (CloneNotSupportedException e) {
            //not possible
        }
        return null;
    }

}
