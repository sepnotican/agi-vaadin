package com.sepnotican.agi.generator.form;


import com.sepnotican.agi.generator.annotations.AgiUI;
import com.vaadin.ui.MenuBar;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.Set;

@Component
public class MainMenuGenerator {

    @Value("#{environment['com.sepnotican.agi-package']}")
    public String[] packagesToScan;
    @Autowired
    ApplicationContext context;
    @Autowired
    private IFormHandler listFormHandler;
    private MenuBar menuBar = new MenuBar();

    public MainMenuGenerator() {
    }

    @PostConstruct
    public void init() {

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
                listFormHandler.getMainLayout().addComponent(menuBar, 0);

            });
        }
    }
}



