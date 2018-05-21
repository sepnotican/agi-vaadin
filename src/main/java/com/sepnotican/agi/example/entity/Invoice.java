package com.sepnotican.agi.example.entity;

import com.sepnotican.agi.core.annotations.AgiDrawOrder;
import com.sepnotican.agi.core.annotations.AgiUI;
import com.sepnotican.agi.core.annotations.LinkedObject;
import com.sepnotican.agi.core.annotations.Synonym;
import com.vaadin.icons.VaadinIcons;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Data
@AgiUI(singleCaption = "Invoice", manyCaption = "Invoices", icon = VaadinIcons.INVOICE,
        menuPath = "/etc/submenu/mysecond/third")
public class Invoice {

    @Id
    @GeneratedValue
    @AgiDrawOrder(3)
    Long id;

    @LinkedObject
    @Synonym("Customer")
    @ManyToOne
    @JoinColumn(name = "customer_id")
    Customer customer;


    @Synonym("Operator's comment")
    String comment;

    //goods
}
