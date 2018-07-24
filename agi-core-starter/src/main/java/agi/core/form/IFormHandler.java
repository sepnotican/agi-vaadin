package agi.core.form;

import com.vaadin.ui.AbstractOrderedLayout;

public interface IFormHandler {
    <T> void showAbstractListForm(Class<T> aClass);

    <T> void showAbstractElementForm(T entity, boolean isNewInstance) throws NoSuchFieldException, IllegalAccessException;

    <T> void refreshElementCaption(T entity, String formCachedName) throws NoSuchFieldException, IllegalAccessException;

    AbstractOrderedLayout getMainLayout();

    void handleFilterException(String fieldName, Exception ex);
}
