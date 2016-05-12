package ar.edu.utn.sigmaproject.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface SortingPagingHelperDelegate<T> {

	Page<T> getPage(PageRequest pageRequest);

}
