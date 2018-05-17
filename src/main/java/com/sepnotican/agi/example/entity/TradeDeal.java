package com.sepnotican.agi.example.entity;

import com.sepnotican.agi.core.annotations.AgiUI;
import com.sepnotican.agi.core.annotations.LinkedObject;
import com.sepnotican.agi.core.annotations.Synonym;
import com.sepnotican.agi.example.repository.TradeDealsRepo;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "trade_deals")
@AgiUI(listCaption = "Trade deals",
        entityCaption = "Trade deal",
        repo = TradeDealsRepo.class,
        idFieldName = "id")
@Data
@NoArgsConstructor
public class TradeDeal {

    @Column
    @Synonym("Summ of the deal")
    double sum;
    @LinkedObject
    @Synonym("Customer")
    @ManyToOne
    @JoinColumn(name = "customer_id")
    Customer customer;
    @Id
    @GeneratedValue
    private Long id;

    public TradeDeal(double sum, Customer customer) {

        this.sum = sum;
        this.customer = customer;
    }

}
