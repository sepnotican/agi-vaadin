package example.entity;

import agi.core.annotations.*;
import example.ClientLevel;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "customer")
@AgiEntity(menuCaption = "Our Customers ",
        singleCaption = "Customer",
        nameForInputSearch = "name",
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
    @Filtered
    private Long id;

    @Column
    @Synonym("Customer caption")
    @AgiDrawOrder(value = 2)
    @Filtered
    private String name;

    @Column
    @BigString
    @Synonym("Customer description")
    private String description;

    @Column
    @Synonym("Client grade level")
    @AgiDrawOrder(value = 5)
    @Filtered
    private ClientLevel color;

    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<TradeDeal> tradeDeals;

//    @AgiColumnValueProvider
//    @Synonym("Count of trade deals with the client")
//    @AgiDrawOrder(100)
//    public static ValueProvider<Customer, String> countOfDeals() {
//        return (ValueProvider<Customer, String>) anObject -> String.valueOf(anObject.tradeDeals.size());
//    }

    public String getFullName() {
        return this.name + ':' + this.id;
    }

}
