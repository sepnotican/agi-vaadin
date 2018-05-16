package com.sepnotican.agi.example.entity;

import com.sepnotican.agi.core.annotations.AgiUI;
import com.sepnotican.agi.core.annotations.LinkedObject;
import com.sepnotican.agi.core.annotations.Synonym;
import com.sepnotican.agi.example.repository.TradeDealsRepo;

import javax.persistence.*;

@Entity
@Table(name = "trade_deals")
@AgiUI(listCaption = "Trade deals",
        entityCaption = "Trade deal",
        repo = TradeDealsRepo.class,
        idFieldName = "id")
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

    public TradeDeal() {
    }

    public TradeDeal(double sum, Customer customer) {

        this.sum = sum;
        this.customer = customer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "TradeDeal{" +
                "id=" + id +
                ", C=" + customer +
                ", $=" + sum +
                '}';
    }
}
