package com.sepnotican.agi.core.utils;

import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.shared.data.sort.SortDirection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaContext;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Slf4j
public class GenericDAO<T> {

    private Class<T> entityClass;
    private JpaContext jpaContext;

    public GenericDAO(Class<T> entityClass, JpaContext jpaContext) {
        this.entityClass = entityClass;
        this.jpaContext = jpaContext;
    }

    @SuppressWarnings("unchecked")
    public List<T> getEntitiesByCriteriaFilterSet(Set<CriteriaFilter> filterSet, List<QuerySortOrder> sortOrders, int offset, int limit) {
        EntityManager entityManager = jpaContext.getEntityManagerByManagedType(entityClass);
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

    public Long getCountByCriteriaFilterSet(Set<CriteriaFilter> filterSet) {
        EntityManager entityManager = jpaContext.getEntityManagerByManagedType(entityClass);
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery query = builder.createQuery();
        Root<T> root = query.from(entityClass);
        query.select(builder.count(root));
        buildPredicate(builder, root, query, filterSet);
        TypedQuery typedQuery = entityManager.createQuery(query);
        return (Long) typedQuery.getSingleResult();
    }

    private void buildLimit(TypedQuery typedQuery, int offset, int limit) {
        typedQuery.setFirstResult(offset);
        typedQuery.setMaxResults(limit);
    }

    private void buildSortOrders(CriteriaBuilder builder, Root<T> root, CriteriaQuery query, List<QuerySortOrder> sortOrders) {
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
            if (filter.getFieldValue() == null ||
                    (filter.getFieldValue() instanceof String && ((String) filter.getFieldValue()).isEmpty())) {
                continue;
            }
            if (filter.getCompareType() == CompareType.EQUALS) {
                predicates.add(builder.equal(root.get(filter.getFieldName()), filter.getFieldValue()));
            } else if (filter.getCompareType() == CompareType.NOT_EQUALS) {
                predicates.add((builder.notEqual(root.get(filter.getFieldName()), filter.getFieldValue())));
            } else if (filter.getCompareType() == CompareType.LIKE) {
                predicates.add((builder.like(root.get(filter.getFieldName()), "%" + filter.getFieldValue() + "%")));
            } else if (filter.getCompareType() == CompareType.STARTS_WITH) {
                predicates.add((builder.like(root.get(filter.getFieldName()), filter.getFieldValue() + "%")));
            }
        }
        query.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
    }
}
