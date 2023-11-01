package com.test.demo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingDto {
    private Long productId;
    private LocalDate startDate;
    private LocalDate endDate;

}

