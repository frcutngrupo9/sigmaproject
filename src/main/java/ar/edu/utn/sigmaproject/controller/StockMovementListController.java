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

import ar.edu.utn.sigmaproject.domain.StockMovement;
import ar.edu.utn.sigmaproject.domain.StockMovementType;
import ar.edu.utn.sigmaproject.service.StockMovementRepository;
import ar.edu.utn.sigmaproject.util.SortingPagingHelper;
import ar.edu.utn.sigmaproject.util.SortingPagingHelperDelegate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.Paging;

import java.util.HashMap;
import java.util.Map;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public abstract class StockMovementListController extends SelectorComposer<Component> implements SortingPagingHelperDelegate<StockMovement> {

	@Wire("#included #stockMovementGrid")
	Grid stockMovementGrid;

	@Wire("#included #pager")
	Paging pager;

	@WireVariable
	private StockMovementRepository stockMovementRepository;

	protected abstract String getCreationSrc();
	protected abstract StockMovementType getType();

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		Map<Integer, String> sortProperties = new HashMap<>();
		sortProperties.put(0, "date");
		new SortingPagingHelper<>(stockMovementRepository, stockMovementGrid, pager, sortProperties, this);
	}

	@Override
	public Page<StockMovement> getPage(PageRequest pageRequest) {
		return stockMovementRepository.findAllByType(getType(), pageRequest);
	}

	@Listen("onClick = #included #stockMovementButton")
	public void onNewStockMovementClick() {
		Executions.getCurrent().setAttribute("selected_stock_movement", null);
		Include include = (Include) Selectors.iterable(stockMovementGrid.getPage(), "#mainInclude").iterator().next();
		include.setSrc(getCreationSrc());
	}

	@Listen("onSelectStoveMovement = #included #stockMovementGrid")
	public void onSelectStoveMovement(ForwardEvent evt) {
		StockMovement stockMovement = (StockMovement) evt.getData();
		Executions.getCurrent().setAttribute("selected_stock_movement", stockMovement);
		Include include = (Include) Selectors.iterable(stockMovementGrid.getPage(), "#mainInclude").iterator().next();
		include.setSrc(getCreationSrc());
	}

}
