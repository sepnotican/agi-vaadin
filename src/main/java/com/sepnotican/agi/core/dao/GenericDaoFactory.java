package com.sepnotican.agi.core.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GenericDaoFactory {

    Map<Class, GenericDao> cache = new HashMap<>();

    @Autowired
    ApplicationContext context;

    public GenericDao getGenericDaoForClass(Class aClass) {
        GenericDao genericDao = cache.get(aClass);
        if (genericDao == null) {
            genericDao = context.getBean(GenericDao.class, aClass);
            cache.put(aClass, genericDao);
        }
        return genericDao;
    }

}
