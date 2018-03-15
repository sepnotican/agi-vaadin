package com.sepnotican.agi.generator.form;

import com.vaadin.ui.AbstractOrderedLayout;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IFormHandler {
    <T> void showAbstractListForm(Class<T> aClass, JpaRepository<T, Object> jpaRepository);

    <T> void showAbstractElementForm(JpaRepository<T, Object> jpaRepository
            , T entity, boolean isNewInstance) throws NoSuchFieldException, IllegalAccessException;

    <T> void refreshElementCaption(T entity, String formCachedName) throws NoSuchFieldException, IllegalAccessException;

    AbstractOrderedLayout getMainLayout();
}
