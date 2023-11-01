package com.test.demo.service;

import com.test.demo.BookingStatus;
import com.test.demo.exeptions.BookingNotFoundException;
import com.test.demo.model.Booking;
import com.test.demo.model.Product;
import com.test.demo.model.Status;
import com.test.demo.model.UserEntity;
import com.test.demo.repository.BookingRepository;
import com.test.demo.repository.ProductRepository;
import com.test.demo.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    private final ProductRepository productRepository;
    private final StatusRepository statusRepository;
    public Booking createBooking(Booking booking) {
        // Calculate total price
        long daysBetween = ChronoUnit.DAYS.between(booking.getStartDate(), booking.getEndDate());
        double productPricePerDay = booking.getProduct().getPrice();
        double totalPrice = productPricePerDay * daysBetween;
        booking.setTotalPrice(totalPrice);

        // Set the status to PENDING
        booking.setStatus(BookingStatus.PENDING);

        // Update the product's status to BOOKED
        Product product = booking.getProduct();
        product.setStatus("BOOKED");
        productRepository.save(product);

        // Save the booking
        return bookingRepository.save(booking);
    }

    public void approveBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));

        // Perform additional checks here, e.g., to ensure only owners can approve
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        // Update the product's status to RENTED (or any desired status)
        Product product = booking.getProduct();
        product.setStatus("RENTED");
        productRepository.save(product);
    }

    public void rejectBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));

        // Perform additional checks here, e.g., to ensure only owners can reject
        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);

        // Update the product's status to PENDING (or any desired status)
        Product product = booking.getProduct();
        product.setStatus("PENDING");
        productRepository.save(product);
    }
    public List<Booking> getBookingsForOwnedProducts(UserEntity owner) {
        List<Booking> bookings = new ArrayList<>();
        if (owner != null) {
            // Retrieve the owner's products with a status of "BOOKED"
            List<Product> ownedProducts = productRepository.findByOwnerAndStatus(owner, "BOOKED");

            // For each owned product, retrieve its bookings
            for (Product product : ownedProducts) {
                List<Booking> productBookings = bookingRepository.findByProduct(product);

                // Filter the product bookings to get those with a status of "PENDING"
                List<Booking> relevantBookings = productBookings
                        .stream()
                        .filter(booking ->
                                BookingStatus.PENDING.equals(booking.getStatus())
                        )
                        .collect(Collectors.toList());

                bookings.addAll(relevantBookings);
            }
        }
        return bookings;
    }


    public List<Booking> getRentingsForOwnedProducts(UserEntity owner) {
        List<Booking> rentals = new ArrayList<>();
        if (owner != null) {
            // Retrieve the owner's products with a status of "BOOKED"
            List<Product> rentedProducts = productRepository.findByOwnerAndStatus(owner, "RENTED");

            // For each owned product, retrieve its bookings
            for (Product products : rentedProducts) {
                List<Booking> productBookings = bookingRepository.findByProduct(products);

                // Filter the product bookings to get those with a status of "PENDING"
                List<Booking> relevantBookings = productBookings
                        .stream()
                        .filter(booking ->
                                BookingStatus.APPROVED.equals(booking.getStatus())
                        )
                        .collect(Collectors.toList());

                rentals.addAll(relevantBookings);
            }
        }
        return rentals;
    }


    public ResponseEntity<String> deleteBookingById(Long bookingId) {
        // Find the booking by its ID
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);

        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();

            // Find the associated product
            Product product = booking.getProduct();

            Optional<Status> statusOptional = statusRepository.findByName("pending");

            if (statusOptional.isPresent()) {
                Status status = statusOptional.get();
                product.setStatus(status.getName());
            } else {
                // Handle the case where the status is not found
                return new ResponseEntity<>("Status not found in the database!", HttpStatus.BAD_REQUEST);
            }


            // Save the updated product
            productRepository.save(product);

            // Delete the booking
            bookingRepository.delete(booking);

            return ResponseEntity.ok("Booking deleted, and product status set to PENDING");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

