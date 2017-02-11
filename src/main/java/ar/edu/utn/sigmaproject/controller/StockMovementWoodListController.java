package ar.edu.utn.sigmaproject.controller;

import ar.edu.utn.sigmaproject.domain.Wood;

public class StockMovementWoodListController extends StockMovementListController<Wood> {

	protected String getCreationSrc() {
		return "/stock_movement_creation_wood.zul";
	}

}
