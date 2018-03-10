package com.sepnotican.springjpaformautocreator.entity;

import com.sepnotican.springjpaformautocreator.EnumColor;
import com.sepnotican.springjpaformautocreator.generator.annotations.BigString;
import com.sepnotican.springjpaformautocreator.generator.annotations.Synonym;
import com.sepnotican.springjpaformautocreator.generator.annotations.UIDrawOrder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Customer {

    @Id
    @Synonym("Identifier")
    @UIDrawOrder(row = 1, rowStretch = 1, column = 0, columnStretch = 2)
    private Long id;

    @Column
    @Synonym("Customer name")
    @UIDrawOrder(row = 5, rowStretch = 5, column = 0, columnStretch = 0)
    private String name;

    @Column
    @BigString
    @UIDrawOrder(row = 2, rowStretch = 4, column = 0, columnStretch = 1)
    private String description;

    @Column
    @Synonym("Color")
    @UIDrawOrder(row = 5, rowStretch = 5, column = 1, columnStretch = 1)
    private EnumColor color;

    public Customer() {
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
                ", name='" + name + '\'' +
                ", color=" + color +
                '}';
    }
}
