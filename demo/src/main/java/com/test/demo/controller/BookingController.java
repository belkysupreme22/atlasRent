package com.test.demo.controller;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "localhost:3000")
@RestController
@RequestMapping("/api/v1")
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
    @GetMapping("/renter-bookings")
    public ResponseEntity<BookingResponse> getMyProducts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam int pageSize
    ) {
        if (userDetails != null) {
            String currentUsername = userDetails.getUsername();
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            Page<Booking> bookings = bookingService.getBookingsByUsernamePaged(currentUsername, pageable);

            List<Booking> myBookings = bookings.getContent();
            BookingResponse bookingResponse = new BookingResponse();
            bookingResponse.setContent(myBookings);
            bookingResponse.setPageNo(pageNo);
            bookingResponse.setPageSize(pageSize);
            bookingResponse.setTotalPages(bookings.getTotalPages());
            bookingResponse.setTotalElements(bookings.getTotalElements());
            bookingResponse.setLast(bookings.isLast());

            return ResponseEntity.ok(bookingResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/bookings-for-owned-products")
    public ResponseEntity<BookingResponse> getBookingsForOwnedProducts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam int pageSize
    ) {
        if (userDetails != null ) {
            String ownerUsername = userDetails.getUsername();
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            Page<Booking> bookings = bookingService.getBookingsForOwnedProductsPaged(ownerUsername, pageable);

            BookingResponse bookingResponse = new BookingResponse();
            bookingResponse.setContent(bookings.getContent());
            bookingResponse.setPageNo(pageNo);
            bookingResponse.setPageSize(pageSize);
            bookingResponse.setTotalPages(bookings.getTotalPages());
            bookingResponse.setTotalElements(bookings.getTotalElements());
            bookingResponse.setLast(bookings.isLast());

            return ResponseEntity.ok(bookingResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/{bookingId}/approve")
    public ResponseEntity<String> approveBooking(@PathVariable Long bookingId) {
        bookingService.approveBooking(bookingId);
        return ResponseEntity.ok("Booking approved, and product marked as rented.");
    }

}


