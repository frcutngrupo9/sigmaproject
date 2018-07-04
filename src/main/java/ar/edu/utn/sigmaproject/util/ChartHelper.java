/*
 * The MIT License
 *
 * Copyright (C) 2017 SigmaProject.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package ar.edu.utn.sigmaproject.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.PieModel;
import org.zkoss.zul.SimpleCategoryModel;
import org.zkoss.zul.SimplePieModel;

import ar.edu.utn.sigmaproject.domain.Client;
import ar.edu.utn.sigmaproject.domain.MaterialType;
import ar.edu.utn.sigmaproject.domain.Order;
import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ProductMaterial;
import ar.edu.utn.sigmaproject.domain.ProductTotal;
import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.service.ClientRepository;
import ar.edu.utn.sigmaproject.service.OrderDetailRepository;
import ar.edu.utn.sigmaproject.service.OrderRepository;
import ar.edu.utn.sigmaproject.service.ProductRepository;

public class ChartHelper {

	private OrderDetailRepository orderDetailRepository;
	private ProductRepository productRepository;
	private OrderRepository orderRepository;
	private ClientRepository clientRepository;

	public ChartHelper(OrderDetailRepository orderDetailRepository,
			ProductRepository productRepository, OrderRepository orderRepository, ClientRepository clientRepository) {
		this.orderDetailRepository = orderDetailRepository;
		this.productRepository = productRepository;
		this.orderRepository = orderRepository;
		this.clientRepository = clientRepository;
	}

	public PieModel getProductPieModel(Date dateFrom, Date dateTo, List<Product> filterProductList) {
		PieModel productPieModel = new SimplePieModel();
		for(ProductTotal each : getFilteredProductTotalList(getProductTotalOrders(dateFrom, dateTo), filterProductList)) {
			productPieModel.setValue(each.getProduct().getName(), each.getTotalUnits());
		}
		return productPieModel;
	}
	
	private List<ProductTotal> getFilteredProductTotalList(List<ProductTotal> productTotalList, List<Product> filterProductList) {
		if(filterProductList != null) {
			List<ProductTotal> productTotalRemoveList = new ArrayList<ProductTotal>();
			// verifica que todos los de la lista se encuentren en la lista de filtro caso contrario se remueven
			for(ProductTotal eachProductTotal : productTotalList) {
				boolean isInFilter = false;
				for(Product eachProductFilter : filterProductList) {
					if(eachProductTotal.getProduct().equals(eachProductFilter)) {
						isInFilter = true;
						break;
					}
				}
				if(isInFilter == false) {
					productTotalRemoveList.add(eachProductTotal);
				}
			}
			productTotalList.removeAll(productTotalRemoveList);
		}
		return productTotalList;
	}

	public List<ProductTotal> getProductTotalOrders(Date dateFrom, Date dateTo) {
		Map<Product, Integer> productTotalMap = new HashMap<Product, Integer>();
		for(Product eachProduct : productRepository.findAll()) {
			for(OrderDetail eachOrderDetail : orderDetailRepository.findByProductAndOrderDateAfterAndOrderDateBefore(eachProduct, dateFrom, dateTo)) {
				Integer totalUnits = productTotalMap.get(eachProduct);
				productTotalMap.put(eachProduct, (totalUnits == null) ? eachOrderDetail.getUnits() : totalUnits + eachOrderDetail.getUnits());
			}
		}
		List<ProductTotal> list = new ArrayList<ProductTotal>();
		for (Map.Entry<Product, Integer> entry : productTotalMap.entrySet()) {
			Product product = entry.getKey();
			Integer totalUnits = entry.getValue();
			ProductTotal productTotal = new ProductTotal(product, totalUnits);
			list.add(productTotal);
		}
		return list;
	}

	private List<Product> getFilteredProductList(List<Product> productList, List<Product> filterProductList) {
		if(filterProductList != null) {
			List<Product> productRemoveList = new ArrayList<Product>();
			// verifica que todos los de la lista se encuentren en la lista de filtro caso contrario se remueven
			for(Product eachProduct : productList) {
				boolean isInFilter = false;
				for(Product eachProductFilter : filterProductList) {
					if(eachProduct.equals(eachProductFilter)) {
						isInFilter = true;
						break;
					}
				}
				if(isInFilter == false) {
					productRemoveList.add(eachProduct);
				}
			}
			productList.removeAll(productRemoveList);
		}
		return productList;
	}

	public CategoryModel getProductLineChartModel(Date firstOrderDate, Date lastOrderDate, List<Product> filterProductList) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(firstOrderDate);
		int searchStartYear = calendar.get(Calendar.YEAR);
		int searchStartMonth = calendar.get(Calendar.MONTH);
		calendar.clear();
		calendar.setTime(lastOrderDate);
		int searchFinishYear = calendar.get(Calendar.YEAR);
		int searchFinishMonth = calendar.get(Calendar.MONTH);
		CategoryModel  model = new SimpleCategoryModel();
		for(Product eachProduct : getFilteredProductList(getProductList(), filterProductList)) {
			for(int year=searchStartYear; year <=searchFinishYear; year++) {
				int startMonth;
				if(year == searchStartYear) {
					startMonth = searchStartMonth;
				} else {
					startMonth = 1;
				}
				int finishMonth;
				if(year == searchFinishYear) {
					finishMonth = searchFinishMonth;
				} else {
					finishMonth = 12;
				}
				String yearSubstring = year + "";
				yearSubstring = yearSubstring.substring(2);
				// recorre la lista de meses
				for(int eachMonth=startMonth; eachMonth<=finishMonth; eachMonth++) {
					model.setValue(eachProduct.getName(), monthNameByNumber(eachMonth) + " " + yearSubstring, monthUnits(year, eachMonth, eachProduct));
				}
			}
		}
		return model;
	}

	public String monthNameByNumber(int montNumber) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, montNumber);
		Date date = calendar.getTime();
		DateFormat dateFormat = new SimpleDateFormat("MMM");
		return dateFormat.format(date);
	}

	public Order[] getFirstAndLastOrder() {
		Order[] orderArray = new Order[2];
		for(Order each : orderRepository.findAll()) {
			if(each.getDate() != null) {
				if(orderArray[0] == null) {
					orderArray[0] = each;
				} else {
					if(each.getDate().before(orderArray[0].getDate())) {
						orderArray[0] = each;
					}
				}
				if(orderArray[1] == null) {
					orderArray[1] = each;
				} else {
					if(each.getDate().after(orderArray[1].getDate())) {
						orderArray[1] = each;
					}
				}
			}
		}
		return orderArray;
	}

	private List<Product> getProductList() {
		List<Product> productList = new ArrayList<Product>();
		// devuelve solo los productos que existan en al menos 1 pedido
		Order[] orderArray = getFirstAndLastOrder();
		for(ProductTotal eachProductTotal : getProductTotalOrders(orderArray[0].getDate(), orderArray[1].getDate())) {
			if(eachProductTotal.getTotalUnits() != 0) {
				productList.add(eachProductTotal.getProduct());
			}
		}
		return productList;
	}

	private Integer monthUnits(int eachYear, int eachMonth, Product eachProduct) {
		// devuelve la cantidad del producto pedido en el mes
		Calendar calendar = Calendar.getInstance();
		Map<Product, Integer> productTotalMap = new HashMap<Product, Integer>();
		// recorre todos los pedidos donde aparece el producto y devuelve la cantidad total del mes parametro
		for(Order eachOrder : orderRepository.findAll()) {
			calendar.clear();
			calendar.setTime(eachOrder.getDate());
			// verifica que el mes sea el requerido
			if(calendar.get(Calendar.YEAR) == eachYear && calendar.get(Calendar.MONTH) == eachMonth) {
				// verifica si el producto se encuentra en el pedido
				for(OrderDetail eachOrderDetail : eachOrder.getDetails()) {
					if(eachOrderDetail.getProduct().equals(eachProduct)) {
						Integer totalUnits = productTotalMap.get(eachProduct);
						productTotalMap.put(eachProduct, (totalUnits == null) ? eachOrderDetail.getUnits() : totalUnits + eachOrderDetail.getUnits());
					}
				}
			}
		}
		Integer totalUnits = productTotalMap.get(eachProduct);
		return (totalUnits == null) ? 0 : totalUnits;
	}

	public CategoryModel getProductBarModel(Date dateFrom, Date dateTo, List<Product> filterProductList) {
		CategoryModel model = new SimpleCategoryModel();
		for(Client eachClient : clientRepository.findAll()) {
			// por cada cliente recorremos todos los productos de sus pedidos
			for(ProductTotal eachProductTotal : getFilteredProductTotalList(getProductTotalByClient(eachClient, dateFrom, dateTo), filterProductList)) {
				model.setValue(eachProductTotal.getProduct().getName(), eachClient.getName(), eachProductTotal.getTotalUnits());
			}
		}
		return model;
	}

	private List<ProductTotal> getProductTotalByClient(Client eachClient, Date dateFrom, Date dateTo) {
		Map<Product, Integer> productTotalMap = new HashMap<Product, Integer>();
		for(Order eachOrder : orderRepository.findByClientAndDateAfterAndDateBefore(eachClient, dateFrom, dateTo)) {
			for(OrderDetail eachOrderDetail : eachOrder.getDetails()) {
				Integer totalUnits = productTotalMap.get(eachOrderDetail.getProduct());
				productTotalMap.put(eachOrderDetail.getProduct(), (totalUnits == null) ? eachOrderDetail.getUnits() : totalUnits + eachOrderDetail.getUnits());
			}
		}
		List<ProductTotal> list = new ArrayList<ProductTotal>();
		for(Map.Entry<Product, Integer> entry : productTotalMap.entrySet()) {
			Product product = entry.getKey();
			Integer totalUnits = entry.getValue();
			ProductTotal productTotal = new ProductTotal(product, totalUnits);
			list.add(productTotal);
		}
		return list;
	}

	//	private Map<MaterialRequirement, Integer> getSuppliesTotalMap() {
	//		Map<MaterialRequirement, Integer> suppliesTotalMap = new HashMap<MaterialRequirement, Integer>();
	//		for(MaterialRequirement each : materialRequirementRepository.findAll()) {
	//			if(each.getType() == MaterialType.Supply) {
	//				Integer totalUnits = suppliesTotalMap.get(each);
	//				suppliesTotalMap.put(each, (totalUnits == null) ? each.getQuantity().intValue() : totalUnits + each.getQuantity().intValue());
	//			}
	//		}
	//		return suppliesTotalMap;
	//	}

	private Map<SupplyType, Integer> getSuppliesTotalMap(Date dateFrom, Date dateTo) {
		Map<SupplyType, Integer> suppliesTotalMap = new HashMap<SupplyType, Integer>();
		for(ProductTotal each : getProductTotalOrders(dateFrom, dateTo)) {
			for(ProductMaterial eachMaterial : each.getProduct().getMaterials()) {
				if(eachMaterial.getType() == MaterialType.Supply) {
					SupplyType supplyType = (SupplyType) eachMaterial.getItem();
					Integer currentUnits = eachMaterial.getQuantity().intValue() * each.getTotalUnits();
					Integer totalUnits = suppliesTotalMap.get(supplyType);
					suppliesTotalMap.put(supplyType, (totalUnits == null) ? currentUnits : totalUnits + currentUnits);
				}
			}
		}
		return suppliesTotalMap;
	}

	public PieModel getSuppliesPieModel(Date dateFrom, Date dateTo) {
		PieModel pieModel = new SimplePieModel();
		for(Map.Entry<SupplyType, Integer> entry : getSuppliesTotalMap(dateFrom, dateTo).entrySet()) {
			pieModel.setValue(entry.getKey().getDescription(), entry.getValue());
		}
		return pieModel;
	}

}
