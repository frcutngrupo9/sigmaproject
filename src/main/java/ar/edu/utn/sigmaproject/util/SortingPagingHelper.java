package ar.edu.utn.sigmaproject.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
*
* @author gfzabarino
* @param <T> The domain class this helper will use to fill the listbox model
*/
public final class SortingPagingHelper<T> {
	
	private Sort.Order sortOrder;
    private final Listbox listbox;
    private final Grid grid;
    private final Paging pager;
    private final LinkedHashMap<String, Boolean> sortProperties;
    private final PageCachePagingEventListener pageCachePagingEventListener;
    SortingPagingHelperDelegate<T> delegate;

    public SortingPagingHelper(Listbox listbox, Paging pager, LinkedHashMap<String, Boolean> sortProperties, SortingPagingHelperDelegate<T> delegate) {
        this(listbox, pager, sortProperties, delegate, 0);
    }

    public SortingPagingHelper(Listbox listbox, Paging pager, LinkedHashMap<String, Boolean> sortProperties, SortingPagingHelperDelegate<T> delegate, Integer defaultSortIndex) {
        this(listbox, pager, sortProperties, delegate, defaultSortIndex, true);
    }

    public SortingPagingHelper(Listbox listbox, Paging pager, LinkedHashMap<String, Boolean> sortProperties, SortingPagingHelperDelegate<T> delegate, Integer defaultSortIndex, boolean ascending) {
        this.listbox = listbox;
        this.grid = null;
        this.pager = pager;
        this.sortProperties = sortProperties;
        this.delegate = delegate;
        pageCachePagingEventListener = new PageCachePagingEventListener(this);
        pager.addEventListener("onPaging", pageCachePagingEventListener);
        setup(defaultSortIndex, ascending);
    }

    public SortingPagingHelper(Grid grid, Paging pager, LinkedHashMap<String, Boolean> sortProperties, SortingPagingHelperDelegate<T> delegate, Integer defaultSortIndex, boolean ascending) {
        this.grid = grid;
        this.listbox = null;
        this.pager = pager;
        this.sortProperties = sortProperties;
        this.delegate = delegate;
        pageCachePagingEventListener = new PageCachePagingEventListener(this);
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

        private Page<T> cachedPage;
        private SortingPagingHelper<T> sortingPagingHelper;
        
        public PageCachePagingEventListener(SortingPagingHelper<T> sortingPagingHelper) {
			this.sortingPagingHelper = sortingPagingHelper;
		}

        @Override
        public void onEvent(PagingEvent event) throws Exception {
            int currentPage = event.getActivePage();
            if (cachedPage == null || currentPage != cachedPage.getNumber()) {
                PageRequest pageRequest;
                if (sortOrder != null) {
                    pageRequest = new PageRequest(currentPage, pager.getPageSize(), new Sort(sortOrder));
                } else {
                    pageRequest = new PageRequest(currentPage, pager.getPageSize());
                }
                cachedPage = delegate.getPageForPageRequest(sortingPagingHelper, pageRequest);
                pager.setTotalSize((int) cachedPage.getTotalElements());
                if (listbox != null) {
                    listbox.setModel(new ListModelList<T>(cachedPage.getContent()));
                } else {
                    grid.setModel(new ListModelList<T>(cachedPage.getContent()));
                }
            }
        }

        public void clearCache() {
            cachedPage = null;
        }

    }

    class DummyComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            return -1;
        }

    }

    public interface SortingPagingHelperDelegate<T> {
        public Page<T> getPageForPageRequest(SortingPagingHelper<T> sortingPagingHelper, PageRequest pageRequest);
    }

}