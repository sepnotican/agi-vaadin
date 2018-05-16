package com.sepnotican.agi.example.entity;

import com.sepnotican.agi.core.annotations.*;
import com.sepnotican.agi.example.EnumColor;
import com.sepnotican.agi.example.repository.CustomerRepo;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "customer")
@AgiUI(listCaption = "Our Customers ",
        entityCaption = "Customer",
        repo = CustomerRepo.class,
        idFieldName = "id")
@RepresentationResolver("fullname")
public class Customer {

    @Id
    @GeneratedValue
    @Synonym("Identifier")
    @AgiDrawOrder(drawOrder = -1)
    private Long id;

    @Column
    @Synonym("Customer caption")
    @AgiDrawOrder(drawOrder = 2)
    private String name;

    @Column
    @BigString
    @Synonym("Customer description")
    private String description;

    @Column
    @Synonym("Color")
    @AgiDrawOrder(drawOrder = 5)
    private EnumColor color;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<TradeDeal> tradeDeals;

    public Customer() {
    }

    public Customer(String name, String description, EnumColor color, Set<TradeDeal> tradeDeals) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.tradeDeals = tradeDeals;
    }

    @RepresentationResolver("fullname")
    public String getFullName() {
        return this.id + ":" + this.name;
    }

    public Set<TradeDeal> getTradeDeals() {
        return tradeDeals;
    }

    public void setTradeDeals(Set<TradeDeal> tradeDeals) {
        this.tradeDeals = tradeDeals;
    }

    public Customer(long id, String name, EnumColor color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EnumColor getColor() {
        return color;
    }

    public void setColor(EnumColor color) {
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", caption='" + name + '\'' +
                ", color=" + color +
                '}';
    }
}
