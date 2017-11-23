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

package ar.edu.utn.sigmaproject.service.impl;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.search.annotations.ClassBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.*;
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
        List<QueryField> fields = new ArrayList<>();
		recursiveAddIndexedFields(this.clazz, new ArrayList<String>(), fields);
		if (fields.isEmpty()) {
			throw new java.lang.RuntimeException(
					"The class " + this.clazz.getName() + " should have the @Indexed annotation present" +
							" and it should have at least one field with the @Field annotation");
		}
		EntityContext entityContext = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(this.clazz);
		// we use an analyzer with lowercase / edge ngram filters at index time, but at search time we just need a
		// standard analyzer, so we override the analyzer to use when searching
		for (QueryField field : fields) {
			entityContext = entityContext.overridesForField(field.name, "standard");
		}
		QueryBuilder qb = entityContext.get();
		TermMatchingContext matchingContext = qb.keyword().onField(fields.get(0).name);
		for (QueryField field : fields) {
			if (field.name.equals(fields.get(0).name)) {
				continue;
			}
			matchingContext = matchingContext.andField(field.name);
			if (field.ignoreFieldBridge) {
				matchingContext = matchingContext.ignoreFieldBridge();
			}
		}
		org.apache.lucene.search.Query query = matchingContext.matching(queryString).createQuery();
        FullTextQuery jpaQuery = fullTextEntityManager.createFullTextQuery(query, this.clazz);

        jpaQuery.setFirstResult(pageable.getOffset());
        jpaQuery.setMaxResults(pageable.getPageSize());
		// si alguna vez implementamos busqueda textual y ordenado, habria que usar algo asi
//        org.springframework.data.domain.Sort modelSort = pageable.getSort();
//        if (modelSort != null) {
//            Iterator<org.springframework.data.domain.Sort.Order> iterator = modelSort.iterator();
//            List<SortField> sortFields = new ArrayList<SortField>();
//            while (iterator.hasNext()) {
//                org.springframework.data.domain.Sort.Order order = iterator.next();
//                sortFields.add(new SortField(order.getProperty(), SortField.Type.STRING, !order.isAscending()));
//            }
//            SortField[] sortFieldsArray = sortFields.toArray(new SortField[0]);
//            Sort sort = new Sort(sortFieldsArray);
//            jpaQuery.setSort(sort);
//        }

        long resultSize = jpaQuery.getResultSize();

        // execute search
        @SuppressWarnings("unchecked")
		List<T> results = jpaQuery.getResultList();
        return new PageImpl<>(results, pageable, resultSize);
	}

	private void recursiveAddIndexedFields(Class clazz, List<String> fieldStack, List<QueryField> fields) {
		if (clazz.isAnnotationPresent(Indexed.class)) {
			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(org.hibernate.search.annotations.Field.class)) {
					String fieldName = "";
					for (String parentField : fieldStack) {
						fieldName += parentField + ".";
					}
					fieldName += field.getName();
					QueryField queryField = new QueryField();
					queryField.name = fieldName;
					fields.add(queryField);
				} else if (field.isAnnotationPresent(org.hibernate.search.annotations.IndexedEmbedded.class)) {
					fieldStack.add(field.getName());
					recursiveAddIndexedFields(field.getType(), fieldStack, fields);
					fieldStack.remove(fieldStack.size() - 1);
				}
			}
		}
		if (clazz.isAnnotationPresent(ClassBridge.class)) {
			Annotation annotation = clazz.getAnnotation(ClassBridge.class);
			String fieldName = "";
			for (String parentField : fieldStack) {
				fieldName += parentField + ".";
			}
			//fieldName = fieldName.substring(0, fieldName.length() - 1);
			fieldName += ((ClassBridge)annotation).name();
			QueryField queryField = new QueryField();
			queryField.name = fieldName;
			queryField.ignoreFieldBridge = true;
			fields.add(queryField);
		}
	}

	class QueryField {

		String name;
		boolean ignoreFieldBridge;

	}

}
