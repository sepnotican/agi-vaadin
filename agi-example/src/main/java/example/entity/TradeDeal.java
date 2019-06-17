package example.entity;

import agi.core.annotations.AgiEntity;
import agi.core.annotations.LinkedObject;
import agi.core.annotations.RepresentationResolver;
import agi.core.annotations.Synonym;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "trade_deals")
@AgiEntity(menuCaption = "Trade deals",
        singleCaption = "Trade deal", nameForInputSearch = "id",
        menuPath = "/Trade")
@Getter
@Setter
@NoArgsConstructor
@RepresentationResolver("representation")
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

    public String representation() {
        return "" + getId() + ":" + getCustomer() + " $" + getSum();
    }

}
