package com.sepnotican.agi.example.forms;

import com.sepnotican.agi.core.annotations.AgiForm;
import com.sepnotican.agi.core.annotations.Command;
import com.sepnotican.agi.core.annotations.LinkedObject;
import com.sepnotican.agi.core.annotations.Synonym;
import com.sepnotican.agi.example.entity.Customer;
import com.sepnotican.agi.example.entity.TradeDeal;
import com.sepnotican.agi.example.repository.TradeDealsRepo;
import com.vaadin.icons.VaadinIcons;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@AgiForm(caption = ActiveSessions.FORM_NAME, menuPath = "/Service")
@Data
public class ActiveSessions {
    public final static String FORM_NAME = "Active session";

    @Autowired
    private TradeDealsRepo tradeDealsRepo;

    @LinkedObject
    Customer customer;
    @Synonym("free text")
    String text;
    @LinkedObject
    TradeDeal tradeDeal;

    @Command(caption = "Hellomoto", icon = VaadinIcons.VAADIN_V)
    public void sayHello() {
        com.vaadin.ui.Notification.show("HELLO FROM CMD!\n" + (customer == null ? "" : customer.getFullName()) + "\ntext is:" + text);
    }

    @Command(caption = "change something", icon = VaadinIcons.VAADIN_H)
    public void changeTradeDeal() {
        if (this.customer == null) return;
        this.tradeDeal = tradeDealsRepo.findFirst1ByCustomerOrderByIdDesc(customer).orElse(null);
    }
}
