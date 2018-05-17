package com.sepnotican.agi.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaContext;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Set;

@Slf4j
public class GenericCriteriaFetcher<T> {

    private Class<T> aClass;
    private JpaContext jpaContext;

    public GenericCriteriaFetcher(Class<T> aClass, JpaContext jpaContext) {
        this.aClass = aClass;
        this.jpaContext = jpaContext;
    }

    public List<T> getByCriteria(Set<CriteriaFilter> filterSet) {
        EntityManager entityManagerByManagedType = jpaContext.getEntityManagerByManagedType(aClass);
        CriteriaBuilder criteriaBuilder = entityManagerByManagedType.getCriteriaBuilder();

        CriteriaQuery query = criteriaBuilder.createQuery();
        Root<T> root = query.from(aClass);
        query.select(root);

        buildPredicate(criteriaBuilder, root, query, filterSet);

        List<T> resultList = entityManagerByManagedType.createQuery(query).getResultList();
        for (Object student : resultList) {
            log.warn(student.toString());
        }
        return resultList;
    }

    private void buildPredicate(CriteriaBuilder builder, Root<T> root, CriteriaQuery<T> query, Set<CriteriaFilter> filterSet) {

        for (CriteriaFilter filter : filterSet) {
            if (filter.getFieldValue() == null || filter.getFieldValue().isEmpty()) continue;

            if (filter.getCompateType() == CompareType.EQUALS) {
                query.where(builder.equal(root.get(filter.getFieldName()), filter.getFieldValue()));
            } else if (filter.getCompateType() == CompareType.NOT_EQUALS) {
                query.where(builder.notEqual(root.get(filter.getFieldName()), filter.getFieldValue()));
            } else if (filter.getCompateType() == CompareType.LIKE) {
                query.where(builder.like(root.get(filter.getFieldName()), "%" + filter.getFieldValue() + "%"));
            } else if (filter.getCompateType() == CompareType.STARTS_WITH) {
                query.where(builder.like(root.get(filter.getFieldName()), filter.getFieldValue() + "%"));
            }
        }


    }
}
