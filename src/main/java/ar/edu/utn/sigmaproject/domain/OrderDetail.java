package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;

public class OrderDetail  implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    
    Integer idOrder;
    Integer idProduct;
    Integer units;

    public OrderDetail(Integer idOrder, Integer idProduct, Integer units) {
        this.idOrder = idOrder;
        this.idProduct = idProduct;
        this.units = units;
    }
    
    public Integer getIdOrder() {
        return idOrder;
    }


    public void setIdOrder(Integer idOrder) {
        this.idOrder = idOrder;
    }


    public Integer getIdProduct() {
        return idProduct;
    }


    public void setIdProduct(Integer idProduct) {
        this.idProduct = idProduct;
    }


    public Integer getUnits() {
        return units;
    }


    public void setUnits(Integer units) {
        this.units = units;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OrderDetail other = (OrderDetail) obj;
        if (idOrder != null && idProduct != null) {
            if (other.idOrder != null && other.idProduct != null) {
                if (other.idOrder.compareTo(idOrder) == 0  && other.idProduct.compareTo(idProduct) == 0)
                    return true;
            }
        }
        return false;
    }

    public static OrderDetail clone(OrderDetail orderDetail) {
        try {
            return (OrderDetail) orderDetail.clone();
        } catch (CloneNotSupportedException e) {
            // not possible
        }
        return null;
    }
    
}