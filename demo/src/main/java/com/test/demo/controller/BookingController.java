package com.test.demo.controller;

import com.test.demo.BookingStatus;
import com.test.demo.dto.BookingDto;
import com.test.demo.dto.BookingResponse;
import com.test.demo.dto.ProductResponse;
import com.test.demo.model.Booking;
import com.test.demo.model.Product;
import com.test.demo.model.UserEntity;
import com.test.demo.repository.ProductRepository;
import com.test.demo.repository.UserRepository;
import com.test.demo.service.BookingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "localhost:3000")
@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final BookingService bookingService;

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/create-booking")
    public ResponseEntity<String> createBooking(
            @RequestBody BookingDto bookingDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails != null) {
            UserEntity booker = userRepository.findByUsername(userDetails.getUsername()).orElse(null);

            if (booker != null) {
                Product product = productRepository.findById(bookingDto.getProductId()).orElse(null);

                if (product != null) {
                    Booking booking = new Booking();
                    booking.setProduct(product);
                    booking.setBooker(booker);
                    booking.setStartDate(bookingDto.getStartDate());
                    booking.setEndDate(bookingDto.getEndDate());

                    Booking createdBooking = bookingService.createBooking(booking);
                    return new ResponseEntity<>("Booking created successfully!", HttpStatus.OK);
                }
            }
        }

        return new ResponseEntity<>("Failed to create booking!", HttpStatus.BAD_REQUEST);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/approve/{bookingId}")
    public ResponseEntity<String> approveBooking(@PathVariable Long bookingId) {
        bookingService.approveBooking(bookingId);
        return ResponseEntity.ok("Booking approved");
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/reject/{bookingId}")
    public ResponseEntity<String> rejectBooking(@PathVariable Long bookingId) {
        bookingService.rejectBooking(bookingId);
        return ResponseEntity.ok("Booking rejected");
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/owner-bookings")
    public ResponseEntity<List<Booking>> getBookingsForOwnedProducts(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            UserEntity owner = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
            if (owner != null) {
                List<Booking> bookings = bookingService.getBookingsForOwnedProducts(owner);

                return new ResponseEntity<>(bookings, HttpStatus.OK);
            }
        }

        // Return an empty list if there are no bookings
        return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/renter-bookings")
    public ResponseEntity<List<Booking>> getBookingsForRentee(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            UserEntity booker = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
            if (booker != null) {
                List<Booking> bookings = bookingService.getBookingsForRentee(booker);

                return new ResponseEntity<>(bookings, HttpStatus.OK);
            }
        }

        // Return an empty list if there are no bookings
        return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/renter-rentals")
    public ResponseEntity<List<Booking>> getRentingsForRentee(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            UserEntity booker = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
            if (booker != null) {
                List<Booking> bookings = bookingService.getRentingsForRentee(booker);

                return new ResponseEntity<>(bookings, HttpStatus.OK);
            }
        }

        // Return an empty list if there are no bookings
        return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
    }
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/owner-rentals")
    public ResponseEntity<List<Booking>> getRentalsForOwnedProducts(@AuthenticationPrincipal
                                                                        UserDetails user) {
        if (user != null) {
            UserEntity owner = userRepository.findByUsername(user.getUsername()).orElse(null);
            if (owner != null) {
                List<Booking> rentals = bookingService.getRentingsForOwnedProducts(owner);

                return new ResponseEntity<>(rentals, HttpStatus.OK);
            }
        }

        // Return an empty list if there are no bookings
        return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<String> deleteBooking(@PathVariable Long bookingId) {
        try {
            bookingService.deleteBookingById(bookingId);
            return ResponseEntity.ok("Booking deleted successfully");
        } catch (EmptyResultDataAccessException ex) {
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the booking");
        }
    }



}


