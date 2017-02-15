package ar.edu.utn.sigmaproject.controller;

import java.math.BigDecimal;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.StockMovementDetail;
import ar.edu.utn.sigmaproject.domain.SupplyType;
import ar.edu.utn.sigmaproject.domain.Wood;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class InvalidStockController extends SelectorComposer<Component> {

	@Wire
	private Window missingStockWin;

	@Listen("onClick = #closeButton")
	public void close() {
		missingStockWin.detach();
	}

	public BigDecimal getDifference(StockMovementDetail detail) {
		BigDecimal currentStock = null;
		if (detail.getItem() instanceof Product) {
			Product product = (Product)detail.getItem();
			currentStock = BigDecimal.valueOf(product.getStock());
		} else if (detail.getItem() instanceof SupplyType) {
			SupplyType supplyType = (SupplyType)detail.getItem();
			currentStock = supplyType.getStock();
		} else if (detail.getItem() instanceof Wood) {
			Wood wood = (Wood)detail.getItem();
			currentStock = wood.getStock();
		}
		if (currentStock != null) {
			return detail.getQuantity().subtract(currentStock);
		} else {
			return BigDecimal.ZERO;
		}
	}

}
