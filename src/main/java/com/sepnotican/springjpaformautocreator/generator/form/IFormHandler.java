package com.sepnotican.springjpaformautocreator.generator.form;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IFormHandler {
    <T> void showAbstractListForm(Class<?> aClass, JpaRepository<T, Object> jpaRepository);

    <T> void showAbstractElementForm(Class<?> aClass, JpaRepository<T, Object> jpaRepository, T entity);

}
