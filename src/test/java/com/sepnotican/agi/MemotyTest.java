package com.sepnotican.agi;

import com.sepnotican.agi.core.utils.GenericDao;
import com.sepnotican.agi.example.entity.Customer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class MemotyTest {

    @Autowired
    ApplicationContext context;

    @Test
    public void test1() {
        for (int i = 0; i < 1000000; i++) {
            context.getBean(GenericDao.class, Customer.class);
//            Thread.sleep(1);
            if (i % 1000 == 0) {
                System.out.println("construct=" + GenericDao.q);
                System.out.println("finalized=" + GenericDao.w);
            }
        }

    }

}
