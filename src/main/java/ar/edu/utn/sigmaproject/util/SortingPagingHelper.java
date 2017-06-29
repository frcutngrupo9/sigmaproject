package ar.edu.utn.sigmaproject.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;
import org.zkoss.zul.event.PagingEvent;

import ar.edu.utn.sigmaproject.service.SearchableRepository;
import org.zkoss.zul.impl.MeshElement;

import java.io.Serializable;
import java.util.*;

/**
*
* @author gfzabarino
* @param <T> The domain class this helper will use to fill the listbox model
*/
public final class SortingPagingHelper<T> {

	private final JpaRepository<T, ? extends Serializable> repository;
	private final MeshElement meshElement;
	private final Button searchButton;
	private final Textbox searchTextbox;
	private final Paging pager;
	private final Map<Integer, String> sortProperties;
	private final PageCachePagingEventListener pageCachePagingEventListener;
	private final SortingPagingHelperDelegate<T> delegate;
	private Sort.Order sortOrder;

	public SortingPagingHelper(JpaRepository<T, ? extends Serializable> repository, MeshElement meshElement, Paging pager) {
		this(repository, meshElement, pager, new HashMap<Integer, String>());
	}

	public SortingPagingHelper(JpaRepository<T, ? extends Serializable> repository, MeshElement meshElement, Paging pager, Map<Integer, String> sortProperties) {
		this(repository, meshElement, null, null, pager, sortProperties, null);
	}

	public SortingPagingHelper(JpaRepository<T, ? extends Serializable> repository, MeshElement meshElement, Paging pager, Map<Integer, String> sortProperties, SortingPagingHelperDelegate<T> delegate) {
		this(repository, meshElement, null, null, pager, sortProperties, delegate);
	}

	public SortingPagingHelper(JpaRepository<T, ? extends Serializable> repository, MeshElement meshElement, Button searchButton, Textbox searchTextbox, Paging pager) {
		this(repository, meshElement, searchButton, searchTextbox, pager, new HashMap<Integer, String>());
	}

	public SortingPagingHelper(JpaRepository<T, ? extends Serializable> repository, MeshElement meshElement, Button searchButton, Textbox searchTextbox, Paging pager, Map<Integer, String> sortProperties) {
		this(repository, meshElement, searchButton, searchTextbox, pager, sortProperties, null);
	}

	public SortingPagingHelper(JpaRepository<T, ? extends Serializable> repository, MeshElement meshElement, Button searchButton, Textbox searchTextbox, Paging pager, Map<Integer, String> sortProperties, SortingPagingHelperDelegate<T> delegate) {
		this(repository, meshElement, searchButton, searchTextbox, pager, sortProperties, delegate, 0);
	}

	public SortingPagingHelper(JpaRepository<T, ? extends Serializable> repository, MeshElement meshElement, Button searchButton, Textbox searchTextbox, Paging pager, Map<Integer, String> sortProperties, SortingPagingHelperDelegate<T> delegate, Integer defaultSortIndex) {
		this(repository, meshElement, searchButton, searchTextbox, pager, sortProperties, delegate, defaultSortIndex, true);
	}

	public SortingPagingHelper(JpaRepository<T, ? extends Serializable> repository, MeshElement meshElement, Button searchButton, Textbox searchTextbox, Paging pager, Map<Integer, String> sortProperties, SortingPagingHelperDelegate<T> delegate, Integer defaultSortIndex, boolean ascending) {
		if (!(meshElement instanceof Listbox) && !(meshElement instanceof Grid)) {
			this.throwRuntimeExceptionBecauseOfIncompatibleMeshElement();
		}
		this.repository = repository;
		this.meshElement = meshElement;
		this.searchButton = searchButton;
		this.searchTextbox = searchTextbox;
		this.pager = pager;
		this.sortProperties = sortProperties;
		this.delegate = delegate;
		pageCachePagingEventListener = new PageCachePagingEventListener();
		pager.addEventListener("onPaging", pageCachePagingEventListener);
		setup(defaultSortIndex, ascending);
	}

