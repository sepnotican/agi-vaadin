package example.entity;

import agi.core.annotations.*;
import com.google.common.base.Joiner;
import com.vaadin.icons.VaadinIcons;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "orders")
@Data
@ToString
@AgiEntity(singleCaption = "Order", menuCaption = "Orders", icon = VaadinIcons.INVOICE,
        menuPath = "/etc/submenu/mysecond",
        nameForInputSearch = "id")
@RepresentationResolver("getOrderRepresentation")
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
    @Filtered
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
