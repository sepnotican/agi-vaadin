package agi.core.form;


import agi.core.annotations.AgiEntity;
import agi.core.annotations.AgiForm;
import agi.core.form.util.EntityNamesResolver;
import agi.core.form.util.UIOrderComparator;
import com.google.common.collect.Lists;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.MenuBar;
import lombok.SneakyThrows;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
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

    private IFormHandler formHandler;
    private MenuBar menuBar = new MenuBar();

    public MainMenuGenerator(IFormHandler formHandler) {
        this.formHandler = formHandler;
    }

    @PostConstruct
    public void init() {

        System.out.println("=================================");
        System.out.println(packagesToScan.length);
        System.out.println("=================================");

        for (String prefix : packagesToScan) {

            Reflections reflections = new Reflections(prefix);
            Set<Class<?>> annotated = new HashSet<>();
            annotated.addAll(reflections.getTypesAnnotatedWith(AgiEntity.class));
            annotated.addAll(reflections.getTypesAnnotatedWith(AgiForm.class));


            annotated.stream().sorted(new UIOrderComparator()).forEach(aClass -> {

                String menuPath;
                if (aClass.isAnnotationPresent(AgiEntity.class)) {
                    menuPath = aClass.getAnnotation(AgiEntity.class).menuPath();
                } else {
                    menuPath = aClass.getAnnotation(AgiForm.class).menuPath();
                }
                VaadinIcons icon;
                if (aClass.isAnnotationPresent(AgiEntity.class)) {
                    icon = aClass.getAnnotation(AgiEntity.class).icon();
                } else {
                    icon = aClass.getAnnotation(AgiForm.class).icon();
                }
                if (menuPath.equals("")) {
                    menuBar.addItem(namesResolver.getMenuName(aClass), icon, getMenuCommand(aClass));
                } else {
                    ArrayList<String> path = Lists.newArrayList(menuPath.split("/"));
                    path.removeIf(String::isEmpty);
                    MenuBar.MenuItem containerElement = findContainerElement(path);
                    containerElement.addItem(namesResolver.getMenuName(aClass), icon, getMenuCommand(aClass));
                }

                formHandler.getMainLayout().addComponent(menuBar, 0);

            });
        }
    }

    private MenuBar.Command getMenuCommand(Class aClass) {
        return new MenuBar.Command() {
            @Override
            @SneakyThrows
            public void menuSelected(MenuBar.MenuItem event) {
                if (aClass.isAnnotationPresent(AgiEntity.class)) {
                    formHandler.showAbstractListForm(aClass);
                } else if (aClass.isAnnotationPresent(AgiForm.class)) {
                    formHandler.showAbstractElementForm(context.getBean(aClass), false);
                }
            }
        };
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



