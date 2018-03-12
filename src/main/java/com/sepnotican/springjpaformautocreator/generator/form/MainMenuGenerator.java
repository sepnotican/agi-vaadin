package com.sepnotican.springjpaformautocreator.generator.form;


import com.sepnotican.springjpaformautocreator.generator.annotations.GenerateUI;
import com.vaadin.ui.Layout;
import com.vaadin.ui.MenuBar;
import org.reflections.Reflections;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;

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
            Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(GenerateUI.class);

            for (Class<?> aClass : annotated) {
                GenerateUI generateUI = aClass.getAnnotation(GenerateUI.class);
                Class repositoryClass = generateUI.repo();
                JpaRepository repository = (JpaRepository) context.getBean(repositoryClass);

                MenuBar.MenuItem item = menuBar.addItem(generateUI.listCaption(), generateUI.icon(),
                        event -> listFormHandler.showAbstractListForm(aClass, repository));
            }

        }

        layoutForMenu.addComponent(menuBar);
    }

}
