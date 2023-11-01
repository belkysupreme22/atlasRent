package com.test.demo.repository;

import com.test.demo.model.Booking;
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
    Page<Booking> findByBooker_Username(String username, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.product.owner.username = :ownerUsername")
    Page<Booking> findBookingsForOwnedProducts(@Param("ownerUsername") String ownerUsername, Pageable pageable);

}


