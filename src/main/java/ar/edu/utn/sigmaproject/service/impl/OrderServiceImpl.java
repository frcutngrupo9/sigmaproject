package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.service.OrderService;
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
        for(Order order:orderList){
            list.add(Order.clone(order));
        }
        return list;
    }
    
    public synchronized Order getOrder(Integer id) {
        int size = orderList.size();
        for(int i=0;i<size;i++){
            Order t = orderList.get(i);
            if(t.getId().equals(id)){
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
        if(order.getId() != null && order.getId().compareTo(getNewId()) == 0)
        {
            order = saveOrder(order);
            return order;
        }
        else {
            if(order.getId()==null){
                throw new IllegalArgumentException("can't update a null-id order, save it first");
            }else{
                order = Order.clone(order);
                int size = orderList.size();
                for(int i=0;i<size;i++){
                    Order t = orderList.get(i);
                    if(t.getId().equals(order.getId())){
                        orderList.set(i, order);
                        return order;
                    }
                }
                throw new RuntimeException("Order not found "+order.getId());
            }
        }
    }
    
    public synchronized void deleteOrder(Order order) {
        if(order.getId()!=null){
            int size = orderList.size();
            for(int i=0;i<size;i++){
                Order t = orderList.get(i);
                if(t.getId().equals(order.getId())){
                    orderList.remove(i);
                    serializator.grabarLista(orderList);
                    return;
                }
            }
        }
    }
    
    public synchronized Integer getNewId() {
        Integer lastId = 0;
        for(int i=0;i<orderList.size();i++){
            Order aux = orderList.get(i);
            if(lastId < aux.getId()){
                lastId = aux.getId();
            }
        }
        return lastId + 1;
    }
    
}
