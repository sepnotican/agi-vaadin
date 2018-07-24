package agi.core.form;

import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Scope("prototype")
public class UIHandler implements IUIHandler {

    @Value("${agi.forms.main.title}")
    String mainPageTitle;

    @Autowired
    ApplicationContext context;

    MainMenuGenerator mainMenuGenerator;

    @Autowired
    MainFormHandler mainFormHandler;

    public UIHandler() {
    }

    @Override
    @PostConstruct
    public void init() {
        context.getBean(MainMenuGenerator.class, mainFormHandler);
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
