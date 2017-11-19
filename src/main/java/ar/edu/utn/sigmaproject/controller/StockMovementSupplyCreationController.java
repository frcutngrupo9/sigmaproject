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

import ar.edu.utn.sigmaproject.domain.*;
import ar.edu.utn.sigmaproject.service.SearchableRepository;
import ar.edu.utn.sigmaproject.service.SupplyTypeRepository;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Include;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StockMovementSupplyCreationController extends StockMovementCreationController {

	@WireVariable
	private SupplyTypeRepository supplyTypeRepository;

	@Override
	protected boolean updateItemsStock(List<StockMovementDetail> invalidDetails) {
		Set<SupplyType> supplyTypesToSave = new HashSet<>();
		for (StockMovementDetail stockMovementDetail : currentStockMovement.getDetails()) {
			SupplyType supplyType = (SupplyType) stockMovementDetail.getItem();
			BigDecimal stock = supplyType.getStock();
			stock = stock.add(stockMovementDetail.getQuantity().multiply(BigDecimal.valueOf(currentStockMovement.getSign())));
			if (stock.compareTo(BigDecimal.ZERO) >= 0) {
				supplyType.setStock(stock);
				supplyTypesToSave.add(supplyType);
			} else {
				invalidDetails.add(stockMovementDetail);
			}
		}
		if (invalidDetails.size() == 0) {
			supplyTypeRepository.save(supplyTypesToSave);
			return true;
		} else {
			return false;
		}
	}

	@Override
	@Listen("onClick = #included #returnButton")
	public void doReturn() {
		Include include = (Include) Selectors.iterable(this.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/stock_movement_list_supply.zul");
	}

	@Override
	protected SearchableRepository<? extends Item, Long> getItemRepository() {
		return supplyTypeRepository;
	}

	@Override
	protected StockMovementType getStockMovementType() {
		return StockMovementType.Supply;
	}

}
