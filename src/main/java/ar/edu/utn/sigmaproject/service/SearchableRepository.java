package ar.edu.utn.sigmaproject.service;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
*
* @author gfzabarino
*/
@NoRepositoryBean
public interface SearchableRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

	public Page<T> findAll(String queryString, Pageable pageable);
	
}
