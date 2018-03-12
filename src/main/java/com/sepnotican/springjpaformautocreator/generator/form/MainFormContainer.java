package com.sepnotican.springjpaformautocreator.generator.form;

import com.sepnotican.springjpaformautocreator.generator.annotations.GenerateUI;
import com.sepnotican.springjpaformautocreator.generator.form.generic.AbstractListForm;
import com.vaadin.ui.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.HashMap;
import java.util.Map;

public class MainFormContainer extends VerticalLayout implements IFormHandler {

    private static final String DEFAULT_FORM_PREFIX = "DEF_LIST_";
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
        final String fromCacheName = DEFAULT_FORM_PREFIX + aClass.getCanonicalName();

        TabSheet.Tab tab;
        tab = openedForms.get(fromCacheName);
        if (tab != null) {
            tabSheet.focus();
            tab.setVisible(true);
            return;
        }

        AbstractListForm<T, JpaRepository> listForm = new AbstractListForm<>(aClass, jpaRepository);
        tab = tabSheet.addTab(listForm);
        tab.setCaption(aClass.getAnnotation(GenerateUI.class).caption());
        tab.setIcon(aClass.getAnnotation(GenerateUI.class).icon());

        tab.setVisible(true);
        tab.setClosable(true);

        openedForms.put(fromCacheName, tab);
    }

}
