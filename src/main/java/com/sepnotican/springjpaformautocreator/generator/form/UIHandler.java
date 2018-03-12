package com.sepnotican.springjpaformautocreator.generator.form;

import com.sepnotican.springjpaformautocreator.EnumColor;
import com.sepnotican.springjpaformautocreator.entity.Customer;
import com.sepnotican.springjpaformautocreator.entity.TradeDeal;
import com.sepnotican.springjpaformautocreator.repository.CustomerRepo;
import com.sepnotican.springjpaformautocreator.repository.TradeDealsRepo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Layout;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UIHandler {
    MainMenuGenerator mainMenuGenerator;
    MainFormContainer mainFormContainer;
    Layout mainLayout;
    ApplicationContext context;

    public UIHandler(Layout mainLayout, ApplicationContext context) {
        this.mainLayout = mainLayout;
        this.context = context;

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
        mainLayout.addComponent(btnPopulateDB);
        // =========================================
        //DEBUG todo remove

        mainFormContainer = new MainFormContainer(this, mainLayout);
        mainMenuGenerator = new MainMenuGenerator(this, mainLayout, mainFormContainer);

        mainMenuGenerator.init(new String[]{"com.sepnotican.springjpaformautocreator.entity"}, context);
        mainFormContainer.init();

    }
}
