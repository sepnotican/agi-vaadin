package com.sepnotican.springjpaformautocreator.generator.form;

import com.sepnotican.springjpaformautocreator.EnumColor;
import com.sepnotican.springjpaformautocreator.entity.Customer;
import com.sepnotican.springjpaformautocreator.entity.TradeDeal;
import com.sepnotican.springjpaformautocreator.repository.CustomerRepo;
import com.sepnotican.springjpaformautocreator.repository.TradeDealsRepo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class UIHandler {
    MainMenuGenerator mainMenuGenerator;

    @Autowired
    MainFormHandler mainFormHandler;


    @Autowired
    private ApplicationContext context;

    public UIHandler() {
    }

    @PostConstruct
    public void init() {

        //DEBUG todo remove
        // =========================================
        Button btnPopulateDB = new Button();
        btnPopulateDB.addClickListener((e) -> {


            Customer c1 = new Customer("Vasiliy", "descrip ion", EnumColor.BLACK, null);
            Customer c2 = new Customer("Semen", "descrip ion", EnumColor.GREEN, null);
            Customer c3 = new Customer("Pavel petrovi4", "descrip ion", EnumColor.RED, null);
            Customer c4 = new Customer("Dudka", "descrip ion", EnumColor.WHITE, null);
            Customer c5 = new Customer("Utka", "descrip ion", EnumColor.BLUE, null);
            Customer c6 = new Customer("Putka", "descrip ion", EnumColor.BLACK, null);

            Set<TradeDeal> dealSet1 = new HashSet<>();
            dealSet1.add(new TradeDeal(1000d, c1));
            dealSet1.add(new TradeDeal(34500d, c1));
            dealSet1.add(new TradeDeal(500d, c1));
            c1.setTradeDeals(dealSet1);

            Set<TradeDeal> dealSet2 = new HashSet<>();
            dealSet1.add(new TradeDeal(10000d, c2));
            dealSet1.add(new TradeDeal(300d, c2));
            dealSet1.add(new TradeDeal(5700d, c2));
            c2.setTradeDeals(dealSet2);

            List<Customer> l1 = Arrays.asList(c1, c2, c3, c4, c5, c6);
            context.getBean(CustomerRepo.class).saveAll(l1);
            context.getBean(TradeDealsRepo.class).saveAll(dealSet1);
            context.getBean(TradeDealsRepo.class).saveAll(dealSet2);


        });

        mainFormHandler.getMainLayout().addComponent(new Label("Auto Generated Interface for Vaadin - example"));
        mainFormHandler.getMainLayout().addComponent(btnPopulateDB);
        // =========================================
        //DEBUG todo remove

        mainMenuGenerator = new MainMenuGenerator(this, mainFormHandler.getMainLayout(), mainFormHandler);

        mainMenuGenerator.init(new String[]{"com.sepnotican.springjpaformautocreator.entity"}, context);
        mainFormHandler.init();
    }

    public MainMenuGenerator getMainMenuGenerator() {
        return mainMenuGenerator;
    }

    public MainFormHandler getMainFormHandler() {
        return mainFormHandler;
    }

    public Layout getMainLayout() {
        return mainFormHandler.getMainLayout();
    }
}
