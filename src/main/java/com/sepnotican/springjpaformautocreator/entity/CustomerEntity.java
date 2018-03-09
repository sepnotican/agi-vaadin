package com.sepnotican.springjpaformautocreator.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CustomerEntity {

    @Id
    private long id;

    private String name;

    public CustomerEntity() {
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
}
