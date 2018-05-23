package com.sepnotican.agi.example.entity;

import com.sepnotican.agi.core.annotations.AgiColumnValueProvider;
import com.sepnotican.agi.core.annotations.AgiDrawOrder;
import com.sepnotican.agi.core.annotations.AgiEntity;
import com.sepnotican.agi.core.annotations.BigString;
import com.sepnotican.agi.core.annotations.RepresentationResolver;
import com.sepnotican.agi.core.annotations.Synonym;
import com.sepnotican.agi.example.EnumColor;
import com.vaadin.data.ValueProvider;
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
@AgiEntity(manyCaption = "Our Customers ",
        singleCaption = "Customer",
        fieldForInputSearch = "name",
        menuPath = "/Trade")
@RepresentationResolver("getFullName")
@Getter
@Setter
@ToString(exclude = "tradeDeals")
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue
    @Synonym("Identifier")
    @AgiDrawOrder(value = -1)
    private Long id;

    @Column
    @Synonym("Customer caption")
    @AgiDrawOrder(value = 2)
    private String name;

    @Column
    @BigString
    @Synonym("Customer description")
    private String description;

    @Column
    @Synonym("Color")
    @AgiDrawOrder(value = 5)
    private EnumColor color;

    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<TradeDeal> tradeDeals;

    @AgiColumnValueProvider("countOfTradeDeals")
    @Synonym("Count of trade deals with the client")
    @AgiDrawOrder(100)
    public static ValueProvider<Customer, String> countOfDeals() {
        return (ValueProvider<Customer, String>) anObject -> String.valueOf(anObject.tradeDeals.size());
    }

    public String getFullName() {
        return this.name + ':' + this.id;
    }

}
