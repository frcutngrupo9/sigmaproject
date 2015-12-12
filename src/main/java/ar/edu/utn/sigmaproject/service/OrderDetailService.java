package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.OrderDetail;

public interface OrderDetailService {
    
    List<OrderDetail> getOrderDetailList();
    
    OrderDetail getOrderDetail(Integer idOrder, Integer idProduct);

    OrderDetail saveOrderDetail(OrderDetail orderDetail);

    OrderDetail updateOrderDetail(OrderDetail orderDetail);

    void deleteOrderDetail(OrderDetail orderDetail);

	List<OrderDetail> getOrderDetailList(Integer idOrder);

	void deleteAll(Integer idOrder);

}
