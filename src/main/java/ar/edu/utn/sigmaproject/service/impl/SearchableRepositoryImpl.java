package ar.edu.utn.sigmaproject.service.impl;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.TermContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.utn.sigmaproject.service.SearchableRepository;

@Transactional(readOnly = true)
public class SearchableRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>
		implements SearchableRepository<T, ID> {

	private final EntityManager entityManager;
	private Class<T> clazz;

	@SuppressWarnings("unchecked")
	public SearchableRepositoryImpl(JpaEntityInformation entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);

		// Keep the EntityManager around to used from the newly introduced
		// methods.
		this.entityManager = entityManager;
		this.clazz = entityInformation.getJavaType();
	}

	public Page<T> findAll(String queryString, Pageable pageable) {
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        try {
            // This will ensure that index for already inserted data is created.
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException ignored) {

        }
        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(this.clazz).get();
		List<String> fields = new ArrayList<>();
		if (this.clazz.isAnnotationPresent(Indexed.class)) {
			for (Field field : this.clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(org.hibernate.search.annotations.Field.class)) {
					fields.add(field.getName());
				}
			}
		}
		if (fields.isEmpty()) {
			throw new java.lang.RuntimeException(
					"The class " + this.clazz.getName() + " should have the @Indexed annotation present" +
							" and it should have at least one field with the @Field annotation");
		}
		org.apache.lucene.search.Query query = qb.keyword().onFields(fields.toArray(new String[0])).matching(queryString).createQuery();
        FullTextQuery jpaQuery = fullTextEntityManager.createFullTextQuery(query, this.clazz);

        jpaQuery.setFirstResult(pageable.getOffset());
        jpaQuery.setMaxResults(pageable.getPageSize());
        org.springframework.data.domain.Sort modelSort = pageable.getSort();
        if (modelSort != null) {
            Iterator<org.springframework.data.domain.Sort.Order> iterator = modelSort.iterator();
            List<SortField> sortFields = new ArrayList<SortField>();
            while (iterator.hasNext()) {
                org.springframework.data.domain.Sort.Order order = iterator.next();
                sortFields.add(new SortField(order.getProperty(), SortField.Type.STRING, !order.isAscending()));
            }
            SortField[] sortFieldsArray = sortFields.toArray(new SortField[0]);
            Sort sort = new Sort(sortFieldsArray);
            jpaQuery.setSort(sort);
        }

        long resultSize = jpaQuery.getResultSize();

        // execute search
        @SuppressWarnings("unchecked")
		List<T> results = jpaQuery.getResultList();
        return new PageImpl<T>(results, pageable, resultSize);
	}

}
