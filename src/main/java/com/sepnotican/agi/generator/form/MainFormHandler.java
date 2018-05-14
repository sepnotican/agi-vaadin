package com.sepnotican.agi.generator.form;

import com.sepnotican.agi.generator.annotations.AgiUI;
import com.sepnotican.agi.generator.form.generic.AbstractElementForm;
import com.sepnotican.agi.generator.form.generic.AbstractListForm;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.PostConstruct;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@org.springframework.stereotype.Component
public class MainFormHandler extends VerticalLayout implements IFormHandler {

    private static final String DEFAULT_LIST_FORM_PREFIX = "DEF_LIST_";
    private static final String DEFAULT_ELEMENT_FORM_PREFIX = "DEF_ELEM_";
    private TabSheet tabSheet;
    private Map<String, TabSheet.Tab> openedForms = new HashMap<>();
    private AbstractOrderedLayout mainLayout;

    @Autowired
    ApplicationContext context;

    @Autowired
    private Logger logger;

    public MainFormHandler() {
        this.mainLayout = new VerticalLayout();
    }

    @Override
    public AbstractOrderedLayout getMainLayout() {
        return mainLayout;
    }

    @PostConstruct
    public void init() {
        tabSheet = new TabSheet();
        tabSheet.setCloseHandler((TabSheet.CloseHandler) (tabsheet, tabContent) -> {
            TabSheet.Tab tab = tabsheet.getTab(tabContent);
            Notification.show("Closing " + tab.getCaption());

            if (openedForms.containsValue(tab)) {
                openedForms.entrySet().removeIf((entry) -> entry.getValue().equals(tab));
            }
            tabsheet.removeTab(tab);
        });

        this.addComponent(tabSheet);
        mainLayout.addComponent(this);
    }

    public <T> void showAbstractListForm(Class<T> aClass, JpaRepository<T, Object> jpaRepository) {
        final String formCacheName = DEFAULT_LIST_FORM_PREFIX + aClass.getCanonicalName();

        TabSheet.Tab tab;
        tab = openedForms.get(formCacheName);
        if (tab == null) {
            AbstractListForm<T, JpaRepository<T, Object>> listForm = context.getBean(AbstractListForm.class, this, aClass, jpaRepository);
            tab = tabSheet.addTab(listForm);
            tab.setCaption(aClass.getAnnotation(AgiUI.class).listCaption());
            tab.setIcon(aClass.getAnnotation(AgiUI.class).icon());
            tab.setVisible(true);
            tab.setClosable(true);
            openedForms.put(formCacheName, tab);
        }

        tabSheet.setSelectedTab(tab);
        tabSheet.focus();

    }

    @Override
    public <T> void showAbstractElementForm(JpaRepository<T, Object> jpaRepository
            , T entity, boolean isNewInstance) throws NoSuchFieldException, IllegalAccessException {

        AgiUI agiUI = entity.getClass().getAnnotation(AgiUI.class);
        if (agiUI == null) throw new RuntimeException("Expected annotation is not present");
        final String formCacheName = generateElementCacheName(agiUI, entity);

        TabSheet.Tab tab;
        tab = openedForms.get(formCacheName);
        if (tab == null) {
            AbstractElementForm<T> elemForm = context.getBean(AbstractElementForm.class, this, jpaRepository);
            elemForm.init(entity, isNewInstance, formCacheName);
            tab = tabSheet.addTab(elemForm);

            String caption = generateElementCaption(entity, isNewInstance);

            tab.setCaption(caption);
            tab.setIcon(agiUI.icon());
            tab.setVisible(true);
            tab.setClosable(true);
            openedForms.put(formCacheName, tab);
        }

        tabSheet.setSelectedTab(tab);
        tabSheet.focus();
    }

    protected <T> String generateElementCaption(T entity, boolean isNewInstance) {
        if (!entity.getClass().isAnnotationPresent(AgiUI.class))
            throw new RuntimeException("Unacceptable class, annotation AgiUI is necessary");

        String caption = entity.getClass().getAnnotation(AgiUI.class).entityCaption() + ':';
        if (isNewInstance) {
            caption += "new";
        } else {
            for (Field field : entity.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    try {
                        caption += field.get(entity).toString();
                    } catch (IllegalAccessException e) {
                        logger.error(e.getMessage());
                    }
                    break;
                }
            }
        }
        return caption;
    }

    @Override
    public <T> void refreshElementCaption(T entity, String cachedName) throws NoSuchFieldException, IllegalAccessException {
        AgiUI agiUI = entity.getClass().getAnnotation(AgiUI.class);
        if (agiUI == null) throw new RuntimeException("Expected annotation is absent");
        final String newCachedName = generateElementCacheName(agiUI, entity);
        TabSheet.Tab tab = openedForms.get(cachedName);
        if (tab == null) {
            logger.warn("tab not found for cached name: " + cachedName);
            return;
        }
        tab.setCaption(generateElementCaption(entity, false));
        openedForms.remove(cachedName);
        openedForms.put(newCachedName, tab);

    }

    protected <T> String generateElementCacheName(AgiUI agiUI, T entity) throws NoSuchFieldException, IllegalAccessException {
        Field idField = entity.getClass().getDeclaredField(agiUI.idFieldName());
        idField.setAccessible(true);
        String nameAddition = String.valueOf(idField.get(entity));
        idField.setAccessible(false);
        return DEFAULT_ELEMENT_FORM_PREFIX + entity.getClass().getCanonicalName() + nameAddition;
    }

}
