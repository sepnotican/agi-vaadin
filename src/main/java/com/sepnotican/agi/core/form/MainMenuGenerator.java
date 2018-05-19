package com.sepnotican.agi.core.form;


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
import java.util.Comparator;
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

            annotated.stream().sorted(new Comparator<Class<?>>() {
                @Override
                public int compare(Class<?> c1, Class<?> c2) {
                    String name1 = c1.getAnnotation(AgiUI.class).manyCaption();
                    String name2 = c2.getAnnotation(AgiUI.class).manyCaption();
                    return name1.compareTo(name2);
                }
            }).forEach(aClass -> {
                MenuBar.MenuItem item = menuBar.addItem(namesResolver.getManyName(aClass)
                        , aClass.getAnnotation(AgiUI.class).icon(),
                        event -> listFormHandler.showAbstractListForm(aClass));
                listFormHandler.getMainLayout().addComponent(menuBar, 0);

            });
        }
    }
}



