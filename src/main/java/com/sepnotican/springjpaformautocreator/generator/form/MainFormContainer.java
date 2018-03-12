package com.sepnotican.springjpaformautocreator.generator.form;

import com.sepnotican.springjpaformautocreator.generator.annotations.GenerateUI;
import com.sepnotican.springjpaformautocreator.generator.form.generic.AbstractElementForm;
import com.sepnotican.springjpaformautocreator.generator.form.generic.AbstractListForm;
import com.vaadin.ui.*;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MainFormContainer extends VerticalLayout implements IFormHandler {

    private static final String DEFAULT_LIST_FORM_PREFIX = "DEF_LIST_";
    private static final String DEFAULT_ELEMENT_FORM_PREFIX = "DEF_ELEM_";
    private TabSheet tabSheet;
    private UIHandler uiHandler;
    private Map<String, TabSheet.Tab> openedForms = new HashMap<>();
    private Layout mainLayout;

    public MainFormContainer(UIHandler uiHandler, Layout mainLayout) {
        this.uiHandler = uiHandler;
        this.mainLayout = mainLayout;
    }

    public void init() {
        tabSheet = new TabSheet();
        tabSheet.setCloseHandler(new TabSheet.CloseHandler() {
            @Override
            public void onTabClose(TabSheet tabsheet,
                                   Component tabContent) {
                TabSheet.Tab tab = tabsheet.getTab(tabContent);
                Notification.show("Closing " + tab.getCaption());

                if (openedForms.containsValue(tab)) {
                    openedForms.entrySet().removeIf((entry) -> entry.getValue().equals(tab));
                }
                tabsheet.removeTab(tab);
            }
        });

        this.addComponent(tabSheet);
        mainLayout.addComponent(this);
    }

    public <T> void showAbstractListForm(Class<?> aClass, JpaRepository<T, Object> jpaRepository) {
        final String fromCacheName = DEFAULT_LIST_FORM_PREFIX + aClass.getCanonicalName();

        TabSheet.Tab tab;
        tab = openedForms.get(fromCacheName);
        if (tab == null) {
            AbstractListForm<T, JpaRepository> listForm = new AbstractListForm<>(this, aClass, jpaRepository);
            tab = tabSheet.addTab(listForm);
            tab.setCaption(aClass.getAnnotation(GenerateUI.class).listCaption());
            tab.setIcon(aClass.getAnnotation(GenerateUI.class).icon());
            tab.setVisible(true);
            tab.setClosable(true);
            openedForms.put(fromCacheName, tab);
        }

        tabSheet.setSelectedTab(tab);
        tabSheet.focus();

    }

    @Override
    public <T> void showAbstractElementForm(Class<?> aClass, JpaRepository<T, Object> jpaRepository, T entity) {
        final String fromCacheName = DEFAULT_ELEMENT_FORM_PREFIX + aClass.getCanonicalName() + entity.hashCode();

        TabSheet.Tab tab;
        tab = openedForms.get(fromCacheName);
        if (tab == null) {
            AbstractElementForm<T> elemForm = new AbstractElementForm<T>(jpaRepository);
            elemForm.init(entity);
            tab = tabSheet.addTab(elemForm);
            String caption = "undefined";
            for (Field field : entity.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    try {
                        caption = field.get(entity).toString();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        //todo log
                    }
                    break;
                }
            }
            tab.setCaption(aClass.getAnnotation(GenerateUI.class).entityCaption() + ":" + caption);
            tab.setIcon(aClass.getAnnotation(GenerateUI.class).icon());
            tab.setVisible(true);
            tab.setClosable(true);
            openedForms.put(fromCacheName, tab);
        }

        tabSheet.setSelectedTab(tab);
        tabSheet.focus();
    }

}