	private void setup(Integer defaultSortIndex, boolean ascending) {
		Component header = this.getMeshElementHeader();
		if (sortProperties != null) {
			Integer index = 0;
			for (Component component : header.getChildren()) {
				if (sortProperties.get(index++) != null) {
					component.addEventListener(Events.ON_SORT, new SortEventListener());
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
		if (this.searchButton != null) {
			this.searchButton.addEventListener(Events.ON_CLICK, new SearchButtonClickEventListener());
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
		if (sortOrder != null) {
			Component header = getMeshElementHeader();
			for (Component component : header.getChildren()) {
				if (component instanceof Listheader) {
					Listheader otherListheader = (Listheader)component;
					// clear sort icons on other columns
					otherListheader.setSortDirection("natural");
				} else {
					Column otherColumn = (Column) component;
					// clear sort icons on other columns
					otherColumn.setSortDirection("natural");
				}
			}
			sortOrder = null;
		}
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
		if (this.delegate != null) {
			page = this.delegate.getPage(pageRequest);
		} else if (searchTextbox != null && !searchTextbox.getText().isEmpty() && repository instanceof SearchableRepository) {
			SearchableRepository<T, ? extends Serializable> searchableRepository = (SearchableRepository<T, ? extends Serializable>) repository;
			page = searchableRepository.findAll(searchTextbox.getText(), pageRequest);
		} else {
			page = repository.findAll(pageRequest);
		}
		return page;
	}

	private Component getMeshElementHeader() {
		Component header = null;
		if (this.meshElement instanceof Listbox) {
			header = ((Listbox) this.meshElement).getListhead();
		} else if (this.meshElement instanceof Grid) {
			header = ((Grid) this.meshElement).getColumns();
		} else {
			this.throwRuntimeExceptionBecauseOfIncompatibleMeshElement();
		}
		return header;
	}

	private void setModel(ListModel<T> model) {
		if (this.meshElement instanceof Listbox) {
			((Listbox) this.meshElement).setModel(model);
		} else if (this.meshElement instanceof Grid) {
			((Grid) this.meshElement).setModel(model);
		} else {
			this.throwRuntimeExceptionBecauseOfIncompatibleMeshElement();
		}
	}

	private void throwRuntimeExceptionBecauseOfIncompatibleMeshElement() {
		throw new RuntimeException("SortingPagingHelper only accepts Listbox or Grid MeshElement subclasses");
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
			int columnIndex = -1;
			if (listheader != null) {
				columnIndex = listheader.getColumnIndex();
			} else {
				for (Component head : ((Grid) meshElement).getHeads()) {
					if (head instanceof Columns) {
						columnIndex = head.getChildren().indexOf(column);
						break;
					}
				}
			}
			if (columnIndex == -1) {
				throw new RuntimeException("SortingPagingHelper runtime error: Cannot find column index for column " + column);
			}
			sortOrder = new Sort.Order(event.isAscending() ? Sort.Direction.ASC : Sort.Direction.DESC, sortProperties.get(columnIndex)).ignoreCase();
			Component header = getMeshElementHeader();
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
			
			if (searchTextbox != null) {
				searchTextbox.setText("");				
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
				setModel(pageActiveModel);
			}
		}

		public void clearCache() {
			pageActiveModel = null;
		}

	}

	class SearchButtonClickEventListener implements EventListener<Event> {

		@Override
		public void onEvent(Event event) throws Exception {
			if (Objects.equals(event.getName(), Events.ON_CLICK)) {
				resetUnsorted();
			}
		}
	}

	public class PageActiveModel extends AbstractListModel<T> {

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
			return getExecutionPage().getNumberOfElements();
		}

	}

	class DummyComparator implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {
			return -1;
		}

	}

}
