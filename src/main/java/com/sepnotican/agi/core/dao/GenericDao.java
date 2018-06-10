package com.sepnotican.agi.core.dao;

import com.sepnotican.agi.core.annotations.Synonym;
import com.sepnotican.agi.core.form.IFormHandler;
import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.shared.data.sort.SortDirection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@Component
@Scope("prototype")
public class GenericDao<T> {

    public static volatile int q, w;
    @Autowired
    protected EntityManager entityManager;
    protected Class<T> entityClass;
    @Autowired
    IFormHandler formHandler;
    private Pattern numericPattern = Pattern.compile("^[0-9]+?$");

    public GenericDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<T> getEntitiesByCriteriaFilterSet(Set<CriteriaFilter> filterSet, List<QuerySortOrder> sortOrders, int offset, int limit) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery query = builder.createQuery();
        Root<T> root = query.from(entityClass);
        query.select(root);
        buildPredicate(builder, root, query, filterSet);
        buildSortOrders(builder, root, query, sortOrders);
        TypedQuery typedQuery = entityManager.createQuery(query);
        buildLimit(typedQuery, offset, limit);
        return (List<T>) typedQuery.getResultList();
    }

    @Transactional(readOnly = true)
    public Long getCountByCriteriaFilterSet(Set<CriteriaFilter> filterSet) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery query = builder.createQuery();
        Root<T> root = query.from(entityClass);
        query.select(builder.count(root));
        buildPredicate(builder, root, query, filterSet);
        TypedQuery typedQuery = entityManager.createQuery(query);
        return (Long) typedQuery.getSingleResult();
    }

    @Transactional
    public T save(T entity) {
        T mergedEntity = entityManager.merge(entity);
        entityManager.flush();
        entityManager.refresh(mergedEntity);
        return mergedEntity;
    }

    protected void buildLimit(TypedQuery typedQuery, int offset, int limit) {
        typedQuery.setFirstResult(offset);
        typedQuery.setMaxResults(limit);
    }

    protected void buildSortOrders(CriteriaBuilder builder, Root<T> root, CriteriaQuery query, List<QuerySortOrder> sortOrders) {
        List<Order> orderList = new LinkedList<>();
        for (QuerySortOrder vaadinOrder : sortOrders) {
            Path<Object> objectPath = root.get(vaadinOrder.getSorted());
            Order order;
            if (vaadinOrder.getDirection() == SortDirection.ASCENDING) order = builder.asc(objectPath);
            else order = builder.desc(objectPath);
            orderList.add(order);
        }
        query.orderBy(orderList);
    }

    protected void buildPredicate(CriteriaBuilder builder, Root<T> root, CriteriaQuery<T> query, Set<CriteriaFilter> filterSet) {
        if (filterSet == null) return;
        List<Predicate> predicates = new ArrayList<>();

        for (CriteriaFilter filter : filterSet) {
            try {
                if (filter.getFieldValue() == null ||
                        (filter.getFieldValue() instanceof String && ((String) filter.getFieldValue()).isEmpty()))
                    continue;

                if (filter.getCompareType() == CompareType.EQUALS) {
                    predicates.add(builder.equal(root.get(filter.getFieldName()), filter.getFieldValue()));
                } else if (filter.getCompareType() == CompareType.NOT_EQUALS) {
                    predicates.add((builder.notEqual(root.get(filter.getFieldName()), filter.getFieldValue())));
                } else if (filter.getCompareType() == CompareType.LIKE) {
                    if (filter.getFieldValue().getClass().equals(String.class)) {
                        predicates.add((builder.like(root.get(filter.getFieldName()), "%" + filter.getFieldValue() + "%")));
                    } else {
                        predicates.add((builder.like(root.get(filter.getFieldName()), filter.getFieldValue().toString())));
                    }
                } else if (filter.getCompareType() == CompareType.STARTS_WITH) {
                    predicates.add((builder.like(root.get(filter.getFieldName()), filter.getFieldValue() + "%")));
                }
            } catch (NumberFormatException nfe) {
                try {
                    String caption;
                    Field field = entityClass.getDeclaredField(filter.getFieldName());
                    if (field.isAnnotationPresent(Synonym.class)) {
                        caption = field.getAnnotation(Synonym.class).value();
                    } else {
                        caption = filter.getFieldName();
                    }
                    formHandler.handleFilterException(caption, nfe);
                } catch (NoSuchFieldException e) {
                    log.error(e.getMessage(), e);
                }
                break;
            }
        }

        query.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
    }

    @Transactional(readOnly = true)
    public void refresh(T entity) {
        entityManager.refresh(entityManager.merge(entity));
    }

    @Transactional
    public void delete(T entity) {
        entityManager.remove(entityManager.merge(entity));
    }
}
