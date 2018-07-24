package example.entity;

import agi.core.annotations.AgiDrawOrder;
import agi.core.annotations.AgiEntity;
import agi.core.annotations.LinkedObject;
import agi.core.annotations.Picture;
import agi.core.annotations.Synonym;
import com.vaadin.icons.VaadinIcons;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
@Data
@ToString
@AgiEntity(singleCaption = "Invoice", menuCaption = "Invoices", icon = VaadinIcons.INVOICE,
        menuPath = "/etc/",
        fieldForInputSearch = "id")
public class Invoice {

    @Id
    @GeneratedValue
    @AgiDrawOrder(3)
    Long id;

    @LinkedObject
    @Synonym("Customer")
    @ManyToOne
    @JoinColumn(name = "customer_id")
    Customer customer;


    @Synonym("Operator's comment")
    String comment;

    @Column
    @Picture
    @Lob
    byte[] photo;

    //goods
}