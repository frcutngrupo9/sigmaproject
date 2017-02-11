package ar.edu.utn.sigmaproject.controller;

import ar.edu.utn.sigmaproject.domain.StockMovementDetail;
import ar.edu.utn.sigmaproject.domain.Supply;
import ar.edu.utn.sigmaproject.domain.SupplyType;
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

public class StockMovementSupplyCreationController extends StockMovementCreationController<SupplyType> {

	@WireVariable
	private SupplyTypeRepository supplyTypeRepository;

	@Override
	protected boolean updateItemsStock(List<StockMovementDetail<SupplyType>> invalidDetails) {
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

	protected SearchableRepository<SupplyType, Long> getItemRepository() {
		return supplyTypeRepository;
	}

}
