package com.sepnotican.agi.core.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GenericRepositoryFactory {
    @Autowired
    private JpaContext jpaContext;
    @Getter
    private final Map<Class, GenericRepository> cache = new HashMap<>();

    @SuppressWarnings("unchecked")
    public GenericRepository getRepositoryForClass(Class aClass) {
        GenericRepository foundedRepository = cache.get(aClass);
        if (foundedRepository == null) {
            GenericRepository repository = new GenericRepository(aClass, jpaContext);
            cache.put(aClass, repository);
            return repository;
        } else return foundedRepository;
    }

}
