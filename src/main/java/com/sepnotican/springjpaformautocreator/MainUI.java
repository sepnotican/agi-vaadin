package com.sepnotican.springjpaformautocreator;

import com.sepnotican.springjpaformautocreator.generator.form.UIHandler;
import com.sepnotican.springjpaformautocreator.repository.CustomerRepo;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@SpringUI
public class MainUI extends UI {

    private Layout mainLayout;

    @Autowired
    private ApplicationContext context;


    @Autowired
    private CustomerRepo customerRepo;

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        mainLayout = new VerticalLayout();
        mainLayout.addComponent(new Label("Hello ! 123"));

//        AbstractElementForm<Customer> abstractElementForm = new AbstractElementForm<>(customerRepo);
//        abstractElementForm.init(customer);
//        mainLayout.addComponent(abstractElementForm);

//        AbstractListForm<Customer, CustomerRepo> list = new AbstractListForm<>(Customer.class, customerRepo);
//        mainLayout.addComponent(list);
        UIHandler uiHandler = new UIHandler(mainLayout, context);

        setContent(mainLayout);
    }


}
