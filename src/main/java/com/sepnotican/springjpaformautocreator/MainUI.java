package com.sepnotican.springjpaformautocreator;

import com.sepnotican.springjpaformautocreator.entity.Customer;
import com.sepnotican.springjpaformautocreator.generator.form.element.AbstractElementForm;
import com.sepnotican.springjpaformautocreator.repository.CustomerRepo;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;

@SpringUI
public class MainUI extends UI {

    private Layout mainLayout;

    @Autowired
    private AbstractElementForm<Customer> abstractElementForm;

    @Autowired
    private CustomerRepo customerRepo;

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        mainLayout = new VerticalLayout();
        mainLayout.addComponent(new Label("Hello ! 123"));
        Customer customer = customerRepo.findById(1L).get();

        abstractElementForm.init(customer);

        mainLayout.addComponent(abstractElementForm);
        setContent(mainLayout);

        new Thread(() -> {
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.err.println(customer);
        }).start();
    }


}
