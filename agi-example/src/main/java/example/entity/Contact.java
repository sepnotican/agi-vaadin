package example.entity;

import agi.core.annotations.*;
import com.vaadin.icons.VaadinIcons;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@AgiEntity(menuPath = "/etc", icon = VaadinIcons.CONNECT,
        singleCaption = "contact", menuCaption = "Contacts",
        nameForInputSearch = "id")
public class Contact {

    @Id
    @GeneratedValue
    @AgiDrawOrder(3)
    @Filtered
    Long id;

    @Synonym("Operator's comment")
    @BigString
    @AgiDrawOrder(1)
    @Filtered
    String comment;

    @LinkedObject
    @Synonym("Customer")
    @ManyToOne
    @JoinColumn(name = "customer_id")
    @AgiDrawOrder(-2)
    @Filtered
    Customer customer;


}
