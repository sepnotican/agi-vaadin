package com.sepnotican.springjpaformautocreator.entity;

import com.sepnotican.springjpaformautocreator.generator.annotations.GenerateUI;
import com.sepnotican.springjpaformautocreator.generator.annotations.Synonym;
import com.sepnotican.springjpaformautocreator.repository.TradeDealsRepo;

import javax.persistence.*;

@Entity
@Table(name = "trade_deals")
@GenerateUI(caption = "Trade deals", repo = TradeDealsRepo.class)
public class TradeDeal {

    @Column
    @Synonym("Summ of the deal")
    double sum;
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
}
