package ar.edu.utn.sigmaproject.controller;

import ar.edu.utn.sigmaproject.domain.*;
import ar.edu.utn.sigmaproject.service.SearchableRepository;
import ar.edu.utn.sigmaproject.service.SupplyTypeRepository;
import ar.edu.utn.sigmaproject.service.WoodRepository;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Include;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StockMovementWoodCreationController extends StockMovementCreationController {

	@WireVariable
	private WoodRepository woodRepository;

	@Override
	protected boolean updateItemsStock(List<StockMovementDetail> invalidDetails) {
		Set<Wood> woodsToSave = new HashSet<>();
		for (StockMovementDetail stockMovementDetail : currentStockMovement.getDetails()) {
			Wood wood = (Wood) stockMovementDetail.getItem();
			BigDecimal stock = wood.getStock();
			stock = stock.add(stockMovementDetail.getQuantity().multiply(BigDecimal.valueOf(currentStockMovement.getSign())));
			if (stock.compareTo(BigDecimal.ZERO) >= 0) {
				wood.setStock(stock);
				woodsToSave.add(wood);
			} else {
				invalidDetails.add(stockMovementDetail);
			}
		}
		if (invalidDetails.size() == 0) {
			woodRepository.save(woodsToSave);
			return true;
		} else {
			return false;
		}
	}

	@Override
	@Listen("onClick = #included #returnButton")
	public void doReturn() {
		Include include = (Include) Selectors.iterable(this.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/stock_movement_list_wood.zul");
	}

	@Override
	protected SearchableRepository<? extends Item, Long> getItemRepository() {
		return woodRepository;
	}

	@Override
	protected StockMovementType getStockMovementType() {
		return StockMovementType.Wood;
	}

}
