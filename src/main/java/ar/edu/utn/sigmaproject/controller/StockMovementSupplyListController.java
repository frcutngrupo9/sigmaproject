package ar.edu.utn.sigmaproject.controller;

import ar.edu.utn.sigmaproject.domain.SupplyType;

public class StockMovementSupplyListController extends StockMovementListController<SupplyType> {

	protected String getCreationSrc() {
		return "/stock_movement_creation_supply.zul";
	}

}
