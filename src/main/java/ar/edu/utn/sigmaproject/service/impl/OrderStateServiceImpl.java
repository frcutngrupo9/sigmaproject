package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.OrderState;
import ar.edu.utn.sigmaproject.service.OrderStateService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class OrderStateServiceImpl  implements OrderStateService {

	static List<OrderState> orderStateList = new ArrayList<OrderState>();
	private SerializationService serializator = new SerializationService("order_state");
	
	public OrderStateServiceImpl() {
		List<OrderState> aux = serializator.obtenerLista();
		if(aux != null) {
			orderStateList = aux;
		} else {
			serializator.grabarLista(orderStateList);
		}
	}
	
	//synchronized para prevenir acceso concurrente al servicio de lista
	public synchronized List<OrderState> getOrderStateList() {
		List<OrderState> list = new ArrayList<OrderState>();
		for(OrderState orderState:orderStateList) {
			list.add(OrderState.clone(orderState));
		}
		return list;
	}
	
	public synchronized List<OrderState> getOrderStateList(Integer idOrder) {
		List<OrderState> list = new ArrayList<OrderState>();
		for(OrderState orderState:orderStateList) {
			if(orderState.getIdOrder().equals(idOrder)) {
				list.add(OrderState.clone(orderState));
			}
		}
		return list;
	}
	
	public synchronized OrderState getOrderState(Integer idOrder, Integer idOrderStateType) {
		int size = orderStateList.size();
  		for(int i = 0; i < size; i++) {
  			OrderState t = orderStateList.get(i);
  			if(t.getIdOrder().equals(idOrder) && t.getIdOrderStateType().equals(idOrderStateType)) {
  				return OrderState.clone(t);
  			}
  		}
  		return null;
	}
	
	public synchronized OrderState getLastOrderState(Integer idOrder) {
		List<OrderState> list = getOrderStateList(idOrder);
		OrderState aux = null;
		for(OrderState orderState:list) {
			if(aux != null) {
				if(aux.getDate().before(orderState.getDate())) {
					aux = orderState;
				}
			} else {
				aux = orderState;
			}
		}
		return aux;
	}
	
	public synchronized OrderState saveOrderState(OrderState orderState) {
		orderState = OrderState.clone(orderState);
		orderStateList.add(orderState);
		serializator.grabarLista(orderStateList);
		return orderState;
	}
	
	public synchronized OrderState updateOrderState(OrderState orderState) {
		if(orderState.getIdOrder() == null || orderState.getIdOrderStateType() == null) {
			throw new IllegalArgumentException("can't update a null-id orderState, save it first");
		}else {
			orderState = OrderState.clone(orderState);
			int size = orderStateList.size();
			for(int i = 0; i < size; i++) {
				OrderState t = orderStateList.get(i);
				if(t.getIdOrder().equals(orderState.getIdOrder()) && t.getIdOrderStateType().equals(orderState.getIdOrderStateType())) {
					orderStateList.set(i, orderState);
					serializator.grabarLista(orderStateList);
					return orderState;
				}
			}
			throw new RuntimeException("OrderState not found " + orderState.getIdOrder()+" "+orderState.getIdOrderStateType());
		}
	}
	
	public synchronized void deleteOrderState(OrderState orderState) {
		if(orderState.getIdOrder()!=null && orderState.getIdOrderStateType()!=null) {
			int size = orderStateList.size();
			for(int i = 0; i < size; i++) {
				OrderState t = orderStateList.get(i);
				if(t.getIdOrder().equals(orderState.getIdOrder()) && t.getIdOrderStateType().equals(orderState.getIdOrderStateType())) {
					orderStateList.remove(i);
					serializator.grabarLista(orderStateList);
					return;
				}
			}
		}
	}
}
