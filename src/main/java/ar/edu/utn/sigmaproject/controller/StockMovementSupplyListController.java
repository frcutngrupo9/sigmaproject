package ar.edu.utn.sigmaproject.controller;

import ar.edu.utn.sigmaproject.domain.StockMovementType;

public class StockMovementSupplyListController extends StockMovementListController {

	@Override
	protected String getCreationSrc() {
		return "/stock_movement_creation_supply.zul";
	}

	@Override
	protected StockMovementType getType() {
		return StockMovementType.Supply;
	}

}
