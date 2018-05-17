package com.sepnotican.agi.core.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Component;

@Component
public class GenericCriteriaFetcherFactory {

    @Autowired
    private JpaContext jpaContext;

    @SuppressWarnings("unchecked")
    public GenericCriteriaFetcher createCriteriaRepository(Class aClass) {
        return new GenericCriteriaFetcher(aClass, jpaContext);
    }

}
