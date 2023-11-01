package com.test.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "bookings")
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // Many bookings can be associated with one product
    @JoinColumn(name = "product_id") // Specifies the foreign key column in the bookings table
    private Product product;

    @JsonIgnore
    @ManyToOne // Many bookings can be associated with one user (the booker)
    @JoinColumn(name = "user_id") // Specifies the foreign key column in the bookings table
    private UserEntity booker;

    private LocalDate startDate;
    private LocalDate endDate;
    private double totalPrice;

}

