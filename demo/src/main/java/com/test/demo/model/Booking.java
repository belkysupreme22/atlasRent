package com.test.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.test.demo.BookingStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
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
    @Enumerated(EnumType.STRING)
    private BookingStatus status; // Use an enum for status (PENDING, APPROVED, REJECTED)


}

