package com.sepnotican.agi.example.entity;

import com.sepnotican.agi.core.annotations.AgiDrawOrder;
import com.sepnotican.agi.core.annotations.AgiUI;
import com.sepnotican.agi.core.annotations.BigString;
import com.sepnotican.agi.core.annotations.RepresentationResolver;
import com.sepnotican.agi.core.annotations.Synonym;
import com.sepnotican.agi.example.EnumColor;
import com.sepnotican.agi.example.repository.CustomerRepo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "customer")
@AgiUI(listCaption = "Our Customers ",
        entityCaption = "Customer",
        repo = CustomerRepo.class,
        idFieldName = "id")
@RepresentationResolver("fullname")
@Getter
@Setter
@ToString(exclude = "tradeDeals")
@NoArgsConstructor
@AllArgsConstructor
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

    @RepresentationResolver("fullname")
    public String getFullName() {
        return this.id + ":" + this.name;
    }

}