package ar.edu.utn.sigmaproject.service.impl;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sigmaproject.domain.Supply;
import ar.edu.utn.sigmaproject.service.SupplyService;
import ar.edu.utn.sigmaproject.service.serialization.SerializationService;

public class SupplyServiceImpl implements SupplyService {
	static List<Supply> supplyList = new ArrayList<Supply>();
	private SerializationService serializator = new SerializationService("supply");
	
	public SupplyServiceImpl() {
		List<Supply> aux = serializator.obtenerLista();
		if(aux != null) {
			supplyList = aux;
		} else {
			serializator.grabarLista(supplyList);
		}
	}
	
	public synchronized List<Supply> getSupplyList() {
		List<Supply> list = new ArrayList<Supply>();
		for(Supply supply:supplyList) {
			list.add(Supply.clone(supply));
		}
		return list;
	}
	
	public synchronized List<Supply> getSupplyList(Integer idProduct) {
		List<Supply> list = new ArrayList<Supply>();
		for(Supply supply:supplyList) {
			if(supply.getIdProduct().equals(idProduct)) {
				list.add(Supply.clone(supply));
			}
		}
		return list;
	}
	
	public synchronized Supply getSupply(Integer idProduct, Integer idSupplyType) {
		int size = supplyList.size();
		for(int i = 0; i < size; i++) {
			Supply aux = supplyList.get(i);
			if(aux.getIdProduct().equals(idProduct) && aux.getIdSupplyType().equals(idSupplyType)) {
				return Supply.clone(aux);
			}
		}
		return null;
	}
	
	public synchronized Supply saveSupply(Supply supply) {
		supply = Supply.clone(supply);
		supplyList.add(supply);
		serializator.grabarLista(supplyList);
		return supply;
	}
	
	public synchronized Supply updateSupply(Supply supply) {
		if(supply.getIdProduct()==null || supply.getIdSupplyType()==null) {
			throw new IllegalArgumentException("can't update a null-id supply, save it first");
		} else {
			supply = Supply.clone(supply);
			int size = supplyList.size();
			for(int i = 0; i < size; i++) {
				Supply aux = supplyList.get(i);
				if(aux.getIdProduct().equals(supply.getIdProduct()) && aux.getIdSupplyType().equals(supply.getIdSupplyType())) {
					supplyList.set(i, supply);
					serializator.grabarLista(supplyList);
					return supply;
				}
			}
			throw new RuntimeException("Supply not found "+supply.getIdProduct()+" "+supply.getIdSupplyType());
		}
	}
	
	public synchronized void deleteSupply(Supply supply) {
		if(supply.getIdProduct()!=null && supply.getIdSupplyType()!=null) {
			int size = supplyList.size();
			for(int i = 0; i < size; i++) {
				Supply aux = supplyList.get(i);
				if(aux.getIdProduct().equals(supply.getIdProduct()) && aux.getIdSupplyType().equals(supply.getIdSupplyType())) {
					supplyList.remove(i);
					serializator.grabarLista(supplyList);
					return;
				}
			}
		}
	}
}
