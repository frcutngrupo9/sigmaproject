package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderDetail;

public interface OrderService {

	List<Order> getOrderList();

	List<Order> getOrderList(Integer idOrderStateType);

	List<Order> getOrderListByClientId(Integer idClient);

	Order getOrder(Integer idOrder);

	Order saveOrder(Order order);

	Order saveOrder(Order order, Integer idOrderStateType, List<OrderDetail> orderDetailList);

	Order updateOrder(Order order);

	Order updateOrder(Order order, Integer idOrderStateType, List<OrderDetail> orderDetailList, List<OrderDetail> lateDeleteOrderDetailList);

	void deleteOrder(Order order);

	Integer getNewId();

}
