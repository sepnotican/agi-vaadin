package com.sepnotican.agi;

import com.sepnotican.agi.core.form.UIHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;

@SpringUI
public class MainUI extends UI {

    @Autowired
    private UIHandler uiHandler;

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        setContent(uiHandler.getMainLayout());
        setSizeFull();
    }


}
