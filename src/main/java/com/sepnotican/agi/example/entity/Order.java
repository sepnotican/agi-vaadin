package com.sepnotican.agi.example.entity;

import com.google.common.base.Joiner;
import com.sepnotican.agi.core.annotations.AgiDrawOrder;
import com.sepnotican.agi.core.annotations.AgiEntity;
import com.sepnotican.agi.core.annotations.LinkedObject;
import com.sepnotican.agi.core.annotations.RepresentationResolver;
import com.sepnotican.agi.core.annotations.Synonym;
import com.vaadin.icons.VaadinIcons;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "orders")
@Data
@ToString
@AgiEntity(singleCaption = "Order", menuCaption = "Orders", icon = VaadinIcons.INVOICE,
        menuPath = "/etc/submenu/mysecond",
        fieldForInputSearch = "id")
@RepresentationResolver("getId")
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

    @LinkedObject
    @Synonym("Order linked")
    @ManyToOne
    @JoinColumn(name = "order_id")
    @AgiDrawOrder(3)
    Order orderFrom;

    @Synonym("Operator's comment")
    String comment;

    //goods

    public String getOrderRepresentation() {
        return Joiner.on(":").join(id, customer.getFullName(), comment);
    }
}
