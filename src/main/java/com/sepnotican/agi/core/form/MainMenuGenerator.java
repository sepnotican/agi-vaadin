package com.sepnotican.agi.core.form;


import com.google.common.collect.Lists;
import com.sepnotican.agi.core.annotations.AgiUI;
import com.sepnotican.agi.core.form.util.EntityNamesResolver;
import com.vaadin.ui.MenuBar;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Set;

@Component
@Scope("prototype")
public class MainMenuGenerator {

    @Value("${com.sepnotican.agi-package}")
    public String[] packagesToScan;
    @Autowired
    ApplicationContext context;
    @Autowired
    EntityNamesResolver namesResolver;

    private IFormHandler listFormHandler;
    private MenuBar menuBar = new MenuBar();

    public MainMenuGenerator(IFormHandler listFormHandler) {
        this.listFormHandler = listFormHandler;
    }

    @PostConstruct
    public void init() {

        for (String prefix : packagesToScan) {

            Reflections reflections = new Reflections(prefix);
            Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(AgiUI.class);

            annotated.stream().sorted((c1, c2) -> {
                String name1 = c1.getAnnotation(AgiUI.class).manyCaption();
                String name2 = c2.getAnnotation(AgiUI.class).manyCaption();
                return name1.compareTo(name2);
            }).forEach(aClass -> {

                String menuPath = aClass.getAnnotation(AgiUI.class).menuPath();
                if (menuPath.equals("")) {
                    menuBar.addItem(namesResolver.getManyName(aClass)
                            , aClass.getAnnotation(AgiUI.class).icon(),
                            event -> listFormHandler.showAbstractListForm(aClass));
                } else {
                    ArrayList<String> path = Lists.newArrayList(menuPath.split("/"));
                    path.removeIf(String::isEmpty);
                    MenuBar.MenuItem containerElement = findContainerElement(path);
                    containerElement.addItem(namesResolver.getManyName(aClass)
                            , aClass.getAnnotation(AgiUI.class).icon(),
                            event -> listFormHandler.showAbstractListForm(aClass));
                }

                listFormHandler.getMainLayout().addComponent(menuBar, 0);

            });
        }
    }

    private MenuBar.MenuItem findContainerElement(ArrayList<String> path) {

        String pathElement = path.get(0);

        for (MenuBar.MenuItem menuItem : menuBar.getItems()) {
            if (menuItem.getText().equals(pathElement)) {
                if (path.size() == 1) return menuItem;
                else {
                    path.remove(0);
                    return findContainerElement(path, menuItem);
                }
            }
        }

        MenuBar.MenuItem menuItem = menuBar.addItem(pathElement, null);
        if (path.size() == 1) return menuItem;
        else {
            path.remove(0);
            return findContainerElement(path, menuItem);
        }
    }

    private MenuBar.MenuItem findContainerElement(ArrayList<String> path, MenuBar.MenuItem child) {
        String pathElement = path.get(0);
        if (child.hasChildren()) {
            for (MenuBar.MenuItem menuItem : child.getChildren()) {
                if (menuItem.getText().equals(pathElement)) {
                    if (path.size() == 1) return menuItem;
                    else {
                        path.remove(0);
                        return findContainerElement(path, menuItem);
                    }
                }
            }
        }
        MenuBar.MenuItem menuItem = child.addItem(pathElement, null);
        if (path.size() == 1) return menuItem;
        else {
            path.remove(0);
            return findContainerElement(path, menuItem);
        }
    }
}



