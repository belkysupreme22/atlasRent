package com.test.demo.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@Entity
@Table(name = "products")
public class Product{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;
    private String location;
    @JsonIgnore
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private String status;

    //product-category association
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    //product-owner association
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity owner;

    //product-status association
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "product_status_mapping",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "status_id"))
    private List<Status> statuses;

    //product-booking association
    @JsonIgnore
    @OneToMany(mappedBy = "product")
    private List<Booking> bookings;


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
