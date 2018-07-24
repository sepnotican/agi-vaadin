package agi.core.form;

import com.vaadin.ui.Layout;

import javax.annotation.PostConstruct;

public interface IUIHandler {
    @PostConstruct
    void init();

    MainMenuGenerator getMainMenuGenerator();

    MainFormHandler getMainFormHandler();

    Layout getMainLayout();
}
