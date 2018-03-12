package com.sepnotican.springjpaformautocreator.entity;

import com.sepnotican.springjpaformautocreator.EnumColor;
import com.sepnotican.springjpaformautocreator.generator.annotations.BigString;
import com.sepnotican.springjpaformautocreator.generator.annotations.GenerateUI;
import com.sepnotican.springjpaformautocreator.generator.annotations.Synonym;
import com.sepnotican.springjpaformautocreator.generator.annotations.UIDrawOrder;
import com.sepnotican.springjpaformautocreator.repository.CustomerRepo;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "customer")
@GenerateUI(caption = "Our Customers ", repo = CustomerRepo.class)
public class Customer {

    @Id
    @GeneratedValue
    @Synonym("Identifier")
    @UIDrawOrder(drawOrder = -1)
    private Long id;

    @Column
    @Synonym("Customer caption")
    @UIDrawOrder(drawOrder = 2)
    private String name;

    @Column
    @BigString
    @Synonym("Customer description")
    private String description;

    @Column
    @Synonym("Color")
    @UIDrawOrder(drawOrder = 5)
    private EnumColor color;

    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER)
    private Set<TradeDeal> tradeDeals;

    public Customer() {
    }

    public Customer(String name, String description, EnumColor color, Set<TradeDeal> tradeDeals) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.tradeDeals = tradeDeals;
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
