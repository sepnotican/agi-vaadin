package example.entity;

import agi.core.annotations.AgiDrawOrder;
import agi.core.annotations.AgiEntity;
import agi.core.annotations.BigString;
import agi.core.annotations.LinkedObject;
import agi.core.annotations.Synonym;
import com.vaadin.icons.VaadinIcons;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Data
@AgiEntity(menuPath = "/etc", icon = VaadinIcons.CONNECT,
        singleCaption = "contact", menuCaption = "Contacts",
        fieldForInputSearch = "id")
public class Contact {

    @Id
    @GeneratedValue
    @AgiDrawOrder(3)
    Long id;

    @Synonym("Operator's comment")
    @BigString
    @AgiDrawOrder(1)
    String comment;

    @LinkedObject
    @Synonym("Customer")
    @ManyToOne
    @JoinColumn(name = "customer_id")
    @AgiDrawOrder(-2)
    Customer customer;


}
