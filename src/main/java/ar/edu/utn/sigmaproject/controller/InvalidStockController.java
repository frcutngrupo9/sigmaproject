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
