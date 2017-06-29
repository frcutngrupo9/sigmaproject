package ar.edu.utn.sigmaproject.controller;

import ar.edu.utn.sigmaproject.domain.StockMovementType;

public class StockMovementWoodListController extends StockMovementListController {

	@Override
	protected String getCreationSrc() {
		return "/stock_movement_creation_wood.zul";
	}

	@Override
	protected StockMovementType getType() {
		return StockMovementType.Wood;
	}

}
