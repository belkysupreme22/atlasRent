package com.test.demo.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "product_status")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
