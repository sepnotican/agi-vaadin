package example.entity;

import agi.core.annotations.*;
import com.vaadin.icons.VaadinIcons;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
@AgiEntity(singleCaption = "Invoice", menuCaption = "Invoices", icon = VaadinIcons.INVOICE,
        menuPath = "/etc/",
        nameForInputSearch = "id")
public class Invoice {

    @Id
    @GeneratedValue
    @AgiDrawOrder(3)
    Long id;

    @LinkedObject
    @Synonym("Customer")
    @ManyToOne
    @JoinColumn(name = "customer_id")
    @Filtered
    Customer customer;


    @Synonym("Operator's comment")
    String comment;

    @Column
    @Picture
    @Lob
    byte[] photo;

    //goods
}
