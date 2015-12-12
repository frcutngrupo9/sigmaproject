package ar.edu.utn.sigmaproject.service;

import java.util.List;

import ar.edu.utn.sigmaproject.domain.OrderState;

public interface OrderStateService {
	
	List<OrderState> getOrderStateList();
	
	List<OrderState> getOrderStateList(Integer idOrder);
	
	OrderState getOrderState(Integer idOrder, Integer idOrderStateType);
	
	OrderState getLastOrderState(Integer idOrder);

	OrderState saveOrderState(OrderState orderState);

	OrderState updateOrderState(OrderState orderState);

	void deleteOrderState(OrderState orderState);

	void deleteAll(Integer idOrder);

	void setNewOrderState(String stateName, Integer idOrder);

}
