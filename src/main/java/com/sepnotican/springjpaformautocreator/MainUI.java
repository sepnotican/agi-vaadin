package com.sepnotican.springjpaformautocreator;

import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SpringUI
public class MainUI extends UI {

    private Layout mainLayout;

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        mainLayout = new VerticalLayout();
        mainLayout.addComponent(new Label("Hello ! 123"));
        setContent(mainLayout);
    }


}
