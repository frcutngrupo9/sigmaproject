package ar.edu.utn.sigmaproject.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SortEvent;
import org.zkoss.zul.*;
import org.zkoss.zul.event.PagingEvent;

import ar.edu.utn.sigmaproject.service.SearchableRepository;

import java.io.Serializable;
import java.util.*;

/**
*
* @author gfzabarino
* @param <T> The domain class this helper will use to fill the listbox model
*/
public final class SortingPagingHelper<T> {

	private final JpaRepository<T, ? extends Serializable> repository;
	private final Listbox listbox;
	private final Textbox searchTextbox;
	private final Grid grid;
	private final Paging pager;
	private final LinkedHashMap<String, Boolean> sortProperties;
	private final PageCachePagingEventListener pageCachePagingEventListener;
	private Sort.Order sortOrder;

	public SortingPagingHelper(JpaRepository<T, ? extends Serializable> repository, Listbox listbox, Textbox searchTextbox, Paging pager, LinkedHashMap<String, Boolean> sortProperties) {
		this(repository, listbox, searchTextbox, pager, sortProperties, 0);
	}

	public SortingPagingHelper(JpaRepository<T, ? extends Serializable> repository, Listbox listbox, Textbox searchTextbox, Paging pager, LinkedHashMap<String, Boolean> sortProperties, Integer defaultSortIndex) {
		this(repository, listbox, searchTextbox, pager, sortProperties, defaultSortIndex, true);
	}

	public SortingPagingHelper(JpaRepository<T, ? extends Serializable> repository, Listbox listbox, Textbox searchTextbox, Paging pager, LinkedHashMap<String, Boolean> sortProperties, Integer defaultSortIndex, boolean ascending) {
		this.repository = repository;
		this.listbox = listbox;
		this.grid = null;
		this.searchTextbox = searchTextbox;
		this.pager = pager;
		this.sortProperties = sortProperties;
		pageCachePagingEventListener = new PageCachePagingEventListener();
		pager.addEventListener("onPaging", pageCachePagingEventListener);
		setup(defaultSortIndex, ascending);
	}

	public SortingPagingHelper(JpaRepository<T, ? extends Serializable> repository, Grid grid, Textbox searchTextbox, Paging pager, LinkedHashMap<String, Boolean> sortProperties, Integer defaultSortIndex, boolean ascending) {
		this.repository = repository;
		this.grid = grid;
		this.listbox = null;
		this.searchTextbox = searchTextbox;
		this.pager = pager;
		this.sortProperties = sortProperties;
		pageCachePagingEventListener = new PageCachePagingEventListener();
		pager.addEventListener("onPaging", pageCachePagingEventListener);
		setup(defaultSortIndex, ascending);
	}

	private void setup(Integer defaultSortIndex, boolean ascending) {
		Component header = this.grid != null ? this.grid.getColumns() : this.listbox.getListhead();
		if (sortProperties != null) {
			Iterator<Boolean> valueIterator = sortProperties.values().iterator();
			for (Component component : header.getChildren()) {
				if (!valueIterator.hasNext()) {
					break;
				}
				if (valueIterator.next()) {
					component.addEventListener("onSort", new SortEventListener());
					if (component instanceof Listheader) {
						Listheader listHeader = (Listheader)component;
						listHeader.setSortAscending(new DummyComparator());
						listHeader.setSortDescending(new DummyComparator());
					} else {
						Column column = (Column)component;
						column.setSortAscending(new DummyComparator());
						column.setSortDescending(new DummyComparator());
					}
				}
			}
			Events.postEvent(new SortEvent("onSort", header.getChildren().get(defaultSortIndex), ascending));
		} else {
			Events.postEvent(new PagingEvent("onPaging", pager, pager.getActivePage()));
		}
	}

	public void reloadCurrentPage() {
		pageCachePagingEventListener.clearCache();
		Events.postEvent(new PagingEvent("onPaging", pager, pager.getActivePage()));
	}

	public void reset() {
		pager.setActivePage(0);
		reloadCurrentPage();
	}

	public void resetUnsorted() {
		sortOrder = null;
		reset();
	}

