package com.sepnotican.springjpaformautocreator.generator.form;


import com.sepnotican.springjpaformautocreator.generator.annotations.GenerateUI;
import com.vaadin.ui.Layout;
import com.vaadin.ui.MenuBar;
import org.reflections.Reflections;

import java.util.Set;

public class MainMenuGenerator {

    public void generateMenu(Layout layout, String[] packagesToScan) {

        for (String prefix : packagesToScan) {

            MenuBar menuBar = new MenuBar();

            Reflections reflections = new Reflections(prefix);
            Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(GenerateUI.class);

            for (Class<?> aClass : annotated) {
                GenerateUI generateUI = aClass.getAnnotation(GenerateUI.class);
                MenuBar.MenuItem item = menuBar.addItem(generateUI.name(), generateUI.icon(),
                        event -> createAbstractListForm(aClass));
            }

        }


    }

    private void createAbstractListForm(Class<?> aClass) {

    }

}
