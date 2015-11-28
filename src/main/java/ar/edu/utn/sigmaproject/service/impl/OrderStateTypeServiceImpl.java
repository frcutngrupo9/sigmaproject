package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.OrderStateType;
import ar.edu.utn.sigmaproject.service.OrderStateTypeService;

public class OrderStateTypeServiceImpl implements OrderStateTypeService {

	static int orderStateTypeId = 1;
	static List<OrderStateType> orderStateTypeList = new ArrayList<OrderStateType>();  
	static{
		orderStateTypeList.add(new OrderStateType(orderStateTypeId++,"iniciado", ""));
		orderStateTypeList.add(new OrderStateType(orderStateTypeId++,"cancelado", ""));
		orderStateTypeList.add(new OrderStateType(orderStateTypeId++,"planificado", ""));
		orderStateTypeList.add(new OrderStateType(orderStateTypeId++,"en produccion", ""));
		orderStateTypeList.add(new OrderStateType(orderStateTypeId++,"realizado", ""));
	}
	
	public List<OrderStateType> getOrderStateTypeList() {
		List<OrderStateType> list = new ArrayList<OrderStateType>();
		for(OrderStateType orderStateType:orderStateTypeList) {
			list.add(OrderStateType.clone(orderStateType));
		}
		return list;
	}
	
	public synchronized OrderStateType getOrderStateType(Integer id) {
		int size = orderStateTypeList.size();
		for(int i = 0; i < size; i++){
			OrderStateType t = orderStateTypeList.get(i);
			if(t.getId().equals(id)) {
				return OrderStateType.clone(t);
			}
		}
		return null;
	}
	
	public synchronized OrderStateType getOrderStateType(String name) {
		int size = orderStateTypeList.size();
		for(int i = 0; i < size; i++){
			OrderStateType t = orderStateTypeList.get(i);
			if(t.getName().equalsIgnoreCase(name)) {
				return OrderStateType.clone(t);
			}
		}
		return null;
	}
}