	private PageRequest pageRequestForPageNumber(int pageNumber) {
		PageRequest pageRequest;
		if (sortOrder != null) {
			pageRequest = new PageRequest(pageNumber, pager.getPageSize(), new Sort(sortOrder));
		} else {
			pageRequest = new PageRequest(pageNumber, pager.getPageSize());
		}
		return pageRequest;
	}

	private Page<T> getPage(PageRequest pageRequest) {
		Page<T> page;
		if (searchTextbox != null && !searchTextbox.getText().isEmpty() && repository instanceof SearchableRepository) {
			SearchableRepository<T, ? extends Serializable> searchableRepository = (SearchableRepository<T, ? extends Serializable>) repository;
			page = searchableRepository.findAll(searchTextbox.getText(), pageRequest);
		} else {
			page = repository.findAll(pageRequest);
		}
		return page;
	}

	class SortEventListener implements EventListener<SortEvent> {

		@Override
		public void onEvent(SortEvent event) throws Exception {
			Listheader listheader = null;
			Column column = null;
			if (event.getTarget() instanceof Listheader) {
				listheader = (Listheader)event.getTarget();
				// set current sort order on the client
				listheader.setSortDirection(event.isAscending() ? "ascending" : "descending");
			} else {
				column = (Column)event.getTarget();
				// set current sort order on the client
				column.setSortDirection(event.isAscending() ? "ascending" : "descending");
			}
			String[] sortKeys = sortProperties.keySet().toArray(new String[0]);
			int columnIndex = listheader != null ? listheader.getColumnIndex() : grid.getHeads().iterator().next().getChildren().indexOf(column);
			sortOrder = new Sort.Order(event.isAscending() ? Sort.Direction.ASC : Sort.Direction.DESC, sortKeys[columnIndex]).ignoreCase();
			Component header = grid != null ? grid.getColumns() : listbox.getListhead();
			for (Component component : header.getChildren()) {
				if (component instanceof Listheader) {
					Listheader otherListheader = (Listheader)component;
					if (otherListheader != event.getTarget()) {
						// clear sort icons on other columns
						otherListheader.setSortDirection("natural");
					}
				} else {
					Column otherColumn = (Column)component;
					if (otherColumn != event.getTarget()) {
						// clear sort icons on other columns
						otherColumn.setSortDirection("natural");
					}
				}
			}
			reloadCurrentPage();

			// stop propagation of the event so DummyComparator is never used
			event.stopPropagation();
		}

	}

	class PageCachePagingEventListener implements EventListener<PagingEvent> {

		private PageActiveModel pageActiveModel;

		@Override
		public void onEvent(PagingEvent event) throws Exception {
			int currentPage = event.getActivePage();
			if (pageActiveModel == null || currentPage != pageActiveModel.pageRequest.getPageNumber()) {
				pageActiveModel = new PageActiveModel(pageRequestForPageNumber(currentPage));
				pager.setTotalSize((int)pageActiveModel.getExecutionPage().getTotalElements());
				if (listbox != null) {
					listbox.setModel(pageActiveModel);
				} else {
					grid.setModel(pageActiveModel);
				}
			}
		}

		public void clearCache() {
			pageActiveModel = null;
		}

	}

	class PageActiveModel extends AbstractListModel<T> {

		private final String CACHE_KEY = System.identityHashCode(this) + "_cache";
		private PageRequest pageRequest;

		public PageActiveModel(PageRequest pageRequest) {
			super();
			this.pageRequest = pageRequest;
		}

		@Override
		public T getElementAt(int index) {
			T targetElement = getExecutionPage().getContent().get(index);
			if (targetElement == null) {
				//if we cannot find the target object from database, there is inconsistency in the database
				throw new RuntimeException("Element at index " + index + " cannot be found in the database.");
			} else {
				return targetElement;
			}
		}

		private Page<T> getExecutionPage() {
			Execution execution = Executions.getCurrent();
			//we only reuse this cache in one execution to avoid accessing detached objects.
			//our filter opens a session during a HTTP request
			Page<T> cache = (Page<T>) execution.getAttribute(CACHE_KEY);
			if (cache == null) {
				cache = getPage(pageRequest);
				execution.setAttribute(CACHE_KEY, cache);
			}
			return cache;
		}

		@Override
		public int getSize() {
			return getExecutionPage().getSize();
		}

	}

	class DummyComparator implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {
			return -1;
		}

	}

}
