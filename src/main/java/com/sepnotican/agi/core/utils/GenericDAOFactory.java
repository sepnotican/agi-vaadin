package com.sepnotican.agi.core.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GenericDAOFactory {
    @Autowired
    private JpaContext jpaContext;
    @Getter
    private final Map<Class, GenericDAO> cache = new HashMap<>();

    @SuppressWarnings("unchecked")
    public GenericDAO getRepositoryForClass(Class aClass) {
        GenericDAO foundedRepository = cache.get(aClass);
        if (foundedRepository == null) {
            GenericDAO repository = new GenericDAO(aClass, jpaContext);
            cache.put(aClass, repository);
            return repository;
        } else return foundedRepository;
    }

}
