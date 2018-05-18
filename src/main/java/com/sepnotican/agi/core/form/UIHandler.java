package com.sepnotican.agi.core.form;

import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class UIHandler implements IUIHandler {

    @Value("${agi.forms.main.title}")
    String mainPageTitle;

    @Autowired
    MainMenuGenerator mainMenuGenerator;

    @Autowired
    MainFormHandler mainFormHandler;

    public UIHandler() {
    }

    @Override
    @PostConstruct
    public void init() {
        mainFormHandler.getMainLayout().
                addComponent(new Label(mainPageTitle), 0);
    }

    @Override
    public MainMenuGenerator getMainMenuGenerator() {
        return mainMenuGenerator;
    }

    @Override
    public MainFormHandler getMainFormHandler() {
        return mainFormHandler;
    }

    @Override
    public Layout getMainLayout() {
        return mainFormHandler.getMainLayout();
    }
}
