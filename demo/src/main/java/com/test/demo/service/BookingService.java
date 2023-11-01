package com.test.demo.service;

import com.test.demo.exeptions.BookingNotFoundException;
import com.test.demo.model.Booking;
import com.test.demo.model.Product;
import com.test.demo.repository.BookingRepository;
import com.test.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    private final ProductRepository productRepository;

    public Booking createBooking(Booking booking) {
        // Calculate the total price based on the logic you provided
        long daysBetween = ChronoUnit.DAYS.between(booking.getStartDate(), booking.getEndDate());
        double productPricePerDay = booking.getProduct().getPrice();
        double totalPrice = productPricePerDay * daysBetween;
        booking.setTotalPrice(totalPrice);

        // Set the status to "booked" for the product
        Product product = booking.getProduct();
        product.setStatus("booked");

        // Update the product status and booker in the database
        productRepository.save(product);

        // Save the booking
        return bookingRepository.save(booking);
    }

    public Page<Booking> getBookingsByUsernamePaged(String username, Pageable pageable) {
        return bookingRepository.findByBooker_Username(username, pageable);
    }

    public Page<Booking> getBookingsForOwnedProductsPaged(String ownerUsername, Pageable pageable) {
        // Query the database to retrieve bookings for owned products with the "booked" status
        return bookingRepository.findBookingsForOwnedProducts(ownerUsername, pageable);
    }

    public void approveBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        // Get the associated product and update its status to "RENTED"
        Product product = booking.getProduct();
        product.setStatus("RENTED");
        productRepository.save(product);
    }


}

