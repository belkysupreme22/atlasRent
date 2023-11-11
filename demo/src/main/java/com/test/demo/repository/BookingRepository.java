package com.test.demo.repository;

import com.test.demo.BookingStatus;
import com.test.demo.model.Booking;
import com.test.demo.model.Product;
import com.test.demo.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByProduct(Product product);

    List<Booking> findByBookerAndStatus(UserEntity renter, BookingStatus bookingStatus);
}



