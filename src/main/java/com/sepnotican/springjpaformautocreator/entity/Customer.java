package com.sepnotican.springjpaformautocreator.entity;

import com.sepnotican.springjpaformautocreator.EnumColor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Customer {

    @Id
    private Long id;

    @Column
    private String name;

    @Column
    private EnumColor color;

    public Customer() {
    }

    public Customer(long id, String name, EnumColor color) {
        this.id = id;
        this.name = name;
        this.color = color;
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
