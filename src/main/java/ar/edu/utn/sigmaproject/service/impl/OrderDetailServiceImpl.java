package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.service.OrderDetailService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class OrderDetailServiceImpl implements OrderDetailService {
    
    static List<OrderDetail> orderDetailList = new ArrayList<OrderDetail>();
    private SerializationService serializator = new SerializationService("order_detail");
    
    public OrderDetailServiceImpl() {
        List<OrderDetail> aux = serializator.obtenerLista();
        if(aux != null) {
            orderDetailList = aux;
        } else {
            serializator.grabarLista(orderDetailList);
        }
    }
    
    //synchronized para prevenir acceso concurrente al servicio de lista
    public synchronized List<OrderDetail> getOrderDetailList() {
        List<OrderDetail> list = new ArrayList<OrderDetail>();
        for(OrderDetail orderDetail:orderDetailList){
            list.add(OrderDetail.clone(orderDetail));
        }
        return list;
    }
    
	public synchronized List<OrderDetail> getOrderDetailList(Integer idOrder) {
		List<OrderDetail> list = new ArrayList<OrderDetail>();
        for(OrderDetail orderDetail:orderDetailList) {
        	if(orderDetail.getIdOrder().equals(idOrder)) {
        		list.add(OrderDetail.clone(orderDetail));
        	}
            
        }
        return list;
	}
    
    public synchronized OrderDetail getOrderDetail(Integer idOrder, Integer idProduct) {
        int size = orderDetailList.size();
        for(int i = 0; i < size; i++){
            OrderDetail aux = orderDetailList.get(i);
            if(aux.getIdOrder().equals(idOrder) && aux.getIdProduct().equals(idProduct)){
                return OrderDetail.clone(aux);
            }
        }
        return null;
    }
    
    public synchronized OrderDetail saveOrderDetail(OrderDetail orderDetail) {
        orderDetail = OrderDetail.clone(orderDetail);
        orderDetailList.add(orderDetail);
        serializator.grabarLista(orderDetailList);
        return orderDetail;
    }
    
    public synchronized OrderDetail updateOrderDetail(OrderDetail orderDetail) {
        if(orderDetail.getIdOrder()==null || orderDetail.getIdProduct()==null) {
            throw new IllegalArgumentException("can't update a null-id orderDetail, save it first");
        }else {
            orderDetail = OrderDetail.clone(orderDetail);
            int size = orderDetailList.size();
            for(int i = 0; i < size; i++) {
                OrderDetail aux = orderDetailList.get(i);
                if(aux.getIdOrder().equals(orderDetail.getIdOrder()) && aux.getIdProduct().equals(orderDetail.getIdProduct())) {
                    orderDetailList.set(i, orderDetail);
                    serializator.grabarLista(orderDetailList);
                    return orderDetail;
                }
            }
            throw new RuntimeException("OrderDetail not found "+orderDetail.getIdOrder()+" "+orderDetail.getIdProduct());
        }
    }
    
    public synchronized void deleteOrderDetail(OrderDetail orderDetail) {
        if(orderDetail.getIdOrder()!=null && orderDetail.getIdProduct()!=null) {
            int size = orderDetailList.size();
            for(int i = 0; i < size; i++) {
                OrderDetail aux = orderDetailList.get(i);
                if(aux.getIdOrder().equals(orderDetail.getIdOrder()) && aux.getIdProduct().equals(orderDetail.getIdProduct())) {
                    orderDetailList.remove(i);
                    serializator.grabarLista(orderDetailList);
                    return;
                }
            }
        }
    }

	public void deleteAll(Integer idOrder) {
		List<OrderDetail> deleteList = getOrderDetailList(idOrder);
		for(OrderDetail delete:deleteList) {
			deleteOrderDetail(delete);
		}
	}

}
