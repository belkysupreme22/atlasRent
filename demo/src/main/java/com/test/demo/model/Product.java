package com.test.demo.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Optional;

@Getter
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;
    private String location;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private String status;
    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private Category category;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private UserEntity owner;

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
