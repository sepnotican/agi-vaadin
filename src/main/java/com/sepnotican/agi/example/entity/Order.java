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
import javax.persistence.Table;

@Entity
@Table(name = "orders")
@Data
@AgiUI(singleCaption = "Order", manyCaption = "Orders", icon = VaadinIcons.INVOICE,
        menuPath = "/etc/submenu/mysecond")
public class Order {


    @Id
    @GeneratedValue
    @AgiDrawOrder(4)
    Long id;

    @LinkedObject
    @Synonym("Customer")
    @ManyToOne
    @JoinColumn(name = "customer_id")
    @AgiDrawOrder(3)
    Customer customer;

    @Synonym("Operator's comment")
    String comment;

    //goods
}
