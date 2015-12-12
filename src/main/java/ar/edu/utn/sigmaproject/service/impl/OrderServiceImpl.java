package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.domain.OrderState;
import ar.edu.utn.sigmaproject.service.OrderDetailService;
import ar.edu.utn.sigmaproject.service.OrderService;
import ar.edu.utn.sigmaproject.service.OrderStateService;
import ar.edu.utn.sigmaproject.service.OrderStateTypeService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class OrderServiceImpl implements OrderService {
    
    static List<Order> orderList = new ArrayList<Order>();
    private SerializationService serializator = new SerializationService("order");
    
    public OrderServiceImpl() {
        List<Order> aux = serializator.obtenerLista();
        if(aux != null) {
            orderList = aux;
        } else {
            serializator.grabarLista(orderList);
        }
    }
    
    public synchronized List<Order> getOrderList() {
        List<Order> list = new ArrayList<Order>();
        for(Order order : orderList) {
            list.add(Order.clone(order));
        }
        return list;
    }
    
    public synchronized List<Order> getOrderList(Integer idOrderStateType) {
        List<Order> list = new ArrayList<Order>();
        OrderStateService orderStateService = new OrderStateServiceImpl();
        for(Order order : orderList) {
        	Integer order_id = order.getId();
        	OrderState order_state = orderStateService.getLastOrderState(order_id);
        	if(order_state.getIdOrderStateType().equals(idOrderStateType)) {
        		list.add(Order.clone(order));
        	}
        }
        return list;
    }
    
    public synchronized Order getOrder(Integer id) {
        int size = orderList.size();
        for(int i = 0; i < size; i++){
            Order t = orderList.get(i);
            if(t.getId().equals(id)) {
                return Order.clone(t);
            }
        }
        return null;
    }
    
    public synchronized Order saveOrder(Order order) {
        if(order.getId() == null) {
            order.setId(getNewId());
        }
        order = Order.clone(order);
        orderList.add(order);
        serializator.grabarLista(orderList);
        return order;
    }
    
    public synchronized Order updateOrder(Order order) {
        if(order.getId() == null) {
            throw new IllegalArgumentException("can't update a null-id order, save it first");
        } else {
            order = Order.clone(order);
            int size = orderList.size();
            for(int i = 0; i < size; i++) {
                Order t = orderList.get(i);
                if(t.getId().equals(order.getId())) {
                    orderList.set(i, order);
                    serializator.grabarLista(orderList);
                    return order;
                }
            }
            throw new RuntimeException("Order not found "+order.getId());
        }
    }
    
    public synchronized void deleteOrder(Order order) {
        if(order.getId() != null) {
        	OrderDetailService orderDetailService = new OrderDetailServiceImpl();
        	OrderStateService orderStateService = new OrderStateServiceImpl();
            int size = orderList.size();
            for(int i = 0; i < size; i++) {
                Order t = orderList.get(i);
                if(t.getId().equals(order.getId())) {
                    orderList.remove(i);
                    serializator.grabarLista(orderList);
                    // se deben eliminar todos los detalles del pedido tbn
                    orderDetailService.deleteAll(order.getId());
                    // debemos eliminar todos los estados que hagan referencia a este pedido
                    orderStateService.deleteAll(order.getId());
                    return;
                }
            }
        }
    }
    
    public synchronized Integer getNewId() {
        Integer lastId = 0;
        for(int i = 0; i < orderList.size(); i++) {
            Order aux = orderList.get(i);
            if(lastId < aux.getId()) {
                lastId = aux.getId();
            }
        }
        return lastId + 1;
    }

	public synchronized Order saveOrder(Order order, Integer idOrderStateType, List<OrderDetail> orderDetailList) {
		order = saveOrder(order);// se obtiene un pedido con id agregado
		OrderDetailService orderDetailService = new OrderDetailServiceImpl();
		for(OrderDetail orderDetail : orderDetailList) {// agregamos el id del pedido a todos los detalles y los guardamos
			orderDetail.setIdOrder(order.getId());
			orderDetailService.saveOrderDetail(orderDetail);
		}
		OrderState aux = new OrderState(order.getId(), idOrderStateType, new Date());
		OrderStateService orderStateService = new OrderStateServiceImpl();
		orderStateService.saveOrderState(aux);// grabamos el estado del pedido
		return order;
	}

	public synchronized Order updateOrder(Order order, Integer idOrderStateType, List<OrderDetail> orderDetailList, List<OrderDetail> lateDeleteOrderDetailList) {
		order = updateOrder(order);
		OrderDetailService orderDetailService = new OrderDetailServiceImpl();
		for(OrderDetail orderDetail : orderDetailList) {// hay que actualizar los detalles que existen y agregar los que no
			OrderDetail aux = orderDetailService.getOrderDetail(orderDetail.getIdOrder(), orderDetail.getIdProduct());
			if(aux == null) {// no existe se agrega
				orderDetail.setIdOrder(order.getId());// agregamos el id del pedido
				orderDetailService.saveOrderDetail(orderDetail);
			} else {// existe, se actualiza
				orderDetailService.updateOrderDetail(orderDetail);
			}
		}
		for(OrderDetail lateDeleteOrderDetail : lateDeleteOrderDetailList) {// y eliminar los detalles que se eliminaron
			orderDetailService.deleteOrderDetail(lateDeleteOrderDetail);
		}
		OrderStateService orderStateService = new OrderStateServiceImpl();
		if(idOrderStateType != null) {
			int order_id = order.getId();
			if(idOrderStateType != orderStateService.getLastOrderState(order_id).getIdOrderStateType()) {// si el estado seleccionado es diferente al ultimo guardado
				OrderState aux = new OrderState(order_id, idOrderStateType, new Date());
				orderStateService.saveOrderState(aux);
			}
		}
		return order;
	}
    
}
