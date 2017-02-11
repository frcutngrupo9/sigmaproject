package ar.edu.utn.sigmaproject.controller;

import ar.edu.utn.sigmaproject.domain.Item;
import ar.edu.utn.sigmaproject.domain.StockMovement;
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
public abstract class StockMovementListController<T extends Item> extends SelectorComposer<Component> implements SortingPagingHelperDelegate<StockMovement<T>> {

	@Wire("#included #stockMovementGrid")
	Grid stockMovementGrid;

	@Wire("#included #pager")
	Paging pager;

	@WireVariable
	private StockMovementRepository<T> stockMovementRepository;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		Map<Integer, String> sortProperties = new HashMap<>();
		sortProperties.put(0, "date");
		new SortingPagingHelper<>(stockMovementRepository, stockMovementGrid, pager, sortProperties, this);
	}

	@Override
	public Page<StockMovement<T>> getPage(PageRequest pageRequest) {
		return stockMovementRepository.findAll(pageRequest);
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

	protected abstract String getCreationSrc();
}
