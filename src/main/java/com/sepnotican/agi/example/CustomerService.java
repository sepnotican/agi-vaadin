package com.sepnotican.agi.example;

import com.sepnotican.agi.example.entity.Customer;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Service
public class CustomerService {
    @Autowired
    private JpaContext jpaContext;

    @Autowired
    private Logger logger;

    List<Customer> getCustomersByCriteria(String text) {
        EntityManager entityManagerByManagedType = jpaContext.getEntityManagerByManagedType(Customer.class);
        CriteriaBuilder criteriaBuilder = entityManagerByManagedType.getCriteriaBuilder();

        CriteriaQuery<Customer> query = criteriaBuilder.createQuery(Customer.class);
        Root<Customer> root = query.from(Customer.class);
        query.select(root);
        query.where(criteriaBuilder.like(root.get("name"), text));

        List<Customer> resultList = entityManagerByManagedType.createQuery(query).getResultList();
        for (Customer student : resultList) {
            logger.warn("id:" + student.getId() + ", age:" + student.getColor());
        }
        return null;
    }
}
