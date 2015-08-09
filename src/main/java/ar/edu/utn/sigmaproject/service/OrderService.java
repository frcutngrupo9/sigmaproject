package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.Order;

public interface OrderService {
    
    List<Order> getOrderList();
    
    Order getOrder(Integer idOrder);

    Order saveOrder(Order order);

    Order updateOrder(Order order);

    void deleteOrder(Order order);

    Integer getNewId();

}
