package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.ProductionPlanDetail;
import ar.edu.utn.sigmaproject.service.ProductionPlanDetailService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class ProductionPlanDetailServiceImpl implements ProductionPlanDetailService{
	
	static List<ProductionPlanDetail> productionPlanDetailList = new ArrayList<ProductionPlanDetail>();
	private SerializationService serializator = new SerializationService("production_plan_detail");
	
	public ProductionPlanDetailServiceImpl() {
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
		for(ProductionPlanDetail productionPlanDetail:productionPlanDetailList){
			list.add(ProductionPlanDetail.clone(productionPlanDetail));
		}
		return list;
	}
	
	public synchronized ProductionPlanDetail getProductionPlanDetail(Integer idProductionPlan, Integer idProduct) {
		int size = productionPlanDetailList.size();
		for(int i=0;i<size;i++){
			ProductionPlanDetail aux = productionPlanDetailList.get(i);
			if(aux.getIdProductionPlan().equals(idProductionPlan) && aux.getIdProduct().equals(idProduct)){
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
		if(productionPlanDetail.getIdProductionPlan()==null && productionPlanDetail.getIdProduct()==null){
			throw new IllegalArgumentException("can't update a null-id process, save it first");
		}else{
			productionPlanDetail = ProductionPlanDetail.clone(productionPlanDetail);
			int size = productionPlanDetailList.size();
			for(int i=0;i<size;i++){
				ProductionPlanDetail aux = productionPlanDetailList.get(i);
				if(aux.getIdProductionPlan().equals(productionPlanDetail.getIdProductionPlan()) && aux.getIdProduct().equals(productionPlanDetail.getIdProduct())){
					productionPlanDetailList.set(i, productionPlanDetail);
					serializator.grabarLista(productionPlanDetailList);
					return productionPlanDetail;
				}
			}
			throw new RuntimeException("Process not found "+productionPlanDetail.getIdProductionPlan()+" "+productionPlanDetail.getIdProduct());
		}
	}
	
	public synchronized void deleteProductionPlanDetail(ProductionPlanDetail productionPlanDetail) {
		if(productionPlanDetail.getIdProductionPlan()!=null && productionPlanDetail.getIdProduct()!=null){
			int size = productionPlanDetailList.size();
			for(int i=0;i<size;i++){
				ProductionPlanDetail aux = productionPlanDetailList.get(i);
				if(aux.getIdProductionPlan().equals(productionPlanDetail.getIdProductionPlan()) && aux.getIdProduct().equals(productionPlanDetail.getIdProduct())){
					productionPlanDetailList.remove(i);
					serializator.grabarLista(productionPlanDetailList);
					return;
				}
			}
		}
	}

}
