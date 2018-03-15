package com.sepnotican.agi.generator.form;


import com.sepnotican.agi.generator.annotations.AgiUI;
import com.vaadin.ui.Layout;
import com.vaadin.ui.MenuBar;
import org.reflections.Reflections;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Comparator;
import java.util.Set;

public class MainMenuGenerator {

    private MenuBar menuBar = new MenuBar();
    private Layout layoutForMenu;
    private IFormHandler listFormHandler;
    private UIHandler uiHandler;

    public MainMenuGenerator(UIHandler uiHandler, Layout layoutForMenu, IFormHandler listFormHandler) {
        this.layoutForMenu = layoutForMenu;
        this.uiHandler = uiHandler;
        this.listFormHandler = listFormHandler;
    }

    public void init(String[] packagesToScan, ApplicationContext context) {

        for (String prefix : packagesToScan) {

            Reflections reflections = new Reflections(prefix);
            Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(AgiUI.class);

            annotated.stream().sorted(new Comparator<Class<?>>() {
                @Override
                public int compare(Class<?> o1, Class<?> o2) {
                    String name1 = o1.getAnnotation(AgiUI.class).listCaption();
                    String name2 = o2.getAnnotation(AgiUI.class).listCaption();
                    return name1.compareTo(name2);
                }
            }).forEach(aClass -> {
                AgiUI agiUI = aClass.getAnnotation(AgiUI.class);
                Class repositoryClass = agiUI.repo();
                JpaRepository repository = (JpaRepository) context.getBean(repositoryClass);

                MenuBar.MenuItem item = menuBar.addItem(agiUI.listCaption(), agiUI.icon(),
                        event -> listFormHandler.showAbstractListForm(aClass, repository));
                layoutForMenu.addComponent(menuBar);
            });
        }
    }
}


