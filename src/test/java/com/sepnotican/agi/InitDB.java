package com.sepnotican.agi;

import com.sepnotican.agi.example.ClientLevel;
import com.sepnotican.agi.example.entity.Customer;
import com.sepnotican.agi.example.entity.TradeDeal;
import com.sepnotican.agi.example.repository.CustomerRepo;
import com.sepnotican.agi.example.repository.TradeDealsRepo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class InitDB {

    @Autowired
    ApplicationContext context;

    @Test
    public void fillDB() {
        Customer c1 = new Customer(null, "Vasiliy", "descrip ion", ClientLevel.BLACK, null);
        Customer c2 = new Customer(null, "Semen", "descrip ion", ClientLevel.GREEN, null);
        Customer c3 = new Customer(null, "Pavel petrovi4", "descrip ion", ClientLevel.RED, null);
        Customer c4 = new Customer(null, "Dudka", "descrip ion", ClientLevel.WHITE, null);
        Customer c5 = new Customer(null, "Utka", "descrip ion", ClientLevel.BLUE, null);
        Customer c6 = new Customer(null, "Putka", "descrip ion", ClientLevel.BLACK, null);

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
    }

}
