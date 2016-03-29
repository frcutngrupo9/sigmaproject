package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.domain.ProductTotal;
import ar.edu.utn.sigmaproject.domain.ProductionPlanDetail;
import ar.edu.utn.sigmaproject.service.OrderDetailService;
import ar.edu.utn.sigmaproject.service.OrderService;
import ar.edu.utn.sigmaproject.service.OrderStateService;
import ar.edu.utn.sigmaproject.service.ProductService;
import ar.edu.utn.sigmaproject.service.ProductionPlanDetailService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class ProductionPlanDetailServiceImpl implements ProductionPlanDetailService {

	static List<ProductionPlanDetail> productionPlanDetailList = new ArrayList<ProductionPlanDetail>();
	private SerializationService serializator = new SerializationService("production_plan_detail");

	public ProductionPlanDetailServiceImpl() {
		@SuppressWarnings("unchecked")
		List<ProductionPlanDetail> aux = serializator.obtenerLista();
		if(aux != null) {
			productionPlanDetailList = aux;
		} else {
			serializator.grabarLista(productionPlanDetailList);
		}
	}

	//synchronized para prevenir acceso concurrente al servicio de lista
	public synchronized List<ProductionPlanDetail> getProductionPlanDetailList() {
		List<ProductionPlanDetail> list = new ArrayList<ProductionPlanDetail>();
		for(ProductionPlanDetail productionPlanDetail:productionPlanDetailList) {
			list.add(ProductionPlanDetail.clone(productionPlanDetail));
		}
		return list;
	}

	public synchronized List<ProductionPlanDetail> getProductionPlanDetailList(Integer idProductionPlan) {
		List<ProductionPlanDetail> list = new ArrayList<ProductionPlanDetail>();
		for(ProductionPlanDetail productionPlanDetail:productionPlanDetailList) {
			if(productionPlanDetail.getIdProductionPlan().equals(idProductionPlan)) {
				list.add(ProductionPlanDetail.clone(productionPlanDetail));
			}
		}
		return list;
	}

	public synchronized ProductionPlanDetail getProductionPlanDetail(Integer idProductionPlan, Integer idOrder) {
		int size = productionPlanDetailList.size();
		for(int i = 0; i < size; i++) {
			ProductionPlanDetail aux = productionPlanDetailList.get(i);
			if(aux.getIdProductionPlan().equals(idProductionPlan) && aux.getIdOrder().equals(idOrder)) {
				return ProductionPlanDetail.clone(aux);
			}
		}
		return null;
	}

	public synchronized ProductionPlanDetail saveProductionPlanDetail(ProductionPlanDetail productionPlanDetail) {
		productionPlanDetail = ProductionPlanDetail.clone(productionPlanDetail);
		productionPlanDetailList.add(productionPlanDetail);
		serializator.grabarLista(productionPlanDetailList);
		return productionPlanDetail;
	}

	public synchronized ProductionPlanDetail updateProductionPlanDetail(ProductionPlanDetail productionPlanDetail) {
		if(productionPlanDetail.getIdProductionPlan()==null || productionPlanDetail.getIdOrder()==null) {
			throw new IllegalArgumentException("can't update a null-id productionPlanDetail, save it first");
		} else {
			productionPlanDetail = ProductionPlanDetail.clone(productionPlanDetail);
			int size = productionPlanDetailList.size();
			for(int i = 0; i < size; i++) {
				ProductionPlanDetail aux = productionPlanDetailList.get(i);
				if(aux.getIdProductionPlan().equals(productionPlanDetail.getIdProductionPlan()) && aux.getIdOrder().equals(productionPlanDetail.getIdOrder())) {
					productionPlanDetailList.set(i, productionPlanDetail);
					serializator.grabarLista(productionPlanDetailList);
					return productionPlanDetail;
				}
			}
			throw new RuntimeException("ProductionPlanDetail not found "+productionPlanDetail.getIdProductionPlan()+" "+productionPlanDetail.getIdOrder());
		}
	}

	public synchronized void deleteProductionPlanDetail(ProductionPlanDetail productionPlanDetail) {
		if(productionPlanDetail.getIdProductionPlan()!=null && productionPlanDetail.getIdOrder()!=null) {
			int size = productionPlanDetailList.size();
			for(int i = 0; i < size; i++) {
				ProductionPlanDetail aux = productionPlanDetailList.get(i);
				if(aux.getIdProductionPlan().equals(productionPlanDetail.getIdProductionPlan()) && aux.getIdOrder().equals(productionPlanDetail.getIdOrder())) {
					productionPlanDetailList.remove(i);
					serializator.grabarLista(productionPlanDetailList);
					return;
				}
			}
		}
	}

	public synchronized ArrayList<ProductTotal> getProductTotalList(Integer idProductionPlan) {
		OrderService orderService = new OrderServiceImpl();
		OrderDetailService orderDetailService = new OrderDetailServiceImpl();
		ProductService productService = new ProductServiceImpl();
		List<ProductionPlanDetail> auxProductionPlanDetailList = getProductionPlanDetailList(idProductionPlan);
		ArrayList<ProductTotal> productTotalList = new ArrayList<ProductTotal>();// se empieza con una lista vacia
		for(ProductionPlanDetail auxProductionPlanDetail : auxProductionPlanDetailList) {
			Order auxOrder = orderService.getOrder(auxProductionPlanDetail.getIdOrder());// buscamos el pedido referente al detalle del plan de produccion
			List<OrderDetail> orderDetailList =  orderDetailService.getOrderDetailList(auxOrder.getId());// buscamos todos sus detalles
			for(OrderDetail auxOrderDetail : orderDetailList) {// por cada detalle del pedido, observamos si el producto ya esta en la lista, si lo esta sumamos su cantidad y, si no esta lo agregamos
				Boolean is_in_list = false;
				Integer id_product = auxOrderDetail.getIdProduct();
				Integer order_detail_units = auxOrderDetail.getUnits();
				for(ProductTotal productTotal : productTotalList) {
					if(productTotal.getId().equals(id_product)) {// si esta
						is_in_list = true;
						productTotal.setTotalUnits(productTotal.getTotalUnits() + order_detail_units);// sumamos su cantidad con la existente
						break;
					}
				}
				if(is_in_list == false) {// no esta, por lo tanto agregamos el producto a la lista total
					ProductTotal productTotal = new ProductTotal(productService.getProduct(id_product));
					productTotal.setTotalUnits(order_detail_units);// el primer valor son el total de unidades del detalle de pedido
					productTotalList.add(productTotal);
				}
			}
			// si es el primer loop del productionPlanDetailList entonces la lista productTotalList deberia estar llena solo con los productos
			// del primer pedido, en el siguiente loop se sumaran los que ya estan y agregaran los nuevos
		}
		return productTotalList;// devuelve el productTotalList lleno con todos los productos sin repetir y con el total, que conforman el plan de produccion
	}

	public synchronized void deleteAll(Integer idProductionPlan) {
		OrderStateService orderStateService = new OrderStateServiceImpl();
		List<ProductionPlanDetail> deleteList = getProductionPlanDetailList(idProductionPlan);
		for(ProductionPlanDetail delete : deleteList) {
			// debemos volver el estado de los pedidos a "iniciado"
			orderStateService.setNewOrderState("iniciado", delete.getIdOrder());
			// eliminamos el detalle
			deleteProductionPlanDetail(delete);
		}
	}
}
